package com.example.annoncetp1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.navigation.NavigationView;

public class ActivityDisconnected extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    /**
     * @see drawerLayout
     * @see toggle
     * @see navigationView
     * Utilise pour le menu glissant.
     */
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;


    /**
     * Creation de l'activity informant a l'User que l'app n'arrive pas a ce connecter.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disconnected);

        // Setup tool bar.
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Setup menu glissant.
        drawerLayout = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,0,0);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = findViewById(R.id.activity_main_nav_view);


        // Boutton permettant de relancer l'app.
        Button retryButton = findViewById(R.id.retryButton);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ActivityDisconnected.this,MainActivity.class);
                Thread reload = new Thread() {
                    @Override
                    public void run() {
                        try {
                            super.run();
                        } catch (Exception e) {

                        } finally {
                            startActivity(i);
                            finish();
                        }
                    }
                };
                reload.start();
            }
        });
        navigationView.setNavigationItemSelectedListener(this);
    }

    /**
     * Applique un layout au menu.
     * @param menu
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_disconnected,menu);
        return true;
    }

    /**
     * Permet de capter sur quel item l'user as appuye dans le menu onTop.
     * @param menuItem Item selectionner
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        int id = menuItem.getItemId();

        if(id==R.id.Profil) {
            Intent intent = new Intent(getApplicationContext(), ActivityProfil.class);
            startActivity(intent);
        }
        return true;
    }

    /**
     * Permet de capter sur quel item l'user a appuye dans le menu glissant.
     * @param menuItem Item selectionner
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        Intent i = null;
        Log.i("discon",id + "");
        switch (id) {
            case R.id.drawer_profile :
                i = new Intent(ActivityDisconnected.this,ActivityProfil.class);
                startActivity(i);
                break;
            case R.id.drawer_annonceSave:
                i = new Intent(ActivityDisconnected.this,ActivityAnnonceSave.class);
                startActivity(i);
                break;
            default:
                break;

        }

        this.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Si appuie sur bouton retour et que le menu glissant est ouvert, on le ferme.
     * Sinon, comportement normal.
     */
    @Override
    public void onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }
}
