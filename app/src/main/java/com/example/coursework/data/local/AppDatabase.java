package com.example.coursework.data.local;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.coursework.data.local.DAO.YogaClassDAO;
import com.example.coursework.data.local.entities.YogaClass;
import com.example.coursework.data.local.entities.YogaCourse;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {YogaCourse.class, YogaClass.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract YogaClassDAO  yogaClassDAO();
    public static volatile  AppDatabase INSTANCE;

    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "yoga_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}

