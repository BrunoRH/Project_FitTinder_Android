package com.arge.correosm.activities.AlmunoA;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.arge.correosm.R;
import com.arge.correosm.activities.LoginActivity;
import com.arge.correosm.models.AlumnoA;
import com.arge.correosm.providers.AlumnoAprovider;
import com.arge.correosm.providers.AuthProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import dmax.dialog.SpotsDialog;

public class RegisterActivity extends AppCompatActivity {

    SharedPreferences mPref;

    AuthProvider mAuthProvider;
    AlumnoAprovider mAlumnoAprovider;


    /*FirebaseAuth mAuth;
    DatabaseReference mDatabase;*/

    Button mbtnRegister;
    TextInputEditText mTextInputNombre;
    TextInputEditText mTextInputEmail;
    TextInputEditText mTextInputPassword;

    AlertDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuthProvider = new AuthProvider();
        mAlumnoAprovider = new AlumnoAprovider();
/*
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

*/
        mDialog = new SpotsDialog.Builder().setContext(RegisterActivity.this).setMessage("Guardando").build();

        mPref = getApplicationContext().getSharedPreferences("typeUser", MODE_PRIVATE);
        String selecterUser= mPref.getString("user", "");


        mbtnRegister = findViewById(R.id.btnRegister);
        mTextInputNombre =findViewById(R.id.texInputNombre);
        mTextInputEmail =findViewById(R.id.textInputEmail);
        mTextInputPassword =findViewById(R.id.textInputPassword);

        mbtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               ClickRegister();
            }
        });

    }

    void ClickRegister(){
        final String name = mTextInputNombre.getText().toString();
        final String email = mTextInputEmail.getText().toString();
        final String password = mTextInputPassword.getText().toString();

        if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty()){
            Toast.makeText(RegisterActivity.this,"Registro exitoso", Toast.LENGTH_SHORT).show();
            if(password.length()>=5){
                mDialog.show();
                register(name, email, password);
            }else {
                Toast.makeText(this,"La contraseña es muy corta", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this,"Ingrese todos los campos", Toast.LENGTH_SHORT ).show();
        }
        mDialog.dismiss();
    }

    void register(final String name, String email, String password){
        mAuthProvider.register(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    AlumnoA alumnoA = new AlumnoA(id,name,email );
                    create(alumnoA);
                }else{
                    Toast.makeText(RegisterActivity.this,"No se pudo registrar", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    void create(AlumnoA alumnoA){
        mAlumnoAprovider.create(alumnoA).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(RegisterActivity.this,"Registro exitoso", Toast.LENGTH_SHORT).show();

                }else{
                    Toast.makeText(RegisterActivity.this,"No se pudo registrar", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    /*void saveUser(String id, String name, String email){
        String selecterUser= mPref.getString("user", "");
        User user=new User();
        user.setEmail(email);
        user.setName(name);

        if(selecterUser.equals("alumnoB")){
            mDatabase.child("Users").child("alumnoB").child(id).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(RegisterActivity.this, "Registro Exitoso", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(RegisterActivity.this, "Error al registrarse", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }else if(selecterUser.equals("alumnoA")){
            mDatabase.child("Users").child("alumnoA").child(id).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(RegisterActivity.this, "Registro Exitoso", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(RegisterActivity.this, "Error al registrarse", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }*/
}