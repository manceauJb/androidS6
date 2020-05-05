package com.example.annoncetp1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ActivityListAnnonce extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private List<Annonce> list;
    private SearchView mSearchView;
    private SwipeRefreshLayout refreshLayout;
    private String searchSave;
    private Boolean recyclerSpace = false;
    private int ACTIVITY_AJOUTER = 2222;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_annonce);

        // Setup de la ToolBar.
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Setup du Menu.
        drawerLayout = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,0,0);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = findViewById(R.id.activity_main_nav_view);

        // Init des zone de text en haut du Menu pour le profil.
        View hView = navigationView.getHeaderView(0);
        TextView navPseudo = hView.findViewById(R.id.nav_pseudo);
        TextView navPhone = hView.findViewById(R.id.nav_phone);
        TextView navEmail = hView.findViewById(R.id.nav_email);

        // Recuperation du profil.
        SharedPreferences myPrefs = getSharedPreferences("profil",MODE_PRIVATE);
        String pseudo = myPrefs.getString("pseudo",null);
        String tel = myPrefs.getString("tel",null);
        String mail = myPrefs.getString("mail",null);

        // Affectation des zone de texte par les valeurs du profil, si profil rentre.
        if(pseudo != null | tel != null | mail != null){
            navPseudo.setText(pseudo);
            navPhone.setText(tel);
            navEmail.setText(mail);
        }else{
            // Pas de profil.
            Toast.makeText(this,"Pas de profil",Toast.LENGTH_SHORT).show();
        }

        navigationView.setNavigationItemSelectedListener(this);

        // Capture lors du refresh le la list.
        refreshLayout = findViewById(R.id.swipeRecycler);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setList(getSearchSave());
                refreshLayout.setRefreshing(false);
            }
        });

        // Affichage de la list.
        // null => pas d'affinage de recherche.
        setList(null);

        // Verifiaction de la permission au Storage.
        isStoragePermission();
    }

    /**
     * Fonction permettant de demander a l'User l'acces a son stockage, si cette permission n'est touours pas accepte.
     */
    private void isStoragePermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
            }
        }
    }

    /**
     * Permet de d'Init la list.
     * @param search Affinage de la recherche.
     */
    public void setList(String search){
        try {
            this.list = makeRetrofit("https://ensweb.users.info.unicaen.fr/android-api/",search);
        } catch (JsonIOException e) {
            Log.e("JML", "Erreur de création de la liste de personnes");
        }
    }

    /**
     * Permet de sauvegarde la recherche.
     * @param save Affinage.
     */
    public void setSearchSave(String save){
        this.searchSave = save;
    }

    /**
     * Retourne la recherche.
     * @return Affinage.
     */
    public String getSearchSave(){
        return this.searchSave;
    }

    /**
     * Applique un layout au menu.
     * @param menu
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_list,menu);
        MenuItem mSearch = menu.findItem(R.id.Search);

        // Capture lorsque l'User rentre quelque chose dans la barre de recherche.
        mSearchView = (SearchView) mSearch.getActionView();
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            // Capture lors de confirmation
            public boolean onQueryTextSubmit(String query) {
                setSearchSave(query);
                // Refresh de la list.
                setList(query);
                // On retire le focus sur le SearchView.
                mSearchView.clearFocus();
                return false;
            }

            @Override
            // Capture lors de l'ecriture.
            public boolean onQueryTextChange(String newText) {
                setSearchSave(newText);
                // Refresh de la list.
                setList(newText);
                return false;
            }
        });
        return true;
    }


    /**
     * Permet de capter sur quel item l'user a appuye.
     * @param menuItem Item selectionner
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        int id = menuItem.getItemId();
        if(id==R.id.Ajouter){
            Intent intent = new Intent(getApplicationContext(),ActivityAjouter.class);
            startActivityForResult(intent,ACTIVITY_AJOUTER);
        }else if(id==R.id.Profil) {
            Intent intent = new Intent(getApplicationContext(), ActivityProfil.class);
            startActivity(intent);
        }
        return true;
    }

    /**
     * Setup de la RecyclerView en fonction de la liste.
     */
    private void setupRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        AnnonceAdapter adapter = new AnnonceAdapter(this.list,false,false);
        recyclerView.setAdapter(adapter);
        if(!this.recyclerSpace){
            recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(),DividerItemDecoration.VERTICAL));
            this.recyclerSpace=true;
        }
    }

    /**
     * Permet de retourner la list des annonces en communicant avec l'API.
     * @param url url de l'API.
     * @param search Affinache de recherche.
     * @return la list d'Annonce.
     */
    private List<Annonce> makeRetrofit(String url,String search){
        final List<Annonce> Retrolist = new ArrayList<>();
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(url)
                .build();
        final jsonLink json = retrofit.create(jsonLink.class);

        // permettra de transformer un Json en une Annonce.
        Gson gson = new Gson();

        // Preparation de la requete.
        Call<JsonObject> call = json.returnListeJson();
        // Envoie..
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                // Contact avec l'API reussi.
                JsonObject jsonObject = response.body();
                if(jsonObject.get("success").getAsBoolean()){
                    // Reponse de l'API success.
                    JsonArray jsonArray = jsonObject.getAsJsonArray("response");

                    for(int i = jsonArray.size()-1;i>=0;i--){
                        // Parcours du jsonArray dans le sens inverse pour afficher les annonces recentes en premier.

                        // Recupere le json.
                        JsonElement jsonAnn = jsonArray.get(i);
                        // Transformation en une Annonce.
                        Annonce annonce = (Annonce) gson.fromJson(jsonAnn.toString(), Annonce.class);

                        if(search==null) {
                            // Pas d'affinage.
                            Retrolist.add(annonce);
                        }else{
                            // Affinage de la list.
                            // On verifie si la description ou le titre de l'annonce contient search (String).
                            if(annonce.getDescription().toLowerCase().contains(search.toLowerCase()) | annonce.getTitre().toLowerCase().contains(search.toLowerCase())){
                                // Si oui, l'annonce est ajoute a la liste.
                                Retrolist.add(annonce);
                            }
                        }
                    }
                }
                // List complete, on cache l'animation de chargement.
                findViewById(R.id.progressBarList).setVisibility(View.GONE);
                // On affiche la liste.
                setupRecyclerView();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                // Fail dans le contact de l'API.
                Log.i("testo","pasok");
            }
        });
        return Retrolist;
    }


    /**
     * Permet de capter sur quel item l'user a appuye dans le menu de navigation (Left Side).
     * @param menuItem Item selectionner
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        Intent i = null;
        switch (id) {
            case R.id.drawer_profile :
                i = new Intent(ActivityListAnnonce.this,ActivityProfil.class);
                startActivity(i);
                break;
            case R.id.drawer_annonceSave:
                i = new Intent(ActivityListAnnonce.this,ActivityAnnonceSave.class);
                startActivity(i);
                break;
            default:
                Log.i("testo", "kkkkkkk");
                break;

        }
        this.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Appele lors de la pression du bouton retour (ButtonSystem).
     * Avec un comportement different.
     */
    @Override
    public void onBackPressed() {

        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) { // Le menu de navigation est ouvert.
            // On le ferme.
            this.drawerLayout.closeDrawer(GravityCompat.START);

        } else if(!mSearchView.isIconified()) { // Une recherche est en cours.
            // On suprime la recherche.
            mSearchView.setIconified(true);
        }
        else { // Rien de special.
            super.onBackPressed();
        }
    }

    /**
     * Permet de refresh la liste si une annonce vient d'etre ajoute.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        if(requestCode == ACTIVITY_AJOUTER & resultCode==RESULT_OK) {
            super.onActivityResult(requestCode, resultCode, data);
            setList(getSearchSave());
            refreshLayout.setRefreshing(false);
        }
    }
}