package com.example.kitchenwizard;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class Favoris extends AppCompatActivity {

    private SQLClient bdd;

    public long litDonnées(SQLClient bdd){
        // Ouverture d'une connexion en lecture
        SQLiteDatabase dbR = bdd.getWritableDatabase();

        // Sans SQL (cf plus bas pour SQL)
        String [] critèresDeProjection = {"id"};
        String [] critèreDeSélection = {}; // Aucun critère donc tous les enregistrements

        // Ouvre un curseur avec le(s) résultat(s)
        Cursor curs = dbR.query("Favoris", critèresDeProjection, "", critèreDeSélection, null, null, "id DESC");

        // Traite les réponses contenues dans le curseur

        // Y'a t'il au moins un résultat ?
        if (curs.moveToFirst()) {
            // Parcours des résultats
            do {
                // Récupération des données par le numéro de colonne
                //long clientID = curs.getLong(0);
                // ou avec le nom de la colonne (sans doute à privilégier pour la relecture du code)
                return curs.getLong(curs.getColumnIndexOrThrow("id"));

            } while (curs.moveToNext());
        }
        else{
            Toast.makeText(this, "Pas de réponses.....", Toast.LENGTH_SHORT).show();
        }

        //------------------------------------------------------------ Avec SQL
        Cursor cursSQL = dbR.rawQuery("select id from Favoris order by id ASC", null);

        // Le traitement des résultats est similaire à haut dessus.
        // Y'a t'il au moins un résultat ?
        if (cursSQL.moveToFirst()) {
            // Parcours des résultats
            do {
                // Récupération des données par le numéro de colonne
                //long clientID = curs.getLong(0);
                // ou avec le nom de la colonne (sans doute à privilégier pour la relecture du code)
                return cursSQL.getLong(cursSQL.getColumnIndexOrThrow("id"));

            } while (cursSQL.moveToNext());
        }
        else{
            return 0;
        }

        // ferme la connexion en lecture à la BDD -- à vous de voir s'il faut ou non conserver la connexion ouverte ... Attention aux ressources...
        //dbR.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.allergenes) {
            Intent intent = new Intent(this, Allergenes.class);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.recettes) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.favoris) {
            Intent intent = new Intent(this, Favoris.class);
            startActivity(intent);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void doSomething(String valeur) {
        Intent intent = new Intent(this, Recette.class);
        intent.putExtra("pokemon", valeur);
        startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        EdgeToEdge.enable(this);
        setContentView(R.layout.favoris);

        this.bdd = new SQLClient(this);

        //########################################################################
        // Illustration de la lecture de données dans la BDD
        long listeFavoris = this.litDonnées(bdd);

        if (listeFavoris == 0) {
            Toast.makeText(this, "Il n'y a pas de favoris enregistré.", Toast.LENGTH_SHORT).show();
        }
        List<String> listItem = new ArrayList<>();
        ListView listView = findViewById(R.id.listfavoris);
        ArrayAdapter<String> adapter =  new ArrayAdapter<>(this, R.layout.list_recette, R.id.textView, listItem);
        listView.setAdapter(adapter);
        listItem.add("Cupcake");
        listItem.add("eclair");
        listItem.add("macaron");
        adapter.notifyDataSetChanged();

    }
}