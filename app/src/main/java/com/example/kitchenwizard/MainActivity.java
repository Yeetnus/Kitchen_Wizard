package com.example.kitchenwizard;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MonTagDeLActivité";

    private SQLClient bdd;

    // Attention, si vous réexécutez plusieurs fois le programme il faut d'abord
    // supprimer la BDD ou changer la version (puisque le traitement supprime et recréé la BDD)
    public void sauveDonnées(SQLClient bdd){
        // Ouverture d'une connexion en écriture
        SQLiteDatabase dbW = bdd.getWritableDatabase();

        // Pour pouvoir stocker les données envoyées à la BDD sans utilisation de SQL (Avec SQL- cf plus bas)
        // Info d'une première personne
        ContentValues valeursClient1 = new ContentValues();
        valeursClient1.put("id", "1");
        valeursClient1.put("nom", "Chevalier");
        // Insertion dans la BDD
        dbW.insert("Clients", null, valeursClient1);

        //*************************************************************************
        // Info d'une deuxième personne
        ContentValues valeursClient2 = new ContentValues();
        valeursClient2.put("id", "2");
        valeursClient2.put("nom", "Julien");
        // Insertion dans la BDD
        dbW.insert("Clients", null, valeursClient2);

        //--------------------------------------------- Utilisation de SQL
        dbW.execSQL("insert into Clients values(25, 'Roberts');");

        // ferme la connexion en écriture à la BDD -- à vous de voir s'il faut ou non conserver la connexion ouverte ... Attention aux ressources...
        dbW.close();
    }

    public void litDonnées(SQLClient bdd){
        // Ouverture d'une connexion en lecture
        SQLiteDatabase dbR = bdd.getWritableDatabase();

        // Sans SQL (cf plus bas pour SQL)
        String [] critèresDeProjection = {"id", "nom"};
        String [] critèreDeSélection = {}; // Aucun critère donc tous les enregistrements

        // Ouvre un curseur avec le(s) résultat(s)
        Cursor curs = dbR.query("Clients", critèresDeProjection, "", critèreDeSélection, null, null, "nom DESC");

        // Traite les réponses contenues dans le curseur

        // Y'a t'il au moins un résultat ?
        if (curs.moveToFirst()) {
            // Parcours des résultats
            do {
                // Récupération des données par le numéro de colonne
                //long clientID = curs.getLong(0);
                // ou avec le nom de la colonne (sans doute à privilégier pour la relecture du code)
                long clientID = curs.getLong(curs.getColumnIndexOrThrow("id"));
                // déclenche une exception si la colonne n'existe pas cf doc pour autres méthodes disponibles
                String clientNOM = curs.getString(curs.getColumnIndexOrThrow("nom"));

                Log.v(MainActivity.TAG, clientID + " - " + clientNOM);

            } while (curs.moveToNext());
        }
        else{
            Toast.makeText(this, "Pas de réponses.....", Toast.LENGTH_SHORT).show();
        }

        //------------------------------------------------------------ Avec SQL
        Cursor cursSQL = dbR.rawQuery("select id, nom from Clients order by nom ASC", null);

        // Le traitement des résultats est similaire à haut dessus.
        // Y'a t'il au moins un résultat ?
        if (cursSQL.moveToFirst()) {
            // Parcours des résultats
            do {
                // Récupération des données par le numéro de colonne
                //long clientID = curs.getLong(0);
                // ou avec le nom de la colonne (sans doute à privilégier pour la relecture du code)
                long clientID = cursSQL.getLong(cursSQL.getColumnIndexOrThrow("id"));
                // déclenche une exception si la colonne n'existe pas cf doc pour autres méthodes disponibles
                String clientNOM = cursSQL.getString(cursSQL.getColumnIndexOrThrow("nom"));

                Log.v(MainActivity.TAG, clientID + " - " + clientNOM);

            } while (cursSQL.moveToNext());
        }
        else{
            Toast.makeText(this, "Pas de réponses.....", Toast.LENGTH_SHORT).show();
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

    public Recipe patata(int id) {
        try {

            URL url = new URL("www.themealdb.com/api/json/v1/1/lookup.php?i=" + id);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            //Getting the response code
            int responsecode = conn.getResponseCode();

            if (responsecode != 200) {
                throw new RuntimeException("HttpResponseCode: " + responsecode);
            } else {

                String inline = "";
                Scanner scanner = new Scanner(url.openStream());

                //Write all the JSON data into a string using a scanner
                while (scanner.hasNext()) {
                    inline += scanner.nextLine();
                }

                //Close the scanner
                scanner.close();

                //Using the JSON simple library parse the string into a json object
                JSONObject obj = new JSONObject(inline);
                String name = obj.getString("strMeal");
                String imgUrl = obj.getString("strMealThumb");
                List<String> ingredients = new ArrayList<>();
                for (int i = 1; i <= 20; i++) {
                    String ingredient = obj.getString("strIngredient" + i);
                    if (ingredient.isEmpty()) {
                        break;
                    }
                    ingredients.add(ingredient);
                }
                List<String> mesures= new ArrayList<String>();
                for (int i = 1; i <= 20; i++) {
                    String mesure = obj.getString("strMeasure" + i);
                    if (mesure.isEmpty()) {
                        break;
                    }
                    mesures.add(mesure);
                }
                String steps = obj.getString("strInstructions");
                Recipe recette = new Recipe(name, ingredients,mesures,steps, imgUrl);
                return recette;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        this.bdd = new SQLClient(this);

        // Illustration de l'écriture de données dans la BDD
        this.sauveDonnées(bdd);

        //########################################################################
        // Illustration de la lecture de données dans la BDD
        this.litDonnées(bdd);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        List<String> listItem = new ArrayList<>();
        ListView listView = (ListView) findViewById(R.id.list);
        ArrayAdapter<String> adapter =  new ArrayAdapter<>(this, R.layout.list_recette, R.id.textView, listItem);
        listView.setAdapter(adapter);
        for(int i = 0; i <= 2; i++) {
            Recipe recette = patata(52772+i);
            //Log.d(TAG, recette.getName());
        }
        adapter.notifyDataSetChanged();

        ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()){
            Toast.makeText(this, "Connexion OK", Toast.LENGTH_SHORT).show();
// Traitement si le réseau est OK
        }
        else
        {
            Toast.makeText(this, "Pas de Connexion", Toast.LENGTH_SHORT).show();
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Récupérer l'élément sélectionné à partir de l'adaptateur de la liste
                String valeurSelectionnee = (String) parent.getItemAtPosition(position);
                System.out.println(valeurSelectionnee);
                doSomething(valeurSelectionnee);
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // FINISH : Ferme l'instance de BDD ainsi que toutes les connexions ouvertes
        bdd.close();
    }
}