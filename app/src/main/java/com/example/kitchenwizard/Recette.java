package com.example.kitchenwizard;

        import android.app.Activity;
        import android.content.Intent;
        import android.view.View;
        import android.widget.Button;
        import android.widget.TextView;

public class Recette  extends Activity {
    @Override protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recette);
        TextView label = findViewById(R.id.textView);
        label.setText(getIntent().getStringExtra("pokemon"));

        Button bouton=(Button) findViewById(R.id.button);
        bouton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doSomething();
            }
        });
    }

    public void doSomething() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
