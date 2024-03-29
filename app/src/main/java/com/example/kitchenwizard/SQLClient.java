package com.example.kitchenwizard;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class SQLClient extends SQLiteOpenHelper {

    // Vous devez gérer le numéro de version de votre BDD (a un impact sur la reconstruction de la BDD par exemple)
    public static final int DATABASE_VERSION = 6;

    // Nom du fichier contenant la BDD (sqlite = fichier)
    public static final String  DATABASE_FILE = "recettes.db";

    // Requete de creation de la bdd (exemple simplifié)
    public static final String SQL_CREATE = "CREATE TABLE IF NOT EXISTS Favoris (id INTEGER PRIMARY KEY);";

    // Requete de suppression de la bdd (exemple simplifié)
    public static final String SQL_DELETE = "DROP TABLE IF EXISTS  Favoris ;";


    // Constructeur permettant d'appeler le constructeur de SQLIteOpenHelper (cf. doc)
    public SQLClient(Context context){
        super (context, DATABASE_FILE, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // On créé la BDD si besoin
        db.execSQL(SQLClient.SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Si la version de la BDD change.. Ici doit être mis le code pour traiter cette situation
        // Ici : traitement violent... On supprimme et on la créé à nouveau...
        // A adapter en fonction des besoins....
        db.execSQL(SQLClient.SQL_DELETE);
        this.onCreate(db);
    }
}