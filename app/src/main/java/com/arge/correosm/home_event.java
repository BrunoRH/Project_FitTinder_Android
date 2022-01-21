package com.arge.correosm;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Button;
import com.arge.correosm.R;
import com.arge.correosm.activities.MainActivity;
import com.arge.correosm.activities.SelectOptionAuthActivity;

public class home_event extends AppCompatActivity {
    Button mButtonA;
    Button mButtonB;

    SharedPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    }
    private void goToSelectAuth() {
        Intent intent = new Intent(home_event.this, SelectOptionAuthActivity.class );
        startActivity(intent);
    }
}
/*
public class home_event extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    public home_event() {

    }


    public static home_event newInstance(String param1, String param2) {
        home_event fragment = new home_event();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_event, container, false);
    }
}*/