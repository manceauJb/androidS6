package com.example.annoncetp1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AnnonceAdapter extends RecyclerView.Adapter<AnnonceViewHolder> {

    /**
     * @see list
     * La liste des annonces.
     */
    List<Annonce> list;

    /**
     * @see save
     * Bool pour savoir si ce sont des annonces sauvegardees.
     */
    private boolean save;

    /**
     * @see mine
     * Bool pour savoir si ce sont les annonces de l'User.
     */
    private boolean mine;

    /**
     * Class qui creer un Adapter pour la recyclerView
     * @param list list des annonces
     * @param save bool pour savoir si c'est une annonce sauvegarder.
     * @param mine bool pour savoir si cette annonce est affiche a partir de l'activity mes annonces.
     */
    public AnnonceAdapter(List<Annonce> list,boolean save,boolean mine){
        this.list = list;
        this.save = save;
        this.mine = mine;
    }

    /**
     * Initialise le ViewHolder.
     * @param viewGroup
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public AnnonceViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_annonce,viewGroup,false);
        return new AnnonceViewHolder(view,this.list,this.save,this.mine);
    }

    /**
     * Mets a jour le contenu avec l'annonce en '$position' position.
     * @param holder holder.
     * @param position position de l'item dans la list.
     */
    @Override
    public void onBindViewHolder(@NonNull AnnonceViewHolder holder, int position) {
        Annonce annonce = list.get(position);
        holder.bind(annonce);
    }

    /**
     * Retourne le nombre d'items dans la list.
     * @return taille de la liste.
     */
    @Override
    public int getItemCount() {
        return list.size();
    }
}
