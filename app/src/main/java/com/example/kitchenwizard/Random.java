package com.example.kitchenwizard;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Random extends AppCompatActivity {
    public static final String TAG = "Random";

    TextView textView;
    ConstraintLayout relativeLayout;
    private List<String> listItem1 = new ArrayList<>();
    private List<String> listItem2 = new ArrayList<>();
    private RecetteAdapter adapter1;


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
        String lang = Locale.getDefault().getLanguage();
        setLocale(lang);

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

        textView = (TextView) findViewById(R.id.popupChoice);
        relativeLayout = (ConstraintLayout) findViewById(R.id.relLayout);
        registerForContextMenu(textView);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Que voulez-vous faire ?");
        menu.add(0, v.getId(), 0, "Je veux !");
        menu.add(0, v.getId(), 0, "Je veux pas !");
    }

    // menu item select listener
    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if (item.getTitle() == "Je veux !") {
            Intent intent = new Intent();
            int id = getIntent().getIntExtra("id", 52772);
            Log.v(TAG, String.valueOf(id));
            intent.putExtra("id", id);
            setResult(2, intent);
            finish();//finishing activity
        } else if (item.getTitle() == "Je veux pas !") {
            Intent intent = new Intent();
            intent.putExtra("PASOK", "Je veux pas !");
            setResult(3, intent);
            finish();//finishing activity
        }
        return true;
    }

    public void ingredientsRecette(int nbPersonnes) {
        listItem1.clear();
        listItem2.clear();
        Log.v(TAG, String.valueOf(getIntent().getStringArrayExtra("ingredients").length));
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

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }
}