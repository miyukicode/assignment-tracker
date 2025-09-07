package com.example.d308app.dao;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.d308app.entities.Assignment;

import java.util.List;

@Dao
public interface AssignmentDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Assignment assignment);

    @Update
    void update(Assignment assignment);

    @Delete
    void delete(Assignment assignment);

    @Query("SELECT * FROM Assignment ORDER BY excursionID ASC")
    List<Assignment> getAllExcursions();

    @Query("SELECT * FROM Assignment WHERE excursionID=:prod ORDER BY excursionID ASC")
    List<Assignment> getAssociatedExcursions(int prod);

    @Query("DELETE FROM Assignment")
    void deleteAllExcursions();
}
