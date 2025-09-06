package com.example.d308app.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.d308app.dao.ExcursionDAO;
import com.example.d308app.dao.VacationDAO;
import com.example.d308app.entities.Excursion;
import com.example.d308app.entities.Vacation;

@Database(entities = {Vacation.class, Excursion.class}, version=4, exportSchema = false)
public abstract class DatabaseBuilder extends RoomDatabase {
    public abstract VacationDAO vacationDAO();
    public abstract ExcursionDAO excursionDAO();
    public static volatile DatabaseBuilder INSTANCE;

    static DatabaseBuilder getDatabase(final Context context){
        if(INSTANCE==null){
            synchronized (DatabaseBuilder.class){
                if(INSTANCE==null){
                    INSTANCE= Room.databaseBuilder(context.getApplicationContext(),DatabaseBuilder.class, "MyDatabase.db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
