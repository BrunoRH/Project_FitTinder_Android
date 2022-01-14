package com.arge.correosm.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.arge.correosm.R;
import com.arge.correosm.activities.AlmunoA.RegisterActivity;
import com.arge.correosm.activities.AlumnoB.RegisterBActivity;

public class SelectOptionAuthActivity extends AppCompatActivity {

    SharedPreferences mPref;

    Button mButtonGoLogin;
    Button mButtonGoRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_option_auth);

        //button Login
        mButtonGoLogin = (Button)findViewById(R.id.btnGoToLogin);
        mButtonGoLogin.setOnClickListener((view -> {goToLogin();}));

        mButtonGoRegister=(Button)findViewById(R.id.btnGotoRegister);
        //mButtonGoRegister.setOnClickListener((view -> {goToRegister();}));
        mButtonGoRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToRegister();
            }
        });

        mPref = getApplicationContext().getSharedPreferences("typeUser", MODE_PRIVATE);

    }

    public void goToLogin() {
        Intent intent = new Intent(SelectOptionAuthActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    public void goToRegister() {
        String typeUser = mPref.getString("user", "");
        if (typeUser.equals("alumnoA")){
            Intent intent = new Intent(SelectOptionAuthActivity.this, RegisterActivity.class);
            startActivity(intent);
        }else {
            Intent intent = new Intent(SelectOptionAuthActivity.this, RegisterBActivity.class);
            startActivity(intent);
        }

    }


}