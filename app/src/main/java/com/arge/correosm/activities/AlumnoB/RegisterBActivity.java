package com.arge.correosm.activities.AlumnoB;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.arge.correosm.R;
import com.arge.correosm.activities.AlmunoA.RegisterActivity;
import com.arge.correosm.models.AlumnoA;
import com.arge.correosm.models.AlumnoB;
import com.arge.correosm.providers.AlumnoAprovider;
import com.arge.correosm.providers.AlumnoBprovider;
import com.arge.correosm.providers.AuthProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterBActivity extends AppCompatActivity {
    SharedPreferences mPref;

    AuthProvider mAuthProvider;
    AlumnoBprovider mAlumnoBprovider;


    /*FirebaseAuth mAuth;
    DatabaseReference mDatabase;*/

    Button mbtnRegister;
    TextInputEditText mTextInputNombre;
    TextInputEditText mTextInputEmail;
    TextInputEditText mTextInputPassword;
    TextInputEditText mTextInputEdad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_bactivity);

        mAuthProvider = new AuthProvider();
        mAlumnoBprovider = new AlumnoBprovider();
/*
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
*/
        mPref = getApplicationContext().getSharedPreferences("typeUser", MODE_PRIVATE);
        String selecterUser= mPref.getString("user", "");


        mbtnRegister = findViewById(R.id.btnRegister);
        mTextInputNombre =findViewById(R.id.texInputNombre);
        mTextInputEmail =findViewById(R.id.textInputEmail);
        mTextInputPassword =findViewById(R.id.textInputPassword);
        mTextInputEdad =findViewById(R.id.textInputEdad);

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
        final String edad = mTextInputEdad.getText().toString();

        if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty() && !edad.isEmpty()){

            if(password.length()>=5){
                register(name, email, password, edad);
            }else {
                Toast.makeText(this,"La contraseña es muy corta", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this,"Ingrese todos los campos", Toast.LENGTH_SHORT ).show();
        }
    }

    void register(final String name, String email, String password, String edad){
        mAuthProvider.register(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    AlumnoB alumnoB = new AlumnoB(id,name,email,edad);
                    create(alumnoB);
                }else{
                    Toast.makeText(RegisterBActivity.this,"No se pudo registrar(register)", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    void create(AlumnoB alumnoB){
        mAlumnoBprovider.create(alumnoB).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(RegisterBActivity.this,"Registro exitoso", Toast.LENGTH_SHORT).show();

                }else{
                    Toast.makeText(RegisterBActivity.this,"No se pudo registrar", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}