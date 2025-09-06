package com.example.d308app.UI;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
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
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ExcursionDetails extends AppCompatActivity {
    String name;
    String date;
    int excursionID;
    int vacationID;
    TextInputLayout editNameLayout;
    TextInputLayout editDateLayout;
    TextInputEditText editName;
    TextInputEditText editDate;
    Repository repository;
    Excursion currentExcursion;
    Vacation associatedVacation;
    String vacationStart;
    String vacationEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_excursion_details);
        repository = new Repository(getApplication());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        name = getIntent().getStringExtra("name");
        date = getIntent().getStringExtra("date");
        excursionID = getIntent().getIntExtra("id", -1);
        vacationID = getIntent().getIntExtra("vacationID", -1);

        editNameLayout = findViewById(R.id.excursionname_layout);
        editDateLayout = findViewById(R.id.excursiondate_layout);
        editName = (TextInputEditText) editNameLayout.getEditText();
        editDate = (TextInputEditText) editDateLayout.getEditText();

        associatedVacation = repository.getVacationByID(vacationID);
        if (associatedVacation != null) {
            vacationStart = associatedVacation.getStartDate();
            vacationEnd = associatedVacation.getEndDate();
        }

        if (editName != null) editName.setText(name);
        if (editDate != null) editDate.setText(date);


        Button saveButton = findViewById(R.id.saveExcursionButton);
        saveButton.setOnClickListener(v -> {
            if (validateDates()) {
                saveExcursion();
            } else {
                Toast.makeText(this, "Please fix the date error first", Toast.LENGTH_LONG).show();
            }
        });

        editDate.setOnClickListener(v -> {
            showDatePicker(editDate);
        });
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_excursion_details, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()== R.id.excursionsave){
            saveExcursion();
            return true;
        }
        if(item.getItemId()== R.id.excursiondelete) {
            Excursion excursionToDelete = new Excursion(excursionID, name, date, vacationID);
            repository.delete(excursionToDelete);
            finish();
            return true;
        }
        if(item.getItemId()== R.id.excursioncancel) {
            finish();
            return true;
        }
        if(item.getItemId()== R.id.excursionalert) {
            checkExcursionAlerts();
            return true;
        }
        if(item.getItemId()== R.id.excursionshare){
            Intent shareIntent = getShareIntent();
            Intent chooserIntent = Intent.createChooser(shareIntent, "Share your excursion details");
            startActivity(chooserIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @NonNull
    private Intent getShareIntent() {
        String shareText = "I'm going on an excursion";

        if (editName != null) {
            shareText = shareText + ": " + Objects.requireNonNull(editName.getText());
        }
        if (editDate != null) {
            shareText = shareText + " on " + Objects.requireNonNull(editDate.getText());
        }

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        shareIntent.setType("text/plain");
        return shareIntent;
    }
    private void showDatePicker(final TextInputEditText textInputEditText) {
        final Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, yearPicked, monthPicked, dayPicked) -> {
                    Calendar pickedDate = Calendar.getInstance();
                    pickedDate.set(yearPicked, monthPicked, dayPicked);

                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);
                    String formattedDate = sdf.format(pickedDate.getTime());
                    textInputEditText.setText(formattedDate);
                    validateDates();
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private boolean validateDates() {
        editDateLayout.setError(null);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);
        String excDateStr = editDate.getText().toString().trim();
        if (excDateStr.isEmpty()) return true;

        try {
            Date excDate = sdf.parse(excDateStr);
            Date vacStart = vacationStart != null && !vacationStart.isEmpty() ? sdf.parse(vacationStart) : null;
            Date vacEnd = vacationEnd != null && !vacationEnd.isEmpty() ? sdf.parse(vacationEnd) : null;

            if (vacStart != null && excDate.before(vacStart)) {
                editDateLayout.setError("Excursion can't be before vacation start date");
                return false;
            }
            if (vacEnd != null && excDate.after(vacEnd)) {
                editDateLayout.setError("Excursion can't be after vacation end date");
                return false;
            }
        } catch (ParseException e) {
            editDateLayout.setError("Invalid date format");
            return false;
        }
        return true;
    }

    private void checkExcursionAlerts() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);
        Date excDate = null;
        String excText = editDate.getText().toString().trim();
        if (!excText.isEmpty()) {
            try {
                excDate = sdf.parse(excText);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }

        String todayStr = sdf.format(Calendar.getInstance().getTime());
        String msg = "Your excursion: " + editName.getText().toString() + " is starting today!";

        try {
            Date today = sdf.parse(todayStr);
            if (excDate != null && excDate.equals(today)) {
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void saveExcursion() {
        Excursion excursion;
        if (excursionID == -1) {
            if (repository.getAllExcursions().size() == 0) excursionID = 1;
            else
                excursionID = repository.getAllExcursions().get(repository.getAllExcursions().size() - 1).getExcursionID() + 1;
            excursion = new Excursion(excursionID, editName.getText().toString(), editDate.getText().toString(), vacationID);
            repository.insert(excursion);
            this.finish();
        } else {
            try{
                excursion = new Excursion(excursionID, editName.getText().toString(), editDate.getText().toString(), vacationID);
                repository.update(excursion);
                this.finish();
            }
            catch (Exception e){
                throw new RuntimeException(e);
            }
        }
    }
}