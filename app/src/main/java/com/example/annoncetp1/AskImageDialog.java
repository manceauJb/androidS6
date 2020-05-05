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

public class AskImageDialog extends DialogFragment {

    private String id;
    private String text;
    private Toast toast;

    /**
     * AlertDialog pour demande a l'User si il veut ajouter une image.
     * @param id id de l'annonce.
     * @param text text a afficher.
     */
    public AskImageDialog(String id,String text){
        this.id = id;
        this.text = text;
    }

    /**
     * On creer l'AlertDialog, que l'on retourne.
     * @param savedInstanceState Instance save.
     * @return AlertDialog.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Intent intent = new Intent();
        builder.setMessage(text)
                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    @Override
                    // Click sur OUI.
                    public void onClick(DialogInterface dialog, int which) {
                        // On ferme l'activity precedente (ActivityAjouter).
                        getActivity().setResult(Activity.RESULT_OK,intent);
                        getActivity().finish();

                        // On lance l'activity Image.
                        Intent actImage = new Intent(getActivity().getBaseContext(),ActivityImage.class);
                        actImage.putExtra("id",id);
                        startActivity(actImage);
                    }
                })
                .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // On ferme l'activity precedente (ActivityAjouter).
                        getActivity().setResult(Activity.RESULT_OK,intent);
                        getActivity().finish();

                        // On lance l'activity qui affiche l'annonce qui vient d'etre ajoute a l'ecran.
                        Intent intent = new Intent(getActivity().getBaseContext(), ActivityVoirAnnonceID.class);
                        intent.putExtra("id",id);
                        startActivity(intent);
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
