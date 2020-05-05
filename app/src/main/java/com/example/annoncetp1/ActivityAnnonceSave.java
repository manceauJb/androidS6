package com.example.annoncetp1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ActivityAnnonceSave extends AppCompatActivity {

    /**
     * @see list
     * Contient la liste d'annonce.
     */
    private List<Annonce> list;

    /**
     * @see annonceDb
     * Classe premettant de communiquer avec la base de donnee.
     */
    private AnnonceDb annonceDb;

    /**
     * Creation de l'activite permettant de lister les annonces sauvegardes.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annonce_save);
        findViewById(R.id.progressBarList).setVisibility(View.GONE);
        this.list = new ArrayList<>();
        this.annonceDb = new AnnonceDb(this);

        // Setup de la toolbar.
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Setup du RecyclerView.
        setupRecyclerView();
    }

    /**
     * On recharge la list si on reviens sur cette activity.
     */
    @Override
    protected void onResume() {
        super.onResume();
        setupRecyclerView();
    }

    /**
     * Applique un layout au menu,et ajoute le titre
     * @param menu
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_disconnected,menu);
        return true;
    }

    /**
     * Permet de capter sur quel item l'user a appuye.
     * @param menuItem Item selectionner
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        int id = menuItem.getItemId();

        if(id==R.id.Profil) {
            Intent intent = new Intent(getApplicationContext(), ActivityProfil.class);
            startActivity(intent);
        }else if(id==16908332){
            onBackPressed();
        }
        return true;
    }

    /**
     * Setup de la RecyclerView, qui contient les annonces.
     */
    public void setupRecyclerView(){

        // recuperation de la liste des annonces.
        this.list = annonceDb.getAllAnnonce();

        if(list!=null | list.size()!=0) {

            // list contient au moins une annonce.
            RecyclerView recyclerView = findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            // Init de l'adapter.
            AnnonceAdapter adapter = new AnnonceAdapter(this.list,true,false);
            recyclerView.setAdapter(adapter);

            // Permet d'ajouter une separation entre chaque annonce.
            recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        }
    }

}
