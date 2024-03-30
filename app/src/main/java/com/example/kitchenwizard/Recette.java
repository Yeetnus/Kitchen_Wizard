package com.example.kitchenwizard;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
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

public class Recette  extends Activity {
    private static final String TAG = "Recette";
    private List<String> listItem1 = new ArrayList<>();
    private List<String> listItem2 = new ArrayList<>();
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
        this.bdd = new SQLClient(this);
        TextView nom = findViewById(R.id.textView2);
        TextView steps = findViewById(R.id.textView4);
        nom.setText(getIntent().getStringExtra("nom"));
        ImageView image = findViewById(R.id.imageView2);
        Picasso.get().load(getIntent().getStringExtra("image")).into(image);
        steps.setText(getIntent().getStringExtra("steps"));

        ListView list1 = findViewById(R.id.list1);
        ListView list2 = findViewById(R.id.list2);

        list1.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {}

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // Get the first visible position of listView1
                int firstVisiblePosition = list1.getFirstVisiblePosition();
                // Scroll listView2 to the same position
                list2.setSelection(firstVisiblePosition);
            }
        });

        RecetteAdapter adapter1 = new RecetteAdapter(this, listItem1);
        RecetteAdapter adapter2 = new RecetteAdapter(this, listItem2);

        list1.setAdapter(adapter1);
        list2.setAdapter(adapter2);
        for(int i=1;i<getIntent().getStringArrayExtra("ingredients").length; i++) {
            if(getIntent().getStringArrayExtra("ingredients")[i]!=null) {
                TextView textView = new TextView(this);
                String nomIngredient = getIntent().getStringArrayExtra("ingredients")[i];
                Log.i(TAG, "ingredient: " + nomIngredient);
                int b = getNb(getIntent().getStringArrayExtra("mesures")[i]);
                Log.i(TAG, "Quantité: " + b);
                String c = " ";
                String quantitéIngredient = getIntent().getStringArrayExtra("mesures")[i];
                if (quantitéIngredient.length() >= 3) {
                    if(Character.isDigit(quantitéIngredient.charAt(2))) {
                        c = getIntent().getStringArrayExtra("mesures")[i].substring(3);
                        Log.i("oui", "c: " + c);
                    }else if(Character.isDigit(quantitéIngredient.charAt(1))){
                        c = getIntent().getStringArrayExtra("mesures")[i].substring(2);
                        Log.i("oui", "c: " + c);
                    }else if(Character.isDigit(quantitéIngredient.charAt(0))){
                        c = getIntent().getStringArrayExtra("mesures")[i].substring(1);
                    }else{
                        c = getIntent().getStringArrayExtra("mesures")[i];
                    }
                }
                Log.i(TAG, "mesure: " + c);
                if(i%2==1) {
                    listItem1.add(nomIngredient + " : " + b +" "+ c);
                    adapter1.notifyDataSetChanged();
                } else {
                    listItem2.add(nomIngredient + " : " + b +" "+ c);
                    adapter2.notifyDataSetChanged();
                }
            }
        }
        RadioButton radio = findViewById(R.id.radioButton2);
        RadioButton radio2 = findViewById(R.id.radioButton);
        RadioGroup radioGroup = findViewById(R.id.radio_group);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the ID of the checked radio button
                switch (checkedId) {
                    //case radio:
                        // Do something when radio button 1 is checked
                        //break;
                    //case radio2:
                        // Do something when radio button 2 is checked
                        //break;
                    // Add more cases for additional radio buttons if needed
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
        return 1;
    }

    public void doSomething() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}