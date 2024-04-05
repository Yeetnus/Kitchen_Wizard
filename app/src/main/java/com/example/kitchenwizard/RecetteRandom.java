package com.example.kitchenwizard;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class RecetteRandom extends AppCompatActivity implements RandomRecipeFetcher.RecipeListener{
    public static final String TAG = "RecetteRandom";
    private SQLClient bdd;
    private List<Recipe> listItem = new ArrayList<>();
    private CustomAdapter adapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.recettes) {
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

    public void doSomething(Recipe valeur) {
        Intent intent = new Intent(this, Random.class);
        intent.putExtra("id", valeur.getId());
        intent.putExtra("nom", valeur.getName());
        intent.putExtra("ingredients", valeur.getIngredients());
        intent.putExtra("mesures", valeur.getMesures());
        intent.putExtra("steps", valeur.getSteps());
        intent.putExtra("image", valeur.getImageURL());
        startActivityForResult(intent, 2);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        EdgeToEdge.enable(this);
        setContentView(R.layout.recetterandom);
        this.bdd = new SQLClient(this);
        ListView listView = findViewById(R.id.listRandom);
        adapter = new CustomAdapter(this, listItem);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        Button bouton = (Button)findViewById(R.id.buttonRandom);
        bouton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Thread thread = new Thread(new RandomRecipeFetcher(RecetteRandom.this));
                thread.start();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==2) {
            Log.v(TAG, "ça a bien renvoyé 2");
            Log.v(TAG, "resultCode: " + resultCode);
            Log.v(TAG, "data: " + data.getIntExtra("id", 0));
            sauveDonnées(bdd, data.getIntExtra("id", 0));
            Toast.makeText(this, "Recette ajoutée aux favoris", Toast.LENGTH_LONG).show();
        }
    }


    public static Recipe randomPatata() {
        Log.v(TAG, "dododo");
        try {
            URL url = new URL("https://www.themealdb.com/api/json/v1/1/random.php\n");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            //Getting the response code
            int responsecode = conn.getResponseCode();

            if (responsecode != 200) {
                Log.v(TAG, "popopo");
                throw new RuntimeException("HttpResponseCode: " + responsecode);
            } else {
                Log.v(TAG, "blblbl");
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
                Log.v(TAG, "meal: " + meal);
                String name = meal.getString("strMeal");
                String imgUrl = meal.getString("strMealThumb");
                String[] ingredients = new String[100];
                for (int i = 1; i <= 100; i++) {
                    String ingredient = meal.getString("strIngredient" + i);
                    if (ingredient.isEmpty()) {
                        break;
                    }
                    ingredients[i]=ingredient;
                }
                String[] mesures= new String[100];
                for (int i = 1; i <= 100; i++) {
                    String mesure = meal.getString("strMeasure" + i);
                    if (mesure.isEmpty()) {
                        break;
                    }
                    mesures[i]=mesure;
                }
                String steps = meal.getString("strInstructions");
                int id = meal.getInt("idMeal");
                Recipe recette = new Recipe(id, name, ingredients, mesures, steps, imgUrl);
                Log.i(TAG, "oui");
                return recette;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onRecipeFetched(Recipe recipe) {
        runOnUiThread(() -> {
            doSomething(recipe);
        });
    }

    public void sauveDonnées(SQLClient bdd, int id){
        // Ouverture d'une connexion en écriture
        SQLiteDatabase dbW = bdd.getWritableDatabase();
        try {
            id-=52772;
            dbW.execSQL("INSERT INTO Favoris (id) VALUES (" + id + ")");
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors de l'insertion dans la BDD", e);

            Toast.makeText(this, "Cette recette est déjà en favoris", Toast.LENGTH_SHORT).show();
        }

        // ferme la connexion en écriture à la BDD -- à vous de voir s'il faut ou non conserver la connexion ouverte ... Attention aux ressources...
        dbW.close();
    }
}