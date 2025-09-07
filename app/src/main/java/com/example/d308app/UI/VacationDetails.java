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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.d308app.R;
import com.example.d308app.database.Repository;
import com.example.d308app.entities.Assignment;
import com.example.d308app.entities.Course;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
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
    Course currentCourse;
    int numExcursions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacation_details);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Class Details");
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
        List<Assignment> filteredAssignments = new ArrayList<>();
        for (Assignment e : repository.getAllExcursions()) {
            if (e.getVacationID() == vacationID) filteredAssignments.add(e);
        }
        excursionAdapter.setExcursions(filteredAssignments);

        TextView noExcursionsTxt = findViewById(R.id.noExcursionsTxt);

        if (filteredAssignments.isEmpty()) {
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
            for (Course vacay : repository.getAllVacations()) {
                if (vacay.getVacationID() == vacationID) currentCourse = vacay;
            }

            numExcursions = 0;
            for (Assignment assignment : repository.getAllExcursions()) {
                if (assignment.getVacationID() == vacationID) ++numExcursions;
            }

            if (numExcursions == 0) {
                repository.delete(currentCourse);
                finish();
            } else {
                Toast.makeText(VacationDetails.this, "Please delete the existing assignments associated with this class first.", Toast.LENGTH_LONG).show();
            }
            return true;
        }
        if(item.getItemId()== R.id.vacationshare){
            Intent shareIntent = getShareIntent();
            Intent chooserIntent = Intent.createChooser(shareIntent, "Share your class details");
            startActivity(chooserIntent);
            return true;
        }
        if(item.getItemId()== R.id.vacationalert){
            checkVacationAlerts();
            return true;
        }
        if(item.getItemId() == R.id.vacationreport){
            try {
                Course courseToReport = null;
                for (Course vacay : repository.getAllVacations()) {
                    if (vacay.getVacationID() == vacationID) {
                        courseToReport = vacay;
                        break;
                    }
                }

                if (courseToReport != null) {
                    String csvReport = generateVacationReport(courseToReport);
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_TEXT, csvReport);
                    shareIntent.setType("text/plain");
                    startActivity(Intent.createChooser(shareIntent, "Share class report"));
                    Toast.makeText(this, "Class report generated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Class not found", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                Toast.makeText(this, "Failed to generate class report", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @NonNull
    private Intent getShareIntent() {
        String shareText = "I'm taking a class";

        if (editName != null) {
            shareText = shareText + " called " + Objects.requireNonNull(editName.getText());
        }
        if (editStartDate != null) {
            shareText = shareText + ", starting on " + Objects.requireNonNull(editStartDate.getText()) + ".";
        }
        if (editEndDate != null) {
            shareText = shareText + " The subject is " + Objects.requireNonNull(editHotel.getText());
        }
        if (editEndDate != null) {
            shareText = shareText + " and it's ending on " + Objects.requireNonNull(editEndDate.getText()) + ".";
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

        String msgStart = "Your class called " + editName.getText().toString() + " is starting today!";
        String msgEnd = "Your class called " + editName.getText().toString() + " is ending today!";

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

    private String generateVacationReport(Course course) {
        StringBuilder csv = new StringBuilder();
        SimpleDateFormat inputFormat = new SimpleDateFormat("MM/dd/yy", Locale.US);
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        String startDateFormatted = "";
        String endDateFormatted = "";
        try {
            Date start = inputFormat.parse(course.getStartDate());
            Date end = inputFormat.parse(course.getEndDate());
            startDateFormatted = outputFormat.format(start) + " 00:00:00";
            endDateFormatted = outputFormat.format(end) + " 00:00:00";
        } catch (Exception e) {
            e.printStackTrace();
        }

        csv.append("Class Name, Subject, Start Datetime, End Datetime\n");
        csv.append(course.getVacationName()).append(", ");
        csv.append(course.getHotel()).append(", ");
        csv.append(startDateFormatted).append(", ").append(endDateFormatted).append("\n\n");

        List<Assignment> excursionsForVacation = new ArrayList<>();
        for (Assignment e : repository.getAllExcursions()) {
            if (e.getVacationID() == course.getVacationID()) excursionsForVacation.add(e);
        }

        csv.append("Number, Assignment Title, Datetime\n");
        int counter = 1;
        for (Assignment e : excursionsForVacation) {
            String excursionDatetime = "";
            try {
                Date excDate = inputFormat.parse(e.getExcursionDate());
                excursionDatetime = outputFormat.format(excDate) + " 00:00:00";
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            csv.append(counter).append(", ");
            csv.append(e.getExcursionName()).append(", ");
            csv.append(excursionDatetime).append("\n");
            counter++;
        }

        return csv.toString();
    }


    @Override
    protected void onResume() {
        super.onResume();
        RecyclerView recyclerView = findViewById(R.id.excursionRV);
        final ExcursionAdapter excursionAdapter = new ExcursionAdapter(this);
        recyclerView.setAdapter(excursionAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<Assignment> filteredAssignments = new ArrayList<>();
        for (Assignment e : repository.getAllExcursions()) {
            if (e.getVacationID() == vacationID) filteredAssignments.add(e);
        }
        excursionAdapter.setExcursions(filteredAssignments);
        TextView noExcursionsTxt = findViewById(R.id.noExcursionsTxt);
        if (filteredAssignments.isEmpty()) {
            noExcursionsTxt.setVisibility(View.VISIBLE);
        } else {
            noExcursionsTxt.setVisibility(View.INVISIBLE);
        }
    }

    private void saveVacation() {
        Course course;
        if (vacationID == -1) {
            if (repository.getAllVacations().size() == 0) vacationID = 1;
            else
                vacationID = repository.getAllVacations().get(repository.getAllVacations().size() - 1).getVacationID() + 1;
            course = new Course(vacationID, editName.getText().toString(), editStartDate.getText().toString(), editEndDate.getText().toString(), editHotel.getText().toString());
            repository.insert(course);
            this.finish();
        } else {
            try{
                course = new Course(vacationID, editName.getText().toString(), editStartDate.getText().toString(), editEndDate.getText().toString(), editHotel.getText().toString());
                repository.update(course);
                this.finish();
            }
            catch (Exception e){
                throw new RuntimeException(e);
            }
        }
    }

}