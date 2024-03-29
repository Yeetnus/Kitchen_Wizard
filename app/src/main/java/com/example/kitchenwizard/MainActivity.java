package com.example.kitchenwizard;

import android.content.Context;
import android.content.Intent;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity implements RecipeFetcher.RecipeListener {
    public static final String TAG = "MonTagDeLActivité";
    private List<String> listItem = new ArrayList<>();
    private CustomAdapter adapter;
    private ArrayAdapter<String> imageAdapter;

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

    public static Recipe patata(int id) {
        try {
            id+=52772;
            URL url = new URL("https://www.themealdb.com/api/json/v1/1/lookup.php?i=" + id);
            Log.i(TAG, String.valueOf(url));
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
                List<String> ingredients = new ArrayList<>();
                for (int i = 1; i <= 20; i++) {
                    String ingredient = meal.getString("strIngredient" + i);
                    if (ingredient.isEmpty()) {
                        break;
                    }
                    ingredients.add(ingredient);
                }

                List<String> mesures= new ArrayList<String>();
                for (int i = 1; i <= 20; i++) {
                    String mesure = meal.getString("strMeasure" + i);
                    if (mesure.isEmpty()) {
                        break;
                    }
                    mesures.add(mesure);
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
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ListView listView = findViewById(R.id.list);

        // Initialize the ArrayAdapter here
        adapter = new CustomAdapter(this, new ArrayList<>());
        imageAdapter = new ArrayAdapter<>(this, R.layout.list_recette, R.id.imageView, listItem);
        listView.setAdapter(adapter);

        // Iterate through the recipes and fetch them
        for(int i = 0; i <= 10; i++) {
            Thread thread = new Thread(new RecipeFetcher(i, this));
            thread.start();
        }
        adapter.notifyDataSetChanged();

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
    public void onRecipeFetched(Recipe recipe) {
        // Add the fetched recipe name to the listItem
        runOnUiThread(() -> {
            listItem.add(recipe.getName());
            adapter.notifyDataSetChanged();
        });
    }
}