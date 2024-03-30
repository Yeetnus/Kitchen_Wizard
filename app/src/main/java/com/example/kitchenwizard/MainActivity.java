package com.example.kitchenwizard;

import android.annotation.SuppressLint;
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
import android.widget.SearchView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity implements RecipeFetcher.RecipeListener {
    public static final String TAG = "MonTagDeLActivité";
    private List<Recipe> listItem = new ArrayList<>();
    private CustomAdapter adapter;

    private SQLClient bdd;

    // Attention, si vous réexécutez plusieurs fois le programme il faut d'abord
    // supprimer la BDD ou changer la version (puisque le traitement supprime et recréé la BDD)
    /*public void sauveDonnées(SQLClient bdd){
     // Ouverture d'une connexion en écriture
     SQLiteDatabase dbW = bdd.getWritableDatabase();

     // Pour pouvoir stocker les données envoyées à la BDD sans utilisation de SQL (Avec SQL- cf plus bas)
     // Info d'une première personne
     ContentValues valeursClient1 = new ContentValues();
     valeursClient1.put("id", "1");
     // Insertion dans la BDD
     dbW.insert("Favoris", null, valeursClient1);

     //*************************************************************************
     // Info d'une deuxième personne
     ContentValues valeursClient2 = new ContentValues();
     valeursClient2.put("id", "2");
     // Insertion dans la BDD
     dbW.insert("Favoris", null, valeursClient2);

     //--------------------------------------------- Utilisation de SQL
     dbW.execSQL("insert into Favoris values(25);");

     // ferme la connexion en écriture à la BDD -- à vous de voir s'il faut ou non conserver la connexion ouverte ... Attention aux ressources...
     dbW.close();
     }*/

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

    public static Recipe patata(int id) {
        try {
            id+=52772;
            URL url = new URL("https://www.themealdb.com/api/json/v1/1/lookup.php?i=" + id);
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
                JSONArray mealsArray = obj.getJSONArray("meals");
                JSONObject meal = mealsArray.getJSONObject(0);
                String name = meal.getString("strMeal");
                String imgUrl = meal.getString("strMealThumb");
                String[] ingredients = new String[20];
                for (int i = 1; i <= 20; i++) {
                    String ingredient = meal.getString("strIngredient" + i);
                    if (ingredient.isEmpty()) {
                        break;
                    }
                    ingredients[i]=ingredient;
                }

                String[] mesures= new String[20];
                for (int i = 1; i <= 20; i++) {
                    String mesure = meal.getString("strMeasure" + i);
                    if (mesure.isEmpty()) {
                        break;
                    }
                    mesures[i]=mesure;
                }
                String steps = meal.getString("strInstructions");
                Recipe recette = new Recipe(name, ingredients, mesures, steps, imgUrl);
                Log.i(TAG, "oui");
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
        //this.sauveDonnées(bdd);


        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ListView listView = findViewById(R.id.list);

        // Initialize the ArrayAdapter here
        adapter = new CustomAdapter(this, listItem);
        listView.setAdapter(adapter);

        // Iterate through the recipes and fetch them
        for(int i = 0; i <= 10; i++) {
            Thread thread = new Thread(new RecipeFetcher(i, this));
            thread.start();
        }
        adapter.notifyDataSetChanged();
        Log.i(TAG, "onCreate: " + listItem.size());

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Récupérer l'élément sélectionné à partir de l'adaptateur de la liste
                Recipe valeurSelectionnee = (Recipe) parent.getItemAtPosition(position);
                System.out.println(valeurSelectionnee);
                doSomething(valeurSelectionnee);
            }
        });
        SearchView searchView = findViewById(R.id.chercher);

// Set a query listener to perform search when text changes
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Perform search operation here (e.g., filter your list)
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Perform search operation as the text changes
                // Filter your list based on the newText and update the UI
                return true;
            }
        });
    }

    public void doSomething(Recipe valeur) {
        Intent intent = new Intent(this, Recette.class);
        intent.putExtra("nom", valeur.getName());
        intent.putExtra("ingredients", valeur.getIngredients());
        intent.putExtra("mesures", valeur.getMesures());
        intent.putExtra("steps", valeur.getSteps());
        intent.putExtra("image", valeur.getImageURL());
        startActivity(intent);
    }

    @Override
    public void onRecipeFetched(Recipe recipe) {
        // Add the fetched recipe name to the listItem
        runOnUiThread(() -> {
            listItem.add(recipe);
            adapter.notifyDataSetChanged();
        });
    }
    protected void onDestroy() {
        super.onDestroy();
        // FINISH : Ferme l'instance de BDD ainsi que toutes les connexions ouvertes
        bdd.close();
    }
}