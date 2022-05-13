package com.zoemeow.contactapp;

import android.content.Context;

import androidx.room.Room;

public class DatabaseClient {
    private Context mCtx;
    private static DatabaseClient mInstance;

    private AppDatabase appDatabase;

    private DatabaseClient(Context context) {
        this.mCtx = context;

        appDatabase = Room.databaseBuilder(mCtx, AppDatabase.class, "contact.db").build();
    }

    public AppDatabase getAppDatabase() {
        return appDatabase;
    }

    public static synchronized DatabaseClient getInstance(Context mCtx) {
        if (mInstance == null) {
            mInstance = new DatabaseClient(mCtx);
        }

        return mInstance;
    }
}
