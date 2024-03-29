package com.example.kitchenwizard;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class Recette  extends Activity {
    @Override protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recette);
        TextView nom = findViewById(R.id.textView2);
        TextView steps = findViewById(R.id.textView4);
        nom.setText(getIntent().getStringExtra("nom"));
        ImageView image = findViewById(R.id.imageView2);
        Picasso.get().load("image").into(image);
        steps.setText(getIntent().getStringExtra("steps"));
    }

    public void doSomething() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}