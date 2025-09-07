package com.example.d308app.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.d308app.dao.AssignmentDAO;
import com.example.d308app.dao.CourseDAO;
import com.example.d308app.entities.Assignment;
import com.example.d308app.entities.Course;

@Database(entities = {Course.class, Assignment.class}, version=4, exportSchema = false)
public abstract class DatabaseBuilder extends RoomDatabase {
    public abstract CourseDAO vacationDAO();
    public abstract AssignmentDAO excursionDAO();
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
