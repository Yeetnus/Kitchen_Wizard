package com.example.kitchenwizard;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class Favoris extends AppCompatActivity {

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