package com.example.d308app.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.d308app.R;
import com.example.d308app.database.Repository;
import com.example.d308app.entities.Course;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.List;
import java.util.Objects;

public class VacationList extends AppCompatActivity {
    private Repository repository;
    private VacationAdapter vacationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_vacation_list);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Class List");
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ExtendedFloatingActionButton fab=findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(VacationList.this, VacationDetails.class);
                startActivity(intent);
            }
        });

        repository = new Repository(getApplication());
        List<Course> allCourses = repository.getAllVacations();
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        vacationAdapter = new VacationAdapter(this);
        recyclerView.setAdapter(vacationAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        vacationAdapter.setVacations(allCourses);

        androidx.appcompat.widget.SearchView searchView = findViewById(R.id.searchView);
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("Search classes...");
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                vacationAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                vacationAdapter.getFilter().filter(newText);
                return false;
            }
        });

        TextView noVacationsTxt = findViewById(R.id.noVacationsTxt);
        if (allCourses == null || allCourses.isEmpty()) {
            noVacationsTxt.setVisibility(View.VISIBLE);
        } else {
            noVacationsTxt.setVisibility(View.INVISIBLE);
        }

        //System.out.println(getIntent().getStringExtra("test"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_vacation_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == R.id.cleardb) {
            repository.deleteAllExcursions();
            repository.deleteAllVacations();
            Toast.makeText(this, "Database cleared", Toast.LENGTH_SHORT).show();
            vacationAdapter.setVacations(repository.getAllVacations());
            finish();
            return true;
        }
        if(item.getItemId()==R.id.returnhome || item.getItemId()==android.R.id.home) {
            finish();
            return true;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<Course> allCourses = repository.getAllVacations();
        vacationAdapter.setVacations(allCourses);

        TextView noVacationsTxt = findViewById(R.id.noVacationsTxt);
        if (allCourses == null || allCourses.isEmpty()) {
            noVacationsTxt.setVisibility(View.VISIBLE);
        } else {
            noVacationsTxt.setVisibility(View.INVISIBLE);
        }
    }

}