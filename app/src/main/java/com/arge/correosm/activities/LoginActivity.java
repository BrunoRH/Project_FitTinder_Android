package com.arge.correosm.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.arge.correosm.R;
import com.arge.correosm.activities.AlmunoA.RegisterActivity;
import com.arge.correosm.activities.AlumnoB.RegisterBActivity;
import com.arge.correosm.models.AlumnoB;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.arge.correosm.map_alumnoB;
import com.arge.correosm.map_alumnoA;

import dmax.dialog.SpotsDialog;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText mTextInputEmail;
    TextInputEditText mTextInputPassword;
    Button mButtonLogin;

    AlertDialog mDialog;


    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    SharedPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mTextInputEmail = findViewById(R.id.textInputEmail);
        mTextInputPassword = findViewById(R.id.textInputPassword);
        mButtonLogin = findViewById(R.id.btnLogin);

        mAuth =  FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDialog = new SpotsDialog.Builder().setContext(LoginActivity.this).setMessage("Ingresando").build();

        mPref = getApplicationContext().getSharedPreferences("typeUser", MODE_PRIVATE);

        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

    }


    private void login() {
        String email = mTextInputEmail.getText().toString();
        String password = mTextInputPassword.getText().toString();

        if(!email.isEmpty() && !password.isEmpty()){
            if(password.length() >= 5){
                mDialog.show();
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(LoginActivity.this, "El login se realizo correctamente", Toast.LENGTH_SHORT).show();
                            String typeUser = mPref.getString("user", "");
                            if (typeUser.equals("alumnoA")){
                                Intent intent = new Intent(LoginActivity.this, map_alumnoA.class);
                                startActivity(intent);
                            }else{
                                Intent intent = new Intent(LoginActivity.this, map_alumnoB.class);
                                startActivity(intent);
                            }


                        }else{
                            Toast.makeText(LoginActivity.this, "La contraseña o el password son incorrectos", Toast.LENGTH_SHORT).show();
                        }
                        mDialog.dismiss();
                    }
                });
            }else{
                Toast.makeText(LoginActivity.this, "La contraseña debe tener mas de 5 caractener", Toast.LENGTH_SHORT).show();

            }
        }else{
            Toast.makeText(LoginActivity.this, "Complete los campos", Toast.LENGTH_SHORT).show();

        }
    }

}