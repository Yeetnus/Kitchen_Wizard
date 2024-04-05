package com.example.kitchenwizard;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Recette  extends Activity {
    private static final String TAG = "Recette";
    private List<String> listItem1 = new ArrayList<>();
    private List<String> listItem2 = new ArrayList<>();
    private RecetteAdapter adapter1;
    private SQLClient bdd;
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
    @Override protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recette);
        // Detect system language
        String lang = Locale.getDefault().getLanguage();

        // Set locale to desired language
        setLocale(lang);
        this.bdd = new SQLClient(this);
        TextView nom = findViewById(R.id.textView2);
        TextView steps = findViewById(R.id.textView4);
        nom.setText(getIntent().getStringExtra("nom"));
        ImageView image = findViewById(R.id.imageView2);
        Picasso.get().load(getIntent().getStringExtra("image")).into(image);
        steps.setText(getIntent().getStringExtra("steps"));

        ListView list1 = findViewById(R.id.list1);

        adapter1 = new RecetteAdapter(this, listItem1,listItem2);

        list1.setAdapter(adapter1);
        ingredientsRecette(2);
        RadioButton radio = findViewById(R.id.radioButton2);
        radio.setChecked(true);
        RadioButton radio2 = findViewById(R.id.radioButton);
        RadioGroup radioGroup = findViewById(R.id.radio_group);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioButton2) {
                    ingredientsRecette(2);
                } else if (checkedId == R.id.radioButton) {
                    ingredientsRecette(4);
                }
            }
        });

        Button bouton = (Button)findViewById(R.id.addFavoris);
        bouton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add the recipe to the favorites
                int id = getIntent().getIntExtra("id", 0);
                sauveDonnées(bdd, id);
            }
        });

        Button boutonRetour = (Button)findViewById(R.id.retour);
        boutonRetour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void ingredientsRecette(int nbPersonnes) {
        listItem1.clear();
        listItem2.clear();
        for (int i = 1; i < getIntent().getStringArrayExtra("ingredients").length; i++) {
            if (getIntent().getStringArrayExtra("ingredients")[i] != null) {
                TextView textView = new TextView(this);
                String nomIngredient = getIntent().getStringArrayExtra("ingredients")[i];
                Log.i(TAG, "ingredient: " + nomIngredient);
                int b = getNb(getIntent().getStringArrayExtra("mesures")[i]);
                b *= nbPersonnes;
                Log.i(TAG, "Quantité: " + b);
                String c = " ";
                String quantitéIngredient = getIntent().getStringArrayExtra("mesures")[i];
                if(nbPersonnes>=4 && quantitéIngredient.charAt(0)=='\u00bd'){
                    quantitéIngredient="1"+quantitéIngredient.substring(1);
                    b=1;
                }
                if (quantitéIngredient.length() >= 3) {
                    if (Character.isDigit(quantitéIngredient.charAt(2))) {
                        c = getIntent().getStringArrayExtra("mesures")[i].substring(3);
                        Log.i("oui", "c: " + c);
                    } else if (Character.isDigit(quantitéIngredient.charAt(1))) {
                        c = getIntent().getStringArrayExtra("mesures")[i].substring(2);
                        Log.i("oui", "c: " + c);
                    } else if (Character.isDigit(quantitéIngredient.charAt(0))) {
                        c = getIntent().getStringArrayExtra("mesures")[i].substring(1);
                    } else {
                        c = getIntent().getStringArrayExtra("mesures")[i];
                    }
                }
                if (i % 2 == 1 && b==0) {
                    listItem1.add(nomIngredient + " : " + c);
                } else if(i % 2 == 1) {
                    listItem1.add(nomIngredient + " : " + b + " " + c);
                }else if(b==0 && i % 2 == 0) {
                    listItem2.add(nomIngredient + " : " + c);
                }else{
                    listItem2.add(nomIngredient + " : " + b + " " + c);
                }
                adapter1.notifyDataSetChanged();
            }
        }
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }


    public int getNb(String s) {
        String qte = "";
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) != ' ' && Character.isDigit(s.charAt(i))) {
                qte += s.charAt(i);
            } else {
                break;
            }
        }
        if (qte.length() > 2) {
            if (qte.charAt(2) == '/') {
                return Integer.parseInt("" + qte.charAt(0)) / Integer.parseInt("" + qte.charAt(2));
            }else{
                return Integer.parseInt(qte);
            }
        } else if(qte.length() >= 1){
            return Integer.parseInt(qte);
        }
        return 0;
    }

    public void doSomething() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}