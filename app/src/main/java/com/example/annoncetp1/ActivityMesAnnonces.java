package com.example.annoncetp1;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ActivityMesAnnonces extends AppCompatActivity {

    /**
     * @see list
     * Contient la liste des annonces.
     */
    private List<Annonce> list;

    /**
     * @see recyclerSpace
     * recyclerSpace : true  = cela veux dire que l'espace as ete ajouter entre chaque annonce.
     * recyclerSpace : false = cela veux dire que l'espace n'as pas ete ajouter.
     */
    private Boolean recyclerSpace = false;

    /**
     * Activity permettant de lister les annonces d'un User.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_annonce);

        // Setup de la Toolbar.
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Setup de la list.
        setList();

        // Ajout de la possibilite de refresh.
        SwipeRefreshLayout refreshLayout = findViewById(R.id.swipeRecycler);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setList();
                refreshLayout.setRefreshing(false);
            }
        });
    }

    private void setupRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        AnnonceAdapter adapter = new AnnonceAdapter(this.list,false,true);
        recyclerView.setAdapter(adapter);
        if(!this.recyclerSpace){
            recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(),DividerItemDecoration.VERTICAL));
            this.recyclerSpace=true;
        }
    }

    /**
     * Setup de la list.
     */
    public void setList(){
        try {
            this.list = makeRetrofit("https://ensweb.users.info.unicaen.fr/android-api/");
        } catch (JsonIOException e) {
            Log.e("JML", "Erreur de création de la liste de personnes");
        }
    }

    /**
     * Permet de retourner la liste d'annonce.
     * @param url contient l'URL de l'API
     * @return retourne la liste.
     */
    private List<Annonce> makeRetrofit(String url){

        // Recupere le pseudo de l'User.
        String pseudo = getSharedPreferences("profil",MODE_PRIVATE).getString("pseudo",null);

        final List<Annonce> Retrolist = new ArrayList<>();

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(url)
                .build();
        final jsonLink json = retrofit.create(jsonLink.class);

        Gson gson = new Gson();

        // Preparation de la requete avec le pseudo.
        Call<JsonObject> call = json.returnMyListeJson(pseudo);
        //Envoi...
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                // Connexion avec l'API reussi, on recupere la reponse.
                JsonObject jsonObject = response.body();
                if(jsonObject.get("success").getAsBoolean()){// Reponse de l'APÏ success, on parcours le JSON.
                    JsonArray jsonArray = jsonObject.getAsJsonArray("response");
                    for(int i = jsonArray.size()-1;i>=0;i--){
                        JsonElement jsonAnn = jsonArray.get(i);
                        Annonce annonce = (Annonce) gson.fromJson(jsonAnn.toString(), Annonce.class);
                        Retrolist.add(annonce);

                    }
                }
                // Recuperation termine.
                findViewById(R.id.progressBarList).setVisibility(View.GONE);
                setupRecyclerView();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                // Probleme avec l'API.
                Log.i("testo","fail");
            }
        });
        return Retrolist;
    }

    /**
     * Applique un layout au menu,et ajoute le titre
     * @param menu
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_empty, menu);
        setTitle("Mes annonces");
        return true;
    }

    /**
     * Permet de capter sur quel item l'user a appuye.
     * @param menuItem Item selectionner
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        int id = menuItem.getItemId();

        if(id==16908332){
            onBackPressed();
        }
        return true;
    }

    /**
     * Permet de recharger la liste lors d'une suppression ou modification d'une annonce.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("testo" , "result code = " + requestCode + "result = " + resultCode);
        if(requestCode == 3333 & resultCode == RESULT_OK) { // Permet de refresh la liste après un delete/edit.
            super.onActivityResult(requestCode, resultCode, data);
            setList();
            SwipeRefreshLayout refreshLayout = findViewById(R.id.swipeRecycler);
            refreshLayout.setRefreshing(false);
        }
    }
}
