package com.example.annoncetp1;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.ToxicBakery.viewpager.transforms.ZoomOutSlideTransformer;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ActivityVoirAnnonceID extends AppCompatActivity {

    /**
     * @see datasource
     * Permet de communiquer avec la DB, si l'user voudrais save l'annonce.
     */
    private AnnonceDb datasource;

    /**
     * @see annonce
     * Contient l'annonce.
     */
    Annonce annonce;

    /**
     * @see mine
     * Permet de savoir si l'annonce est à l'User.
     * De base mine vaut FALSE.
     */
    boolean mine = false;

    /**
     * @see id
     * Contient l'id de l'annonce a afficher.
     */
    String id;

    /**
     * Activity permetant d'afficher l'annonce.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.annonce_layout);

        // On recupere l'id de l'annonce
        Intent intent = getIntent();
        id = intent.getExtras().getString("id");

        // On verifie si l'annonce provient de l'activity ActivityMesAnnonces.
        if(intent.getExtras().getString("mine")!= null){
            mine = true;
        }

        // On init datasource pour pouvoir communiquer avec la base.
        datasource = new AnnonceDb(this);
        datasource.open();

        // On cache les boutons pendant le chargement.
        findViewById(R.id.sendMail).setVisibility(View.GONE);
        findViewById(R.id.call).setVisibility(View.GONE);

        // On recupere l'annonce.
        annonce = makeRetrofit("https://ensweb.users.info.unicaen.fr/android-api/",id);

        // Setup de la ToolBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void setAnnonce(Annonce annonce){
        this.annonce = annonce;
    }

    /**
     * Affiche l'annonce a l'ecran.
     * @param annonce l'annonce qui sera affiche
     */
    private void annonceToScreen(Annonce annonce){

        // On recupere les champs.
        TextView title = findViewById(R.id.title);
        TextView prix = findViewById(R.id.prix);
        TextView cpville = findViewById(R.id.ville);
        TextView description = findViewById(R.id.description);
        TextView date = findViewById(R.id.date);
        TextView pseudo = findViewById(R.id.Pseudo);
        TextView phone = findViewById(R.id.phone);
        TextView email = findViewById(R.id.email);
        ImageView imageView = findViewById(R.id.image);
        Button sendMail = findViewById(R.id.sendMail);
        Button call = findViewById(R.id.call);

        // On remplis les champs avec les attributs d'annonce.
        title.setText(annonce.getTitre());
        prix.setText(annonce.getPrix());
        cpville.setText(annonce.getCp()+ " " +annonce.getVille());
        description.setText(annonce.getDescription());
        date.setText(annonce.getDate());
        pseudo.setText(annonce.getPseudo());
        phone.setText(annonce.getTelContact());
        email.setText(annonce.getEmailContact());

        // On replis le ViewPager avec les images.
        ViewPager viewPager = findViewById(R.id.view_pager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(this,annonce.getImages(),false);
        viewPager.setAdapter(adapter);
        viewPager.setPageTransformer(true, new ZoomOutSlideTransformer());

        // Si l'annonce provient de l'Activity MesAnnonces, on cache les boutons call et mail.
        if(mine) {
            sendMail.setVisibility(View.GONE);
            call.setVisibility(View.GONE);
        }else{
            // Capture du click sur le bouton mail.
            sendMail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendMail();
                }
            });

            // Capture du click sur le send mail.
            call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callTo();
                }
            });
        }

    }

    /**
     * Fonction permettant de lancer l'activity permettant d'envoyer un mail.
     */
    private void sendMail(){
        Intent email = new Intent(Intent.ACTION_SEND);
        email.setType("message/rfc822");
        email.putExtra(Intent.EXTRA_EMAIL  , new String[]{annonce.getEmailContact()});
        email.putExtra(Intent.EXTRA_SUBJECT, annonce.getTitre());
        email.putExtra(Intent.EXTRA_TEXT   , "Je veux ça !");
        try {
            startActivity(Intent.createChooser(email, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Fonction permettant de lancer l'activity permettant d'appeler.
     */
    private void callTo(){
        Intent call = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel",annonce.getTelContact(),null));
        startActivity(Intent.createChooser(call, "Call to..."));
    }


    /**
     * Requete permettant de retourner l'annonce en contactant l'API
     * @param url l'url de l'api.
     * @param id id de l'annonce.
     * @return retourne l'annonce.
     */
    private Annonce makeRetrofit(String url, String id){
        final Annonce[] annonce = new Annonce[1];

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(url)
                .build();
        final jsonLink json = retrofit.create(jsonLink.class);

        // Pemettra de convertir le Json en une Annonce.
        Gson gson = new Gson();

        // Preparation de la requete.
        Call<JsonObject> call = json.returnAnonceIDJson(id);
        // Envoi...
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                // Contact avec l'API reussi.
                JsonObject jsonObject = response.body();

                if(jsonObject.get("success").getAsBoolean()){
                    // Reponse l'API success, on transforme le JSON en une Annonce.
                    JsonObject jsonObject2 = jsonObject.getAsJsonObject("response");

                    // Transformation.
                    annonce[0] = (Annonce) gson.fromJson(jsonObject2.toString(),Annonce.class);

                    // On affiche l'annonce a l'ecran.
                    annonceToScreen(annonce[0]);
                    setAnnonce(annonce[0]);

                    if(annonce[0].getPseudo().equals(getSharedPreferences("profil",MODE_PRIVATE).getString("pseudo",null))){
                        Log.i("testo","same pseudo");
                        // mine = true;
                    }

                    if(!mine){
                        // Ce n'est pas l'annonce de l'User, on affiche donc les boutons call et mail.
                        findViewById(R.id.sendMail).setVisibility(View.VISIBLE);
                        findViewById(R.id.call).setVisibility(View.VISIBLE);
                    }

                    // Annonce charger, on cache l'animation de chargement.
                    findViewById(R.id.progressBarVoir).setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                // Error pour contacter l'API.
                Log.i("testo","pasok");
            }
        });

        // On retourne l'annonce.
        return annonce[0];
    }

    /**
     * Applique un layout au menu en fonction de mine.
     * @param menu
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        if(getIntent().getExtras().getString("mine")!=null){
            // Annonce provenant de ActivityMesAnnonces
            // Donc layout du menu contenant les boutons Edit et Delete.
            getMenuInflater().inflate(R.menu.menu_annonce_mine, menu);
        }else{
            // Annonce ne provenant pas de ActivityMesAnnonces
            // Donc layout du menu contenant les boutons Call, Mail et Save.
            getMenuInflater().inflate(R.menu.menu_annonce, menu);
        }
        return true;
    }


    /**
     * Permet de capter sur quel item l'user a appuye.
     * @param menuItem Item selectionner
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        int id = menuItem.getItemId();
        if(id==R.id.SaveAnnonce) {
            saveOnDataBase();
        }else if(id==R.id.menuMail){
            sendMail();
        }else if(id==R.id.menuCall){
            callTo();
        }else if(id==16908332){
            onBackPressed();
        }else if(id==R.id.idDelete){
            askForDelete();
        }else if(id==R.id.idEdit){
            edit();
        }
        return true;
    }

    /**
     * On sauvegarde l'annonce dans la DB grace a datasource.
     */
    private void saveOnDataBase(){
        Toast.makeText(this,"Annonce saved",Toast.LENGTH_SHORT).show();
        datasource.addAnnonce(annonce);
    }

    /**
     * Fonction appele si Annonce est a l'utilisiteur et si il a cliquer sur le bouton delete.
     * Affiche un AlertDialog pour demande si il veut vraiment supprimer cette annonce.
     */
    private void askForDelete(){
        DialogFragment dialogFragment = new AskDeleteMyAnnonce(annonce.getId());
        FragmentManager fragmentManager = ((AppCompatActivity) this).getSupportFragmentManager();
        dialogFragment.show(fragmentManager, "test");
    }

    /**
     * Fonction appele si Annonce est a l'utilisiteur et si il a cliquer sur le bouton edit.
     * Lance l'activity ActivityEdit.
     * En lui passant des Informations concernant l'annonce.
     */
    private void edit(){
        Intent intent = new Intent(getApplication(),ActivityEdit.class);
        intent.putExtra("id",id);
        intent.putExtra("title",annonce.getTitre());
        intent.putExtra("description",annonce.getDescription());
        intent.putExtra("prix",annonce.getIntPrix());
        intent.putExtra("ville",annonce.getVille());
        intent.putExtra("cp",annonce.getCp());
        startActivityForResult(intent,4444);
    }

    /**
     * Permet de recharger l'annonce si elle a ete modifie
     * @param requestCode Code initialise au lancement de l'activity ActivityEdit.
     * @param resultCode Code retourner (RESULT_OK) si l'annonce a ete modifie
     * @param data Intent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == 4444 & resultCode == RESULT_OK) {
            // L'annonce a ete modife.
            // Donc on recharge l'annonce.
            super.onActivityResult(requestCode, resultCode, data);
            annonce = makeRetrofit("https://ensweb.users.info.unicaen.fr/android-api/",id);

            // Permettra de refresh la liste de l'activity precedente.
            setResult(Activity.RESULT_OK,new Intent());
        }
    }

}
