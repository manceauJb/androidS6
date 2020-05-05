package com.example.annoncetp1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.ToxicBakery.viewpager.transforms.ZoomOutSlideTransformer;

import java.io.Console;

public class ActivityVoirAnnonceDb extends AppCompatActivity {

    /**
     * @see annonce
     * L'annonce que l'User verra.
     */
    private Annonce annonce;

    /**
     * @see annonceDb
     * Permet de communiquer avec la BD.
     */
    private AnnonceDb annonceDb;

    /**
     * Activity qui affiche l'annonce sauvegarder dans la BD.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.annonce_layout);

        // Setup de la toolbar.
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Recuperation de l'id de l'annonce.
        Intent intent = getIntent();
        String id = intent.getExtras().getString("id");

        // Init de annonceDb.
        annonceDb = new AnnonceDb(this);
        annonceDb.open();
        // Recupere l'annonce qui as pour "id" id.
        annonce = annonceDb.getAnnonce(id);
        // On passe l'annonce a l'ecran.
        annonceToScreen(annonce);
    }

    /**
     * Affiche l'annonce a l'ecran.
     * @param annonce annonce qui sera affiche.
     */
    private void annonceToScreen(Annonce annonce){
        // Recuperation de toutes les zones de texte / Button / ImageView.
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

        // On remplis les champs recuperer precedement.
        title.setText(annonce.getTitre());
        prix.setText(annonce.getPrix());
        cpville.setText(annonce.getCp()+ " " +annonce.getVille());
        description.setText(annonce.getDescription());
        date.setText(annonce.getDate());
        pseudo.setText(annonce.getPseudo());
        phone.setText(annonce.getTelContact());
        email.setText(annonce.getEmailContact());

        // Setup du slider d'image.
        ViewPager viewPager = findViewById(R.id.view_pager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(this,annonce.getPath(),true);
        viewPager.setAdapter(adapter);
        viewPager.setPageTransformer(true, new ZoomOutSlideTransformer());

        // Capture du click sur le bouton mail.
        sendMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMail();
            }
        });

        // Capture du click sur le bouton call.
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callTo();
            }
        });

        // Annonce affiche, donc on desactive l'animation.
        findViewById(R.id.progressBarVoir).setVisibility(View.GONE);
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
     * Permet de capter sur quel item l'user a appuye.
     * @param menuItem Item selectionner
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        int id = menuItem.getItemId();

        if(id==16908332){
            onBackPressed();
        }else if(id==R.id.menuMail){
            sendMail();
        }else if(id==R.id.menuCall) {
            callTo();
        }else if(id==R.id.DbDelete){
            DialogFragment dialogFragment = new AskDeleteDbDialog(annonceDb,annonce.getId());
            dialogFragment.show(getSupportFragmentManager(),"Ask for DB delete");
        }
        return true;
    }

    /**
     * Applique un layout au menu.
     * @param menu
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_annonce_saved,menu);
        return true;
    }
}
