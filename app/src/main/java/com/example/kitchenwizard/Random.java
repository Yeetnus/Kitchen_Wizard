package com.example.kitchenwizard;

        import android.content.Intent;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.Menu;
        import android.view.MenuInflater;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.Button;
        import androidx.activity.EdgeToEdge;
        import androidx.appcompat.app.AppCompatActivity;

        import org.json.JSONArray;
        import org.json.JSONObject;

        import java.net.HttpURLConnection;
        import java.net.URL;
        import java.util.Scanner;

public class Random extends AppCompatActivity {
    public static final String TAG = "Random";


    /*public void doSomething(String valeur) {
        Intent intent = new Intent(this, Recette.class);
        intent.putExtra("pokemon", valeur);
        startActivity(intent);
    }*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.random);
        Button reroll = (Button)findViewById(R.id.PASOK);
        reroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent();
                intent.putExtra("MESSAGE", "reroll");
                setResult(2, intent);
                finish();//finishing activity
            }
        });
    }

    public static Recipe patata() {
        try {
            URL url = new URL("https://www.themealdb.com/api/json/v1/1/random.php\n");
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
}