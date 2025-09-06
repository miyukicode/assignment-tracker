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
import com.example.d308app.entities.Excursion;
import com.example.d308app.entities.Vacation;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class VacationList extends AppCompatActivity {
    private Repository repository;
    private VacationAdapter vacationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_vacation_list);
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
        List<Vacation> allVacations = repository.getAllVacations();
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        vacationAdapter = new VacationAdapter(this);
        recyclerView.setAdapter(vacationAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        vacationAdapter.setVacations(allVacations);

        TextView noVacationsTxt = findViewById(R.id.noVacationsTxt);
        if (allVacations == null || allVacations.isEmpty()) {
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
        if(item.getItemId()==R.id.mysample){
            repository=new Repository(getApplication());
            Vacation france=new Vacation(1, "France", "3/1/26","3/20/26", "Hilton");
            repository.insert(france);

            Excursion tour=new Excursion(1, "tour", "3/2/26",1);
            repository.insert(tour);

            Vacation japan=new Vacation(2, "Japan", "11/20/25","11/27/25", "Hyatt");
            repository.insert(japan);

            Excursion hiking=new Excursion(2, "hiking","11/24/25",2);
            repository.insert(hiking);

            Toast.makeText(this, "Sample data added", Toast.LENGTH_SHORT).show();
            vacationAdapter.setVacations(repository.getAllVacations());
            finish();
            return true;
        }
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
        List<Vacation> allVacations = repository.getAllVacations();
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final VacationAdapter vacationAdapter = new VacationAdapter(this);
        recyclerView.setAdapter(vacationAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        vacationAdapter.setVacations(allVacations);

        TextView noVacationsTxt = findViewById(R.id.noVacationsTxt);
        if (allVacations == null || allVacations.isEmpty()) {
            noVacationsTxt.setVisibility(View.VISIBLE);
        } else {
            noVacationsTxt.setVisibility(View.INVISIBLE);
        }
    }

}