package com.example.annoncetp1;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class AskDeleteDbDialog extends DialogFragment {
    private AnnonceDb annonceDb;
    private String id;
    private Toast toast;

    /**
     * AlertDialog pour demande a l'User si il veut supprimer l'annonce de ses annonces sauvegarde.
     * @param annonceDb Permet de communiquer avec la BD.
     * @param id L'id de l'annonce.
     */
    public AskDeleteDbDialog(AnnonceDb annonceDb,String id){
        this.annonceDb = annonceDb;
        this.id = id;
    }

    /**
     * On creer l'AlertDialog, que l'on retourne.
     * @param savedInstanceState Instance save.
     * @return AlertDialog.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Voullez vous supprimez cette annonce sauvegardée ? ")
                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    @Override
                    // Click sur OUI.
                    public void onClick(DialogInterface dialog, int which) {
                        // On supprime l'annonce dans la BD.
                        annonceDb.delete(id);
                        // On ferme l'activity.
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
}

