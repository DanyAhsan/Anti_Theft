package com.atexcode.antitheft;

import android.app.Application;
        import com.google.firebase.database.FirebaseDatabase;

public class RealTimeApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Enable Firebase Realtime Database persistence
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        // Initialize other Firebase services if needed
        // FirebaseApp.initializeApp(this);
    }
}

