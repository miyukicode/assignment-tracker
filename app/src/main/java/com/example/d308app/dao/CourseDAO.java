package com.example.d308app.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.d308app.entities.Course;

import java.util.List;

@Dao
public interface CourseDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Course course);

    @Update
    void update(Course course);

    @Delete
    void delete(Course course);

    @Query("SELECT * FROM Course ORDER BY vacationID ASC")
    List<Course> getAllVacations();

    @Query("SELECT * FROM Course WHERE vacationID=:prod ORDER BY vacationID ASC")
    List<Course> getAssociatedVacations(int prod);

    @Query("DELETE FROM Course")
    void deleteAllVacations();

    @Query("SELECT * FROM Course WHERE vacationID = :vacationID LIMIT 1")
    Course getVacationById(int vacationID);
}
