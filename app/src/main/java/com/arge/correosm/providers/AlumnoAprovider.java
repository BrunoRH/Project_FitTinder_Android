package com.arge.correosm.providers;

import com.arge.correosm.models.AlumnoA;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AlumnoAprovider {
    DatabaseReference mDatabase;

    public AlumnoAprovider() {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("alumnoA");
    }

    public Task<Void> create(AlumnoA almunoA){
        return mDatabase.child(almunoA.getId()).setValue(almunoA);
    }
}
