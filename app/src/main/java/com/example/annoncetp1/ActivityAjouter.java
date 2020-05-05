package com.example.annoncetp1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

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

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ActivityAjouter extends AppCompatActivity {

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
     * @see profil
     * true : profil renseigne.
     * false: profil non renseigne.
     */
    Boolean profil;


    /**
     * Creation de l'activité ajouter
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajouter);

        // Animation de chargement invisible.
        findViewById(R.id.progressBarAdd).setVisibility(View.GONE);

        // Setup de la toolbar.
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Affectation des variables
        title = findViewById(R.id.AddTitle);
        prix = findViewById(R.id.AddPrix);
        description = findViewById(R.id.AddDescription);
        codeP = findViewById(R.id.AddCodeP);
        ville = findViewById(R.id.AddVille);

        // Recuperation du profil dans les preferences.
        SharedPreferences myPrefs = getSharedPreferences("profil",MODE_PRIVATE);
        pseudo = myPrefs.getString("pseudo",null);
        tel = myPrefs.getString("tel",null);
        mail = myPrefs.getString("mail",null);

        // Verification du profil, affiche si le profil est bien renseigne ou non.
        if(!checkUser()){
            Toast.makeText(this,"Veuillez remplir votre profil d'abord",Toast.LENGTH_SHORT).show();
            profil = false;
        }else{
            Toast.makeText(this,"Profil récupéré",Toast.LENGTH_SHORT).show();
            profil = true;
        }

        // Capture lors du click sur le bouton ajouter.
        Button ajouter = findViewById(R.id.AddSubmit);
        ajouter.setOnClickListener(new View.OnClickListener() {
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
                    // Profil non enregistre
                    Toast.makeText(v.getContext(),"Veuillez remplir votre profil d'abord",Toast.LENGTH_LONG).show();
                }else{
                    // Ok, donc on peut sauvegarder.

                    // Animation de chargement visible
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
                    mapAnn.put("method","save");
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
                    // Envoi...
                    call.enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            // Connexion avec l'API reussi, on recupere la reponse.
                            JsonObject jsonObject = response.body();

                            if(jsonObject.get("success").getAsBoolean()){ // Reponse de l'API success., sauvegarde reussi.
                                JsonObject jsonObject2 = jsonObject.getAsJsonObject("response");
                                String id = jsonObject2.get("id").getAsString();
                                Toast.makeText(v.getContext(),"Annonce saved",Toast.LENGTH_SHORT).show();

                                // Sauvegarder donc on peux cacher l'animation
                                findViewById(R.id.progressBarAdd).setVisibility(View.GONE);

                                // DialogFragment pour demander si l'User veux ajouter une image.
                                DialogFragment dialogFragment = new AskImageDialog(id,"Ajouter une image ?");
                                dialogFragment.show(getSupportFragmentManager(),"Ask for Image");
                            }else{
                                // Error avec l'API
                                findViewById(R.id.progressBarAdd).setVisibility(View.GONE);
                                Toast.makeText(v.getContext(),"QQCH c'est mal passé !",Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {
                            // Probleme avec l'API.
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
        getMenuInflater().inflate(R.menu.menu_disconnected,menu);
        setTitle("Ajouter");
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
            // On lance l'activity en attendant un resultat.
            startActivityForResult(intent,1);
        }else if(id==16908332){
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

    /**
     *
     * Permet de charger/recharger si le profil as ete ajoute/modifie.
     * @param requestCode Code initialise au lancement de l'activity profil (1)
     * @param resultCode Code retourner (RESULT_OK) si le profil as ete modifie/ajoute
     * @param data Intent contenant les valeurs pseudo, tel, mail.
     */
    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        // Collect data from the intent and use it
        if(resultCode==RESULT_OK) {
            super.onActivityResult(requestCode, resultCode, data);
            pseudo = data.getStringExtra("pseudo");
            tel = data.getStringExtra("tel");
            mail = data.getStringExtra("mail");
            Log.i("testo", pseudo + mail + tel);
        }
    }
}