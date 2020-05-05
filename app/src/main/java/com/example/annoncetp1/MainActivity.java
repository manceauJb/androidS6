package com.example.annoncetp1;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.content.Intent;


public class MainActivity extends AppCompatActivity {

    /**
     * Activity lance au demarrage de l'appli, permettant d'afficher un ecran de chargement et de verifier si l'appareil est connecte.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();

        // Thread permettant d'affiche le layout 1s.
        Thread welcomeThread = new Thread() {
            @Override
            public void run() {
                try {
                    super.run();
                    sleep(1000);
                } catch (Exception e) {

                } finally {
                    Intent i = null;

                    // Verification de la connection.
                    if (activeNetwork != null && activeNetwork.isConnected()) {
                        // Connecte donc on lance l'Activity qui affiche la liste d'anonce.
                        i = new Intent(MainActivity.this,
                                ActivityListAnnonce.class);
                    }else{
                        // Non connecte donc on lance l'Activity disconnected.
                        i = new Intent(MainActivity.this,ActivityDisconnected.class);
                    }

                    // On lance l'activity init precedement.
                    startActivity(i);
                    // On ferme MainActivity.
                    finish();
                }
            }
        };
        // On lance le Thread.
        welcomeThread.start();
    }
}