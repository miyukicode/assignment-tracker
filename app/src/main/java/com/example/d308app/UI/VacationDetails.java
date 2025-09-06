package com.example.d308app.UI;

import android.Manifest;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.d308app.R;
import com.example.d308app.database.Repository;
import com.example.d308app.entities.Excursion;
import com.example.d308app.entities.Vacation;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
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

public class VacationDetails extends AppCompatActivity {
    String name;
    String startdate;
    String enddate;
    String hotel;
    int vacationID;
    TextInputLayout editNameLayout;
    TextInputLayout editHotelLayout;
    TextInputLayout editStartDateLayout;
    TextInputLayout editEndDateLayout;
    TextInputEditText editName;
    TextInputEditText editHotel;
    TextInputEditText editStartDate;
    TextInputEditText editEndDate;
    Repository repository;
    Vacation currentVacation;
    int numExcursions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacation_details);
        name = getIntent().getStringExtra("name");
        startdate = getIntent().getStringExtra("startdate");
        enddate = getIntent().getStringExtra("enddate");
        hotel = getIntent().getStringExtra("hotel");
        vacationID = getIntent().getIntExtra("id", -1);

        editNameLayout = findViewById(R.id.vacationname_layout);
        editHotelLayout = findViewById(R.id.vacationhotel_layout);
        editStartDateLayout = findViewById(R.id.vacationstartdate_layout);
        editEndDateLayout = findViewById(R.id.vacationenddate_layout);
        editName = (TextInputEditText) editNameLayout.getEditText();
        editHotel = (TextInputEditText) editHotelLayout.getEditText();
        editStartDate = (TextInputEditText) editStartDateLayout.getEditText();
        editEndDate = (TextInputEditText) editEndDateLayout.getEditText();

        if (editName != null) editName.setText(name);
        if (editStartDate != null) editStartDate.setText(startdate);
        if (editEndDate != null) editEndDate.setText(enddate);
        if (editHotel != null) editHotel.setText(hotel);

        RecyclerView recyclerView = findViewById(R.id.excursionRV);
        repository = new Repository(getApplication());
        final ExcursionAdapter excursionAdapter = new ExcursionAdapter(this);
        recyclerView.setAdapter(excursionAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<Excursion> filteredExcursions = new ArrayList<>();
        for (Excursion e : repository.getAllExcursions()) {
            if (e.getVacationID() == vacationID) filteredExcursions.add(e);
        }
        excursionAdapter.setExcursions(filteredExcursions);

        TextView noExcursionsTxt = findViewById(R.id.noExcursionsTxt);

        if (filteredExcursions.isEmpty()) {
            noExcursionsTxt.setVisibility(View.VISIBLE);
        } else {
            noExcursionsTxt.setVisibility(View.INVISIBLE);
        }

        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> {
            if (validateDates()) {
                saveVacation();
            } else {
                Toast.makeText(this, "Please fix the date errors before saving", Toast.LENGTH_LONG).show();
            }
        });

        editStartDate.setOnClickListener(v -> {
            showDatePicker(editStartDate);
        });

        editEndDate.setOnClickListener(v -> {
            showDatePicker(editEndDate);
        });


        ExtendedFloatingActionButton fab=findViewById(R.id.floatingActionButton2);
        fab.setOnClickListener(v -> {
            if (validateDates()) {
                saveVacation();
            } else {
                Toast.makeText(this, "Please fix the date errors first", Toast.LENGTH_LONG).show();
                return;
            }
            Intent intent=new Intent(VacationDetails.this, ExcursionDetails.class);
            intent.putExtra("vacationID", vacationID);
            startActivity(intent);
        });
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_vacation_details, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()== R.id.vacationsave){
            saveVacation();
            return true;
        }
        if(item.getItemId()== R.id.vacationdelete) {
            for (Vacation vacay : repository.getAllVacations()) {
                if (vacay.getVacationID() == vacationID) currentVacation = vacay;
            }

            numExcursions = 0;
            for (Excursion excursion : repository.getAllExcursions()) {
                if (excursion.getVacationID() == vacationID) ++numExcursions;
            }

            if (numExcursions == 0) {
                repository.delete(currentVacation);
                finish();
            } else {
                Toast.makeText(VacationDetails.this, "Please delete the existing excursions associated with this vacation first.", Toast.LENGTH_LONG).show();
            }
            return true;
        }
        if(item.getItemId()== R.id.vacationshare){
            Intent shareIntent = getShareIntent();
            Intent chooserIntent = Intent.createChooser(shareIntent, "Share your vacation details");
            startActivity(chooserIntent);
            return true;
        }
        if(item.getItemId()== R.id.vacationalert){
            checkVacationAlerts();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @NonNull
    private Intent getShareIntent() {
        String shareText = "I'm going on vacation";

        if (editName != null) {
            shareText = shareText + " to " + Objects.requireNonNull(editName.getText());
        }
        if (editStartDate != null) {
            shareText = shareText + ", starting on " + Objects.requireNonNull(editStartDate.getText()) + ".";
        }
        if (editEndDate != null) {
            shareText = shareText + " I will be staying at " + Objects.requireNonNull(editHotel.getText());
        }
        if (editEndDate != null) {
            shareText = shareText + " and returning on " + Objects.requireNonNull(editEndDate.getText()) + ".";
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
        editStartDateLayout.setError(null);
        editEndDateLayout.setError(null);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);

        try {
            String startText = editStartDate.getText() != null ? editStartDate.getText().toString().trim() : "";
            String endText = editEndDate.getText() != null ? editEndDate.getText().toString().trim() : "";

            Date startDate = null;
            Date endDate = null;
            Date today = sdf.parse(sdf.format(Calendar.getInstance().getTime()));

            if (!startText.isEmpty()) { startDate = sdf.parse(startText); }
            if (!endText.isEmpty()) { endDate = sdf.parse(endText); }

            if (startDate != null && startDate.before(today)) {
                editStartDateLayout.setError("Start date should not be in the past.");
                return false;
            }
            if (startDate != null && endDate != null && endDate.before(startDate)) {
                editEndDateLayout.setError("End date should not be before start date.");
                return false;
            }

        } catch (Exception e) {
            if (!Objects.requireNonNull(editStartDate.getText()).toString().trim().isEmpty()) {
                editStartDateLayout.setError("Invalid start date format");
            }
            if (!Objects.requireNonNull(editEndDate.getText()).toString().trim().isEmpty()) {
                editEndDateLayout.setError("Invalid end date format");
            }
            return false;
        }
        return true;
    }


    private void checkVacationAlerts() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);
        Date startDate = null;
        Date endDate = null;
        String startText = editStartDate.getText().toString().trim();
        if (!startText.isEmpty()) {
            try {
                startDate = sdf.parse(startText);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }

        String endText = editEndDate.getText().toString().trim();
        if (!endText.isEmpty()) {
            try {
                endDate = sdf.parse(endText);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
        String todayStr = sdf.format(Calendar.getInstance().getTime());

        String msgStart = "Your vacation to " + editName.getText().toString() + " is starting today!";
        String msgEnd = "Your vacation to " + editName.getText().toString() + " is ending today!";

        try {
            Date today = sdf.parse(todayStr);

            if (startDate != null && startDate.equals(today)) {
                Toast.makeText(this, msgStart, Toast.LENGTH_LONG).show();
            }

            if (endDate != null && endDate.equals(today)) {
                String finalMsgEnd = msgEnd;
                new android.os.Handler().postDelayed(() ->
                                Toast.makeText(this, finalMsgEnd, Toast.LENGTH_LONG).show(), 1500);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        RecyclerView recyclerView = findViewById(R.id.excursionRV);
        final ExcursionAdapter excursionAdapter = new ExcursionAdapter(this);
        recyclerView.setAdapter(excursionAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<Excursion> filteredExcursions = new ArrayList<>();
        for (Excursion e : repository.getAllExcursions()) {
            if (e.getVacationID() == vacationID) filteredExcursions.add(e);
        }
        excursionAdapter.setExcursions(filteredExcursions);
        TextView noExcursionsTxt = findViewById(R.id.noExcursionsTxt);
        if (filteredExcursions.isEmpty()) {
            noExcursionsTxt.setVisibility(View.VISIBLE);
        } else {
            noExcursionsTxt.setVisibility(View.INVISIBLE);
        }
    }

    private void saveVacation() {
        Vacation vacation;
        if (vacationID == -1) {
            if (repository.getAllVacations().size() == 0) vacationID = 1;
            else
                vacationID = repository.getAllVacations().get(repository.getAllVacations().size() - 1).getVacationID() + 1;
            vacation = new Vacation(vacationID, editName.getText().toString(), editStartDate.getText().toString(), editEndDate.getText().toString(), editHotel.getText().toString());
            repository.insert(vacation);
            this.finish();
        } else {
            try{
                vacation = new Vacation(vacationID, editName.getText().toString(), editStartDate.getText().toString(), editEndDate.getText().toString(), editHotel.getText().toString());
                repository.update(vacation);
                this.finish();
            }
            catch (Exception e){
                throw new RuntimeException(e);
            }
        }
    }

}