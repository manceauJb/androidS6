package com.example.annoncetp1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.JsonObject;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ActivityImage extends AppCompatActivity {

    /**
     * @see send
     * Contient le bouton send.
     */
    private Button send;

    /**
     * @see id
     * Contient l'id de l'annonce a laquelle on ajoute une image.
     */
    private String id;

    /**
     * @see file
     * Contiendra l'image une fois charge.
     */
    private File file;

    /**
     * @see REQUEST_PICK
     * Code requete pour l'activity permettant de choisir une photo dans la galerie.
     */
    private final int REQUEST_PICK = 1;

    /**
     * @see REQUEST_CAM
     * Code requete  pour l'activity permettant de prendre une photo avec la camera.
     */
    private final int REQUEST_CAM = 2;

    /**
     * Activity lancer quand l'utilisateur a confirmer le DialogAlert lui demandant si il veut ajouter une photo.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        // Setup de la toolbar.
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Verification des permissions concernant le stockage.
        String[] perms = {"android.permission.WRITE_EXTERNAL_STORAGE"};
        this.requestPermissions(perms,200);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Log.i("testo","accecs denided");
        }else{
            Log.i("testo","got access");
        }

        // Recuperation de l'id.
        Intent intent = getIntent();
        id = intent.getExtras().getString("id");


        // Lancement de l'activity ACTION_PICK lors du click sur le bouton galerie.
        Button galerie = findViewById(R.id.fromFile);
        galerie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lancement de l'activity en attendant un resultat.
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(photoPickerIntent,REQUEST_PICK);
            }
        });

        // Lancement de l'activity REQUEST_CAM lors du click sur le bouton camera.
        Button camera = findViewById(R.id.fromCam);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lancement de l'activity en attendant un resultat.
                Intent cameraPickerIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraPickerIntent, REQUEST_CAM);
            }
        });

        // Configuration du bouton send.
        send = findViewById(R.id.sendImage);
        // De base le bontoun n'est pas visible.
        send.setVisibility(View.GONE);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Pour pouvoir envoyer l'annonce à l'api.
                Retrofit retrofit = new Retrofit.Builder()
                        .addConverterFactory(GsonConverterFactory.create())
                        .baseUrl("https://ensweb.users.info.unicaen.fr/")
                        .build();
                final jsonLink json = retrofit.create(jsonLink.class);

                // Creation de la request contenant l'image.
                RequestBody fileBody = RequestBody.create(file,MediaType.get("image/png"));

                // Creation de la requete contenant toutes les donnees qui seront envoye en POST.
                MultipartBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("apikey","21713189")
                        .addFormDataPart("method","addImage")
                        .addFormDataPart("id",id)
                        .addFormDataPart("photo",file.getName(),fileBody)
                        .build();

                // Preparation de la requete.
                Call<JsonObject> call = json.saveImage(requestBody);
                // Envoi...
                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        // Connexion avec l'API reussi, on recupere la reponse.
                        JsonObject jsonObject = response.body();
                        if(jsonObject.get("success").getAsBoolean()){ // Reponse de l'API success, sauvegarde de l'image reussi.
                            Toast.makeText(getApplicationContext(),"Upload success",Toast.LENGTH_SHORT).show();

                            // On demande si l'User veux rajouter une autre image.
                            DialogFragment dialogFragment = new AskImageDialog(id,"Une autre image ?");
                            dialogFragment.show(getSupportFragmentManager(),"Ask for more Image");
                        }else{
                            // Error ave l'API.
                            Toast.makeText(getApplicationContext(),"Probleme dans l'upload.",Toast.LENGTH_SHORT);
                        }
                    }
                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        // Probleme avec l'API.
                        Toast.makeText(getApplicationContext(),t.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * Permet de recuperer l'imagee lors de la fin de l'activity ACTION_PICK ou REQUEST_CAM.
     * Dans les deux cas l'image est stocker dans file.
     * @param requestCode Contient le code requete de l'activity.
     * @param resultCode Code si tout c'est passer comme prevu.
     * @param data Intent contenant les informations sur l'image.
     */
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("testo","got image " + requestCode);
        ImageView imageLoaded = findViewById(R.id.imageLoaded);
        if(resultCode == RESULT_OK){
            // Il n'y as pas eu d'erreur.
            if(requestCode == REQUEST_PICK) {
                // REQUEST_PICK => Image from galerie.
                try {
                    Uri imageUri = data.getData();
                    String path;
                    final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    imageLoaded.setImageBitmap(selectedImage);

                    // Boutton send visible.
                    send.setVisibility(View.VISIBLE);

                    Cursor cursor = this.getContentResolver().query(imageUri, null, null, null, null);
                    if (cursor == null) {
                        path = imageUri.getPath();
                    } else {
                        cursor.moveToFirst();
                        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                        path = cursor.getString(idx);
                    }
                    file = new File(path);
                } catch (FileNotFoundException e) {
                    Toast.makeText(this, "Erreur QQPart", Toast.LENGTH_SHORT).show();
                }
            }else if(requestCode == REQUEST_CAM){
                // REQUEST_CAM => Image from camera.

                // Boutton send visible.
                send.setVisibility(View.VISIBLE);
                final Bitmap photo = (Bitmap) data.getExtras().get("data");
                imageLoaded.setImageBitmap(photo);
                file = createImageFile(photo);
            }
        }else{
            // Erreur, donc pas d'image.
            Toast.makeText(this,"Pas d'image selectionné",Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Permet de recuperer la reponse lors de la demande de permission.
     * @param permsRequestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults){
        switch(permsRequestCode){
            case 200:
                boolean writeAccepted = grantResults[0]== PackageManager.PERMISSION_GRANTED;
                break;
        }
    }

    /**
     * Convertit un Bitmap en File.
     * @param bmp
     * @return
     */
    private File createImageFile(Bitmap bmp){
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        OutputStream outputStream = null;
        File file = new File(extStorageDirectory,"temp.png");
        if(file.exists()){
            file.delete();
            file = new File(extStorageDirectory,"temp.png");
        }
        try {
            outputStream = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG,100,outputStream);
            outputStream.flush();
            outputStream.close();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    return file;
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
        int idMenu = menuItem.getItemId();
        if(idMenu==16908332){
            Intent i = new Intent(ActivityImage.this, ActivityVoirAnnonceID.class);
            i.putExtra("id",this.id);
            startActivity(i);
            finish();
        }
        return true;
    }
}
