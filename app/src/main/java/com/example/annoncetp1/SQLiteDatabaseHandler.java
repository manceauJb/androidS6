package com.example.annoncetp1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteDatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "savedb.db";
    public static final String TABLE_NAME = "saveannonce";
    public static final String KEY_ID = "id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_PRIX = "prix";
    public static final String KEY_PSEUDO = "pseudo";
    public static final String KEY_MAIL = "emailContact";
    public static final String KEY_TEL = "telContact";
    public static final String KEY_VILLE = "ville";
    public static final String KEY_CP = "cp";
    public static final String KEY_DATE = "date";
    public static final String KEY_IMAGES = "images";

    public static final String[] COLUMNS = { KEY_ID, KEY_TITLE,KEY_DESCRIPTION,KEY_PRIX,KEY_PSEUDO,KEY_MAIL,KEY_TEL,KEY_VILLE,KEY_CP,KEY_DATE,KEY_IMAGES};

    /**
     * Permet de gerer la base.
     * @param context
     */
    public SQLiteDatabaseHandler(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    /**
     * Fonction permettant de creer la DB.
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATION_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + KEY_ID + " TEXT PRIMARY KEY, "
                + KEY_TITLE + " TEXT, "
                + KEY_DESCRIPTION + " TEXT, "
                + KEY_PRIX + " INTEGER, "
                + KEY_PSEUDO + " TEXT , "
                + KEY_MAIL + " TEXT , "
                + KEY_TEL + " TEXT , "
                + KEY_VILLE + " TEXT , "
                + KEY_CP + " TEXT , "
                + KEY_DATE + " NUMERIC , "
                + KEY_IMAGES + " TEXT )";
        db.execSQL(CREATION_TABLE);
    }

    /**
     * Fonction permettant d'Upgrade la DB. (Non optimal car supprime l'ancienne pour en creer une nouvelle).
     * @param db la base
     * @param oldVersion ancienne version.
     * @param newVersion nouvelle version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(AnnonceDb.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
