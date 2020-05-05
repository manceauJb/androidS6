package com.example.annoncetp1;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityProfil extends AppCompatActivity {


    /**
     * Création de l'activity profil.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        // Setup de la Toolbar.
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Recuperation des zone de text.
        EditText profilPseudo = findViewById(R.id.profilPseudo);
        EditText profilTel = findViewById(R.id.profilTel);
        EditText profilMail = findViewById(R.id.profilEmail);

        // Affectation du pseudo si il a ete enregistre avant.
        String pseudo = getSharedPreferences("profil",MODE_PRIVATE).getString("pseudo",null);
        if(pseudo!=null){
            profilPseudo.setText(pseudo);
        }

        // Affectation du Tel si il a ete enregistre avant.
        String tel = getSharedPreferences("profil",MODE_PRIVATE).getString("tel",null);
        if(tel!=null){
            profilTel.setText(tel);
        }

        // Affectation du mail si il a ete enregistrer avant.
        String mail = getSharedPreferences("profil",MODE_PRIVATE).getString("mail",null);
        if(mail!=null){
            profilMail.setText(mail);
        }

        Button mesAnnonce = findViewById(R.id.mesAnnonces);
        if(mail==null | tel==null | pseudo == null){
            mesAnnonce.setVisibility(View.GONE);
        }

        // Capture lors du click sur le bouton send.
        Button save = findViewById(R.id.profilSave);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Recuperation des champs.
                String editPseudo = profilPseudo.getText().toString();
                String editTel = profilTel.getText().toString();
                String editMail = profilMail.getText().toString();

                // Verification des champs
                if(editMail.equals("") | editPseudo.equals("") | editTel.equals("")){
                    Toast.makeText(view.getContext(),"TOUT DOIT ETRE REMPLIE",Toast.LENGTH_LONG).show();
                }else{
                    // Profil correctement renseigne donc on sauvegarde dans les preferences.
                    SharedPreferences preferences = getSharedPreferences("profil",MODE_APPEND);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("pseudo",editPseudo);
                    editor.putString("tel",editTel);
                    editor.putString("mail",editMail);
                    editor.apply();
                    editor.commit();

                    Toast.makeText(view.getContext(),"Profil SAVED",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent();
                    intent.putExtra("pseudo",editPseudo);
                    intent.putExtra("tel",editTel);
                    intent.putExtra("mail",editMail);
                    setResult(RESULT_OK,intent);
                    // Ajout des data dans le result.

                    // Bouton mes annonces visibles.
                    mesAnnonce.setVisibility(View.VISIBLE);
                }

            }
        });

        // Capture lors du click sur le bouton Mes annonces.
        Button mesAnnonces = findViewById(R.id.mesAnnonces);
        mesAnnonces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lancement de l'activity Mes Annonces.
                Intent intent = new Intent(ActivityProfil.this,ActivityMesAnnonces.class);
                startActivity(intent);
            }
        });

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
     * Applique un layout au menu,et ajoute le titre
     * @param menu
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_empty,menu);
        setTitle("Profil");
        return true;
    }

    /**
     * Capture lors du bouton retour.
     */
    @Override
    public void onBackPressed(){
        // Activity Finish.
        finish();
    }

}
