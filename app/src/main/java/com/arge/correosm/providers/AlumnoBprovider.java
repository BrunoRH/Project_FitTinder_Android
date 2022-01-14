package com.arge.correosm.providers;

import com.arge.correosm.models.AlumnoB;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AlumnoBprovider {
    DatabaseReference mDatabase;

    public AlumnoBprovider() {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("alumnoB");
    }

    public Task<Void> create(AlumnoB almunoB){
        return mDatabase.child(almunoB.getId()).setValue(almunoB);
    }
}
