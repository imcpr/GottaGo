package com.palindromicstudios.gottago;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by Natan on 11/15/2014.
 */
public class App extends Application {
    private final String APPLICATION_ID="SJzvb7dpPUIzlPa9wtWsqasByPeZtI8PZBdKJYPB";
    private final String CLIENT_KEY = "QPM6GqmHy5E47OtuYfQ8XSc6LD3TCU9C2nFlaYCf";

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, APPLICATION_ID, CLIENT_KEY);
    }
}
