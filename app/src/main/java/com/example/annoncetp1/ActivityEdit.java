package com.example.annoncetp1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ActivityEdit extends AppCompatActivity {

    /**
     * @see title
     * Zone texte pour le titre de l'annonce.
     */
    private EditText title;

    /**
     * @see prix
     * Zone texte pour le prix de l'annonce.
     */
    private EditText prix;

    /**
     * @see description
     * Zone texte pour la description de l'annonce.
     */
    private EditText description;

    /**
     * @see codeP
     * Zone texte pour le code postal de l'annonce.
     */
    private EditText codeP;

    /**
     * @see ville
     * Zone texte pour la ville de l'annonce.
     */
    EditText ville;

    /**
     * @see pseudo
     * Contient le nom de l'utilisateur.
     */
    String pseudo;

    /**
     * @see tel
     * Contient le téléphone de l'utilisateur.
     */
    String tel;

    /**
     * @see mail
     * Contient le mail de l'utilisateur.
     */
    String mail;


    /**
     * Creation de l'activité editer.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajouter);

        // Animation de chargement invisible.
        findViewById(R.id.progressBarAdd).setVisibility(View.GONE);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // Affectation des variables.
        title = findViewById(R.id.AddTitle);
        prix = findViewById(R.id.AddPrix);
        description = findViewById(R.id.AddDescription);
        codeP = findViewById(R.id.AddCodeP);
        ville = findViewById(R.id.AddVille);

        // Affectation du text dans les inputs, par les valeur de l'annonce que l'User as demande de modifier.
        Intent intent = getIntent();
        title.setText(intent.getExtras().getString("title"));
        description.setText(intent.getExtras().getString("description"));
        prix.setText(Integer.toString(intent.getExtras().getInt("prix")));
        ville.setText(intent.getExtras().getString("ville"));
        codeP.setText(intent.getExtras().getString("cp"));

        // Recuperation du profil dans les preferences.
        SharedPreferences myPrefs = getSharedPreferences("profil",MODE_PRIVATE);
        pseudo = myPrefs.getString("pseudo",null);
        tel = myPrefs.getString("tel",null);
        mail = myPrefs.getString("mail",null);


        // Capture du boutton editer
        Button editer = findViewById(R.id.AddSubmit);
        editer.setText("Modifier");
        editer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Recuperation des champs.
                String newTitle = title.getText().toString();
                String newPrix = prix.getText().toString();
                String newDescription = description.getText().toString();
                String newCp = codeP.getText().toString();
                String newVille = ville.getText().toString();

                // Verification des champs.
                if(newCp.equals("")|newTitle.equals("")|newPrix.equals("")|newDescription.equals("")|newVille.equals("")){
                    // Manque des champs.
                    Toast.makeText(v.getContext(),"Tous les champs doivent être remplies",Toast.LENGTH_LONG).show();
                }else if(!checkUser()){
                    // Profil non enregistre.
                    Toast.makeText(v.getContext(),"Veuillez remplir votre profil d'abord",Toast.LENGTH_LONG).show();
                }else{
                    // Ok, donc on peut editer.

                    // Animation de chargement visible.
                    findViewById(R.id.progressBarAdd).setVisibility(View.VISIBLE);

                    // Pour pouvoir envoyer l'annonce à l'api.
                    Retrofit retrofit = new Retrofit.Builder()
                            .addConverterFactory(GsonConverterFactory.create())
                            .baseUrl("https://ensweb.users.info.unicaen.fr/")
                            .build();
                    final jsonLink json = retrofit.create(jsonLink.class);

                    // Contient ce que l'on va envoyer en post.
                    Map<String,String> mapAnn = new HashMap<>();

                    mapAnn.put("apikey","21713189");
                    mapAnn.put("method","update");
                    mapAnn.put("id",intent.getExtras().getString("id"));
                    mapAnn.put("titre",newTitle);
                    mapAnn.put("description",newDescription);
                    mapAnn.put("prix",newPrix);
                    mapAnn.put("pseudo",pseudo);
                    mapAnn.put("emailContact",mail);
                    mapAnn.put("telContact",tel);
                    mapAnn.put("ville",newVille);
                    mapAnn.put("cp",newCp);

                    // Preparation de la requete
                    Call<JsonObject> call = json.saveAnnonce(mapAnn);
                    // Envoie...
                    call.enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            JsonObject jsonObject = response.body();
                            if(jsonObject.get("success").getAsBoolean()){
                                JsonObject jsonObject2 = jsonObject.getAsJsonObject("response");
                                String id = jsonObject2.get("id").getAsString();
                                Toast.makeText(v.getContext(),"Annonce edited",Toast.LENGTH_SHORT).show();
                                findViewById(R.id.progressBarAdd).setVisibility(View.GONE);
                                setResult(Activity.RESULT_OK,new Intent());
                                finish();
                            }else{
                                findViewById(R.id.progressBarAdd).setVisibility(View.GONE);
                                Toast.makeText(v.getContext(),"QQCH c'est mal passé !",Toast.LENGTH_SHORT).show();
                                Log.i("testo","err updtate + " + jsonObject.toString());
                            }
                        }
                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {
                            Log.i("testo","fail " + call.toString());
                        }
                    });

                }
            }
        });
    }

    /**
     * Applique un layout au menu,et ajoute le titre
     * @param menu
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_empty,menu);
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
     * Permet de verifier si l'utilisateur a bien rentre son profil.
     * @return si profil est rentre ou non
     */
    public boolean checkUser(){
        boolean res = false;
        if(pseudo != null | tel != null | mail != null){
            res = true;
        }
        return res;
    }

}
