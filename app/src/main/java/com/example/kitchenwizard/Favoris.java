package com.example.kitchenwizard;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class Favoris extends AppCompatActivity implements RecipeFetcher.RecipeListener{

    public static final String TAG = "Favoris";
    private List<Recipe> listItem = new ArrayList<>();
    private CustomAdapter adapter;

    private SQLClient bdd;

    public List<Integer> litDonnées(SQLClient bdd){
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
                Log.v(TAG, "id = " + curs.getLong(curs.getColumnIndexOrThrow("id")));
                curs.getLong(curs.getColumnIndexOrThrow("id"));

            } while (curs.moveToNext());
        }
        else{
            //Toast.makeText(this, "Pas de réponses.....", Toast.LENGTH_SHORT).show();
        }

        //------------------------------------------------------------ Avec SQL
        Cursor cursSQL = dbR.rawQuery("select id from Favoris order by id ASC", null);

        List<Integer> listIDRecette = new ArrayList<>();
        // Le traitement des résultats est similaire à haut dessus.
        // Y'a t'il au moins un résultat ?
        if (cursSQL.moveToFirst()) {
            // Parcours des résultats
            do {
                // Récupération des données par le numéro de colonne
                //long clientID = c urs.getLong(0);
                // ou avec le nom de la colonne (sans doute à privilégier pour la relecture du code)
                Log.v(TAG, "id = " + cursSQL.getLong(cursSQL.getColumnIndexOrThrow("id")));
                listIDRecette.add((int) cursSQL.getLong(cursSQL.getColumnIndexOrThrow("id")));

            } while (cursSQL.moveToNext());
        }
        else{
            Log.v("Favoris", "Pas de réponses.....");
            return listIDRecette;
        }

        // ferme la connexion en lecture à la BDD -- à vous de voir s'il faut ou non conserver la connexion ouverte ... Attention aux ressources...
        return listIDRecette;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.recetteRandom) {
            //Intent intent = new Intent(this, RecetteRandom.class);
            //startActivity(intent);
            //return true;
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
        return false;
    }

    public void doSomething(Recipe valeur) {
        Intent intent = new Intent(this, Recette.class);
        intent.putExtra("id", valeur.getId());
        intent.putExtra("nom", valeur.getName());
        intent.putExtra("ingredients", valeur.getIngredients());
        intent.putExtra("mesures", valeur.getMesures());
        intent.putExtra("steps", valeur.getSteps());
        intent.putExtra("image", valeur.getImageURL());
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

        List<Integer> listeFavoris = this.litDonnées(bdd);
        Log.v("Favoris", "listeFavoris = " + listeFavoris);

        if(!listeFavoris.isEmpty()) {
            ListView listView = findViewById(R.id.listfavoris);

            // Initialize the ArrayAdapter here
            adapter = new CustomAdapter(this, listItem);
            listView.setAdapter(adapter);
            for (int id : listeFavoris) {
                RecipeFetcher fetcher = new RecipeFetcher(id, this);
                new Thread(fetcher).start();
            }
            adapter.notifyDataSetChanged();

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // Récupérer l'élément sélectionné à partir de l'adaptateur de la liste
                    Recipe valeurSelectionnee = (Recipe) parent.getItemAtPosition(position);
                    System.out.println(valeurSelectionnee);
                    doSomething(valeurSelectionnee);
                }
            });
        } else {
            Toast.makeText(this, "Pas de recettes en favoris", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRecipeFetched(Recipe recipe) {
        runOnUiThread(() -> {
            listItem.add(recipe);
            adapter.notifyDataSetChanged();
        });
    }
}