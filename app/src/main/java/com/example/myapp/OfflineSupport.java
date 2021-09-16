package com.example.myapp;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class OfflineSupport extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        /*
        Necessary addition for the app to store the data of the Firebase inside it and be able
        to work while being offline
         */
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
