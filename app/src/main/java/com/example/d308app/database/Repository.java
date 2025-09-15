package com.example.d308app.database;

import android.app.Application;

import com.example.d308app.dao.AssignmentDAO;
import com.example.d308app.dao.CourseDAO;
import com.example.d308app.entities.Assignment;
import com.example.d308app.entities.Course;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Repository {
    private AssignmentDAO mAssignmentDAO;
    private CourseDAO mCourseDAO;

    private List<Course> mAllCourses;
    private List<Assignment> mAllAssignments;

    private static int NUMBER_OF_THREADS=4;
    static final ExecutorService databaseExecutor= Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public Repository(Application application){
        DatabaseBuilder db=DatabaseBuilder.getDatabase(application);
        mAssignmentDAO =db.excursionDAO();
        mCourseDAO = db.vacationDAO();
    }

    public List<Course>getAllVacations(){
        databaseExecutor.execute(()->{
            mAllCourses = mCourseDAO.getAllVacations();
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return mAllCourses;
    }
    public void insert(Course course){
        databaseExecutor.execute(()->{
            mCourseDAO.insert(course);
        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public void update(Course course){
        databaseExecutor.execute(()->{
            mCourseDAO.update(course);
        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public void delete(Course course){
        databaseExecutor.execute(()->{
            mCourseDAO.delete(course);
        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public List<Assignment>getAllExcursions(){
        databaseExecutor.execute(()->{
            mAllAssignments = mAssignmentDAO.getAllExcursions();
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return mAllAssignments;
    }
    public List<Assignment>getAssociatedExcursions(int vacationID){
        databaseExecutor.execute(()->{
            mAllAssignments = mAssignmentDAO.getAssociatedExcursions(vacationID);
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return mAllAssignments;
    }
    public void insert(Assignment assignment){
        databaseExecutor.execute(()->{
            mAssignmentDAO.insert(assignment);
        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public void update(Assignment assignment){
        databaseExecutor.execute(()->{
            mAssignmentDAO.update(assignment);
        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public void delete(Assignment assignment){
        databaseExecutor.execute(()->{
            mAssignmentDAO.delete(assignment);
        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteAllVacations() {
        databaseExecutor.execute(() -> {
            mCourseDAO.deleteAllVacations();
        });
    }

    public void deleteAllExcursions() {
        databaseExecutor.execute(() -> {
            mAssignmentDAO.deleteAllExcursions();
        });
    }

    public Course getVacationByID(int vacationID) {
        final Course[] course = new Course[1];
        databaseExecutor.execute(() -> {
            course[0] = mCourseDAO.getVacationById(vacationID);
        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return course[0];
    }
}
