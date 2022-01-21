package com.arge.correosm.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.arge.correosm.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    /*
    Button mButtonA;
    Button mButtonB;

    SharedPreferences mPref;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNavig = findViewById(R.id.bottomNavigationView);
        NavController navController = Navigation.findNavController(this, R.id.fragment);
        NavigationUI.setupWithNavController(bottomNavig, navController);


        /*super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPref = getApplicationContext().getSharedPreferences("typeUser", MODE_PRIVATE);
        SharedPreferences.Editor editor = mPref.edit();

        mButtonA = (Button)findViewById(R.id.btnAlumnoA);
        mButtonB = (Button)findViewById(R.id.btnAlumnoB);

        mButtonA.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                editor.putString("user", "alumnoA");
                editor.apply();
                goToSelectAuth();
            }
        });

        mButtonB.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                editor.putString("user", "alumnoB");
                editor.apply();
                goToSelectAuth();
            }
        });
        */
    }
/*
    private void goToSelectAuth() {
        Intent intent = new Intent(MainActivity.this,SelectOptionAuthActivity.class );
        startActivity(intent);
    }*/


}

















