package com.arge.correosm.providers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.arge.correosm.models.Token;
import com.arge.correosm.services.MyFirebaseMessaginAlumnoA;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.internal.FirebaseInstanceIdInternal;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.messaging.FirebaseMessaging;


public class TokenProvider {

    DatabaseReference mDatabase;

    public TokenProvider(){
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Tokens");
    }

    public void create(final String idUser){
        MyFirebaseMessaginAlumnoA my = new MyFirebaseMessaginAlumnoA();

       /* FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( TokenProvider.this,  new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String mToken = instanceIdResult.getToken();
                Log.e("Token",mToken);
            }
        });*/
        if (idUser == null) return;
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(@NonNull String s) {
                String token = s;
                Token mytoken = new Token(token);

                mDatabase.child(idUser).setValue(mytoken);
            }
        });



    }

    public DatabaseReference getToken(String idUser){
        return mDatabase.child(idUser);
    }


}















