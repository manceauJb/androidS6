package com.example.annoncetp1;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AskDeleteMyAnnonce extends DialogFragment {

    private String id;
    private Toast toast;
    private final String url ="https://ensweb.users.info.unicaen.fr/android-api/";

    /**
     * AlertDialog pour demande a l'User si il veut supprimer son annonce.
     * @param id l'id de son annonce.
     */
    public AskDeleteMyAnnonce(String id){
        this.id = id;
    }

    /**
     * On creer l'AlertDialogn que l'on retourne.
     * @param savedInstanceState Instance save.
     * @return AlertDialog.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Voullez vous supprimez cette annonce à tout jamais ? ")
                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    @Override
                    // Click sur OUI.
                    public void onClick(DialogInterface dialog, int which) {
                        delete();
                        getActivity().setResult(Activity.RESULT_OK,new Intent());
                        getActivity().finish();
                    }
                })
                .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                    @Override
                    // Click sur NON.
                    public void onClick(DialogInterface dialog, int which) {
                        // On ne fais rien.
                    }
                });
        builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if(toast == null || toast.getView().getWindowVisibility() != View.VISIBLE){
                    // Permet d'eviter le spam du Toast.
                    toast = Toast.makeText(getContext(),"Vous devez répondre  !",Toast.LENGTH_SHORT);
                    toast.show();
                }
                return true;
            }
        });
        AlertDialog alertDialog = builder.create();
        // Force l'User a repondre.
        alertDialog.setCanceledOnTouchOutside(false);
        return alertDialog;
    }

    /**
     * Fonction permettant de supprimer l'annonce en envoyant une requette a l'API.
     */
    private void delete(){
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(url)
                .build();
        final jsonLink json = retrofit.create(jsonLink.class);

        Call<JsonObject> call = json.deleteAnnonce(id);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject jsonObject = response.body();
                if(jsonObject.get("success").getAsBoolean()){
                    // Suppression done.
                    Toast.makeText(getActivity(),"Annonce deleted",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }



}
