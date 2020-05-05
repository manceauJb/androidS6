package com.example.annoncetp1;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.HandlerThread;
import android.telephony.SmsManager;
import android.util.Log;


import com.google.android.material.snackbar.Snackbar;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.http.Url;

import static android.content.Context.MODE_PRIVATE;


public class AnnonceDb {

    private final OkHttpClient okHttpClient = new OkHttpClient();
    private SQLiteDatabase dataBase;
    private SQLiteDatabaseHandler databaseHandler;
    private String[] allColumns = SQLiteDatabaseHandler.COLUMNS;
    private Context context;

    public AnnonceDb(Context context) {
        this.context = context;
        databaseHandler = new SQLiteDatabaseHandler(context);

    }


    public void open() throws SQLException {
        dataBase = databaseHandler.getWritableDatabase();
    }

    public void addAnnonce(Annonce annonce) {
        if(exists(annonce.getId())) {
            delete(annonce.getId());
        }

        ArrayList<String> tabImg = null;
        if (annonce.getImages().size() != 0) {
            tabImg = new ArrayList<String>();
            for (String path : annonce.getImages()) {
                tabImg.add(getImage(path));
            }
        }
        ContentValues values = new ContentValues();
        //databaseHandler.onUpgrade(dataBase,1,1);
        values.put(SQLiteDatabaseHandler.KEY_ID, annonce.getId());
        values.put(SQLiteDatabaseHandler.KEY_DESCRIPTION, annonce.getDescription());
        values.put(SQLiteDatabaseHandler.KEY_TITLE, annonce.getTitre());
        values.put(SQLiteDatabaseHandler.KEY_PRIX, annonce.getIntPrix());
        values.put(SQLiteDatabaseHandler.KEY_PSEUDO, annonce.getPseudo());
        values.put(SQLiteDatabaseHandler.KEY_MAIL, annonce.getEmailContact());
        values.put(SQLiteDatabaseHandler.KEY_TEL, annonce.getTelContact());
        values.put(SQLiteDatabaseHandler.KEY_CP, annonce.getCp());
        values.put(SQLiteDatabaseHandler.KEY_VILLE, annonce.getVille());
        values.put(SQLiteDatabaseHandler.KEY_DATE, annonce.getLongDate());
        values.put(SQLiteDatabaseHandler.KEY_IMAGES, listToString(tabImg));
        dataBase.insert(SQLiteDatabaseHandler.TABLE_NAME, null, values);
    }

    public void delete(String id) {
        Annonce annonce = getAnnonce(id);
        String where = SQLiteDatabaseHandler.KEY_ID + " = ?";
        String whereArgs[] = {id};
        dataBase.delete(SQLiteDatabaseHandler.TABLE_NAME, where, whereArgs);
        ArrayList<String> imgToDel = annonce.getPath();
        if(imgToDel.size()!=0){
            for (String path:imgToDel) {
                if(path != null) {
                    File file = new File(path);
                    file.delete();
                }
            }
        }
    }

    public boolean exists(String id){
        String where = SQLiteDatabaseHandler.KEY_ID + " LIKE \"" + id + "\"";
        dataBase = databaseHandler.getReadableDatabase();
        Cursor cursor = dataBase.query(SQLiteDatabaseHandler.TABLE_NAME, allColumns, where, null, null, null, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public List<Annonce> getAllAnnonce() {
        List<Annonce> annonces = new ArrayList<Annonce>();
        dataBase = databaseHandler.getReadableDatabase();
        //databaseHandler.onUpgrade(dataBase,1,1);
        Cursor cursor = dataBase.rawQuery("SELECT * FROM saveannonce", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ArrayList<String> img = stringToList(cursor.getString(10));
            Annonce annonce = new Annonce(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getInt(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    cursor.getString(7),
                    cursor.getString(8),
                    new ArrayList<>(),
                    cursor.getLong(9));
            annonce.setPath(img);
            annonces.add(annonce);
            cursor.moveToNext();
        }
        cursor.close();
        return annonces;
    }

    public Annonce getAnnonce(String id) {
        String where = SQLiteDatabaseHandler.KEY_ID + " LIKE \"" + id + "\"";
        dataBase = databaseHandler.getReadableDatabase();
        Cursor cursor = dataBase.query(SQLiteDatabaseHandler.TABLE_NAME, allColumns, where, null, null, null, null);
        cursor.moveToFirst();
        ArrayList<String> img = stringToList(cursor.getString(10));
        Annonce annonce = new Annonce(
                cursor.getString(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getInt(3),
                cursor.getString(4),
                cursor.getString(5),
                cursor.getString(6),
                cursor.getString(7),
                cursor.getString(8),
                img,
                cursor.getLong(9));
        annonce.setPath(img);
        return annonce;
    }

    private String getImage(String link) {
        final String[] path = {null};
        final CountDownLatch latch = new CountDownLatch(1);
        Thread thread = new HandlerThread("Dowload"){
            @Override
            public void run(){
                try{
                    path[0] = new imageDownloader().doInBackground(new URL(link));
                    latch.countDown();
                }catch (IOException e){
                    e.getMessage();
                }
            }
        };
        try{
            thread.start();
            latch.await();
        }catch (InterruptedException e){
            e.getMessage();
        }
        return path[0];
    }

    private String listToString(ArrayList<String> list){
        JSONObject json = new JSONObject();
        try{
            json.put("uniqueArrays",new JSONArray(list));
        }catch (JSONException e){
            e.getMessage();
        }
        return json.toString();
    }

    private ArrayList<String> stringToList(String string){
        ArrayList<String> listString = new ArrayList<String>();
        try{
            JSONObject json = new JSONObject(string);
            JSONArray list = json.optJSONArray("uniqueArrays");
            if(list != null){
                for(int i = 0;i<list.length();i++){
                    if(list.getString(i)=="null"){
                        listString.add(null);
                    }else{
                        listString.add(list.getString(i));
                    }
                }
            }
        }catch (JSONException e){
            e.getMessage();
        }
        return listString;
    }

    private class imageDownloader extends AsyncTask<URL,Void,String> {

        protected String doInBackground(URL...urls){
            URL url = urls[0];
            HttpURLConnection connection = null;

            try{
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                Bitmap bmp = BitmapFactory.decodeStream(bufferedInputStream);
                return saveImageToInternalStorage(bmp);

            }catch(IOException e){
                e.printStackTrace();
            }finally{
                connection.disconnect();
            }
            return null;
        }


        protected String saveImageToInternalStorage(Bitmap bitmap){
            ContextWrapper wrapper = new ContextWrapper(context);
            File file = wrapper.getDir("imageDir",context.MODE_PRIVATE);
            file = new File(file, new Date().getTime() +".jpg");

            try{
                OutputStream stream = null;

                stream = new FileOutputStream(file);

                bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);

                stream.flush();

                stream.close();

            }catch (IOException e)
            {
                e.printStackTrace();
            }
            Uri savedImageURI = Uri.parse(file.getAbsolutePath());
            return savedImageURI.getPath();
        }
    }


}

