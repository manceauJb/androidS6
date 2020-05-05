package com.example.annoncetp1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

public class AnnonceViewHolder extends RecyclerView.ViewHolder {
    private TextView vueTitre;
    private TextView vuePrix;
    private TextView vueVille;
    private ImageView vueImage;

    /**
     * Holder permettant d'assigner une annonce dans le recyclerView.
     * @param itemView Un item.
     * @param list La list d'annonce
     * @param save Bool pour savoir si la liste contient des annonces sauvegardees dans la BD
     * @param mine Bool pour savoir si la liste contient les annonces de l'user.
     */
    public AnnonceViewHolder(@NonNull View itemView, List<Annonce> list, boolean save,boolean mine) {
        super(itemView);
        vueTitre = (TextView) itemView.findViewById(R.id.listTitre);
        vueVille = (TextView) itemView.findViewById(R.id.listVille);
        vuePrix = (TextView) itemView.findViewById(R.id.listPrix);
        vueImage = (ImageView) itemView.findViewById(R.id.listImage);

        // Différent comportement en fonction de mine et save.
        if(!mine) {
            // L'annonce n'est pas de l'utilisateur.
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        Intent intent = null;

                        if (!save) {
                            // Annonce sauvegarder dans la base.
                            intent = new Intent(view.getContext(), ActivityVoirAnnonceID.class);
                        } else {
                            // Annonce recupere depuis l'API.
                            intent = new Intent(view.getContext(), ActivityVoirAnnonceDb.class);
                        }
                        intent.putExtra("id", list.get(pos).getId());
                        view.getContext().startActivity(intent);
                    }
                }
            });
        }else{
            // L'annonce de l'utilisateur.
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        Intent intent = new Intent(itemView.getContext(), ActivityVoirAnnonceID.class);
                        intent.putExtra("id",list.get(pos).getId());

                        // On ajoute un extra pour informer que c'est l'annonce de l'utilisateur.
                        // Pour pouvoir personnaliser les boutons dans la Toolbar.
                        intent.putExtra("mine","yes");

                        // On lance l'activity en attendant un resultat.
                        // Ce qui permet de reinitialise la liste si l'utilisateur modifie ou supprime son annonce.
                        Context context = v.getContext();
                        ((Activity) context).startActivityForResult(intent,3333);
                    }
                }
            });
        }

    }

    /**
     * On replis les champs avec les attributs de l'annonce passe en parametre.
     * @param annonce annonce qui sera affiche.
     */
    public void bind(Annonce annonce) {
        vueTitre.setText(annonce.getTitre());
        vuePrix.setText(annonce.getPrix());
        vueVille.setText(annonce.getVille());

        if (annonce.getImages().size() != 0) { // Annonce provenant de l'API

            String url = annonce.getImages().get(0);

            // Bug constate avec les images avec uniquement http.
            if (!url.contains("https")) {
                url = url.replace("http", "https");
            }
            Picasso.get().load(url).error(R.drawable.icon_errorimage).into(vueImage);
        }else if(annonce.getPath() != null){ // Annonce et Image provenant de la BD.
            try {

                if(annonce.getPath().size()==0){
                    // Pas d'image dans la list.
                    Picasso.get().load("www.google.com/image/1").placeholder(R.drawable.icon_noimage).error(R.drawable.icon_noimage).into(vueImage);

                } else if (annonce.getPath().get(0) != null) {
                    // On recupere la premiere image et on l'affiche.
                    try {
                        File f = new File(annonce.getPath().get(0));
                        Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                        vueImage.setImageBitmap(b);
                    } catch (FileNotFoundException e) {e.getMessage();}

                } else if(annonce.getPath().get(0)==null) {

                    // Probleme avec la list d'image.
                    Picasso.get().load("www.google.com/image/1").placeholder(R.drawable.icon_errorimage).error(R.drawable.icon_errorimage).into(vueImage);

                }
            }catch (IndexOutOfBoundsException e){
                e.getMessage();
            }
        }else{

            //Pas de list d'image.
            Picasso.get().load("www.google.com/image/1").placeholder(R.drawable.icon_noimage).error(R.drawable.icon_noimage).into(vueImage);
        }
    }
}
