package com.example.appv1_1;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;

public class UserConfig extends AppCompatActivity {
    CardView card450, card750, card600, card1100, card2500;
    Button btnenviar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_config);
        card450 = findViewById(R.id.card450);
        card600 = findViewById(R.id.card600);
        card750 = findViewById(R.id.card750);
        card1100 = findViewById(R.id.card1100);
        card2500 = findViewById(R.id.card2500);
        //btnenviar = findViewById(R.id.btnEnviardatos);
IConfiguracciones();

    }
    public void IConfiguracciones( ){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Como configurar");
        builder.setMessage("Selecciona el tanque basandote en la altura de este");
        //  builder.setPositiveButton("OK",null);
        builder.create();
        builder.show();

    }
    public void b1(View view){
if (card450.hasOnClickListeners()) {
    card450.setId(99);

    int datos = card450.getId();

    Bundle parmetros = new Bundle();
    parmetros.putInt("datos", datos);

    Intent i = new Intent(this, MainActivity.class);
    i.putExtras(parmetros);
    startActivity(i);
    finish();
}}
public void b2(View view){
 if (card600.hasOnClickListeners()) {
        card450.setId(112);

        int datos = card600.getId();

        Bundle parmetros = new Bundle();
        parmetros.putInt("datos", datos);

        Intent i = new Intent(this, MainActivity.class);
        i.putExtras(parmetros);
        startActivity(i);
        finish();
    }

}
public void b3(View view){
        if (card750.hasOnClickListeners()) {
            card750.setId(105);

            int datos = card750.getId();

            Bundle parmetros = new Bundle();
            parmetros.putInt("datos", datos);

            Intent i = new Intent(this, MainActivity.class);
            i.putExtras(parmetros);
            startActivity(i);
            finish();
        }
    }
    public void b4(View view){
        if (card1100.hasOnClickListeners()) {
            card1100.setId(140);

            int datos = card1100.getId();

            Bundle parmetros = new Bundle();
            parmetros.putInt("datos", datos);

            Intent i = new Intent(this, MainActivity.class);
            i.putExtras(parmetros);
            startActivity(i);
            finish();
        }
    }
    public void b5(View view){
        if (card2500.hasOnClickListeners()) {
            card2500.setId(160);

            int datos = card2500.getId();

            Bundle parmetros = new Bundle();
            parmetros.putInt("datos", datos);

            Intent i = new Intent(this, MainActivity.class);
            i.putExtras(parmetros);
            startActivity(i);
            finish();

        }
    }

}
