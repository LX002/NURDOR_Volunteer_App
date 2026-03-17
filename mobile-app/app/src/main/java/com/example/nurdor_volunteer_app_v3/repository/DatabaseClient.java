package com.example.nurdor_volunteer_app_v3.repository;

import android.content.Context;

import androidx.room.Room;

public class DatabaseClient {
    private static volatile DatabaseClient mInstance;
    private final AppDatabase appDatabase;

    private DatabaseClient(Context context) {
        appDatabase = Room.databaseBuilder(
                context.getApplicationContext(),
                AppDatabase.class,
                "nurdor_app_schema").fallbackToDestructiveMigration(false).build();
    }

    public static synchronized DatabaseClient getInstance(Context context) {
        if (mInstance == null) {
            synchronized (DatabaseClient.class) {
                if(mInstance == null) {
                    mInstance = new DatabaseClient(context);
                }
            }
        }
        return mInstance;
    }

    public AppDatabase getAppDatabase() {
        return appDatabase;
    }
}
