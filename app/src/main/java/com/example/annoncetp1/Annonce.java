package com.example.annoncetp1;

import java.util.ArrayList;
import java.util.Date;

/**
 * Class Annonce.
 */
public class Annonce {


    private String id;

    private String titre;

    private String description;

    private int prix;

    private String pseudo;

    private String emailContact;

    private String telContact;

    private String ville;

    private String cp;

    private ArrayList<String> images;

    private Long date;

    private ArrayList<String> path;

    public Annonce(String id, String titre, String description, int prix, String pseudo, String emailContact, String telContact, String ville, String cp, ArrayList<String> images, Long date) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.prix = prix;
        this.pseudo = pseudo;
        this.emailContact = emailContact;
        this.telContact = telContact;
        this.ville = ville;
        this.cp = cp;
        this.images = images;
        this.date = date;
        this.path = null;
    }

    public String getTitre() {
        return titre;
    }

    public String getDescription() {
        return description;
    }

    public String getPrix() {
        return Integer.toString(prix)+" €";
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getEmailContact() {
        return emailContact;
    }

    public String getTelContact() {
        return telContact;
    }

    public String getVille() {
        return ville;
    }

    public String getCp() {
        return cp;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public String getDate() {
        Date formatDate = new Date(this.date*1000);
        return formatDate.toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getIntPrix(){
        return this.prix;
    }

    public Long getLongDate(){
        return this.date;
    }

    @Override
    public String toString(){
        return this.id + " " + this.description;
    }

    public void setPath(ArrayList<String> path){
        this.path = path;
    }

    public ArrayList<String> getPath(){
        return this.path;
    }
}
