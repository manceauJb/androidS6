package com.example.annoncetp1;

import com.google.gson.JsonObject;

import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface jsonLink {

    // Requete permettant de retourner la liste des annonces.
    @GET("?apikey=21713189&method=listAll")
    Call<JsonObject> returnListeJson();

    // Requete permettant de retourner une annonce avec son Id.
    @GET("?apikey=21713189&method=details")
    Call<JsonObject> returnAnonceIDJson(@Query("id") String id);

    // Requete permettant de sauvegarder une nouvelle annonce.
    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("android-api/")
    Call<JsonObject> saveAnnonce(@FieldMap Map<String,String> annonce);

    // Requete permettant de sauvegarder une image.
    @POST("android-api/")
    Call<JsonObject> saveImage(@Body MultipartBody filePart);

    // Requete permettant de retourner la liste d'annonces appartenant a pseudo.
    @GET("?apikey=21713189&method=listByPseudo")
    Call<JsonObject> returnMyListeJson(@Query("pseudo")String pseudo);

    // Requete permettant de supprimer l'annonce qui a pour id 'id'.
    @GET("?apikey=21713189&method=delete")
    Call<JsonObject> deleteAnnonce(@Query("id")String id);
}
