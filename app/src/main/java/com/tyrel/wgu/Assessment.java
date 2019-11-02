package com.tyrel.wgu;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class Assessment extends AppCompatActivity {

    private static final int COURSE_EDITOR_REQUEST_CODE = 200;
    private static final int ALERTTypeAssessmentStartInt = 3;
    private static final int ALERTTypeAssessmentEndInt = 4;
    private String action;
    private EditText assessmentNameDisplay;
    private String oldAssessmentName;
    private String oldAssessmentStartDate;
    private String oldAssessmentEndDate;
    private String courseSaver;
    private Spinner spinner;
    private String assessmentFilter;
    private EditText assessmentStartDateDisplay;
    private EditText assessmentEndDateDisplay;
    private String oldAssessmentSelection;
    private DatePickerDialog.OnDateSetListener assessmentStartDateSetListener;
    private DatePickerDialog.OnDateSetListener assessmentEndDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment);

        assessmentNameDisplay = findViewById(R.id.editAssessmentName);
        assessmentStartDateDisplay = findViewById(R.id.editAssessmentDate);
        assessmentEndDateDisplay = findViewById(R.id.editAssessmentEndDate);
        spinner = findViewById(R.id.assessmentSpinner);

        spinner();
        loadAssessmentData();
        datePicker();
    }

    private void loadAssessmentData() {

        Intent intent = getIntent();
        Uri uri = intent.getParcelableExtra(DatabaseProvider.ASSESSMENT_CONTENT_ITEM_TYPE);
        courseSaver = intent.getStringExtra(DatabaseProvider.COURSE_SAVER);
        //if the ur is null then create a new note else display the currently selected notes text
        if (uri == null) {
            action = Intent.ACTION_INSERT;
            setTitle(R.string.new_assessment);
        } else {
            action = Intent.ACTION_EDIT;
            //make a where clause for the query uri.getLastPathSegment() gets the id from the selected item
            assessmentFilter = DBOpenHelper.ASSESSMENT_ID + "=" + uri.getLastPathSegment();
            //run the query and save the results to the cursor
            Cursor cursor = getContentResolver().query(uri, DBOpenHelper.ALL_ASSESSMENT_COLUMNS, assessmentFilter, null, null);
            cursor.moveToFirst();
            //set the oldAssessmentName string to the value retrieved from the query
            oldAssessmentName = cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_TITLE));
            //set the assessmentNameDisplay text to the oldAssessmentName
            assessmentNameDisplay.setText(oldAssessmentName);
            //select the terms start date with the cursor
            oldAssessmentStartDate = cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_START_DATE));
            //set the display to show the term start date
            assessmentStartDateDisplay.setText(oldAssessmentStartDate);
            //select the terms end date with the cursor
            oldAssessmentEndDate = cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_END_DATE));
            //set the display to show the term start date
            assessmentEndDateDisplay.setText(oldAssessmentEndDate);
            //select the term end date with the cursor
            oldAssessmentSelection = cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_TYPE));
            spinner.setSelection(getIndexOfSpinner(oldAssessmentSelection));

            assessmentNameDisplay.requestFocus();
        }
    }

    private int getIndexOfSpinner(String searchString) {
        if (searchString == null || spinner.getCount() == 0) {
            return -1;
        } else {
            for (int i = 0; i < spinner.getCount(); i++) {
                if (spinner.getItemAtPosition(i).toString().toUpperCase().equals(searchString.toUpperCase())) {
                    return i;
                }
            }
            return -1;
        }
    }

    private void spinner() {

// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_array_assessment, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }

    private void datePicker() {

        assessmentStartDateDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        Assessment.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, assessmentStartDateSetListener, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Clear", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        assessmentStartDateDisplay.setText("");
                    }
                });
                dialog.show();
            }
        });

        assessmentStartDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;

                String date1 = month + "/" + day + "/" + year;
                assessmentStartDateDisplay.setText(date1);
            }
        };

        assessmentEndDateDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        Assessment.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, assessmentEndDateSetListener, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Clear", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        assessmentEndDateDisplay.setText("");
                    }
                });
                dialog.show();
            }
        });

        assessmentEndDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;

                String date1 = month + "/" + day + "/" + year;
                assessmentEndDateDisplay.setText(date1);
            }
        };
    }

    private void finishEditing() {
        String newAssessmentName = assessmentNameDisplay.getText().toString().trim();
        String newAssessmentStartDate = assessmentStartDateDisplay.getText().toString().trim();
        String newAssessmentEndDate = assessmentEndDateDisplay.getText().toString().trim();
        String newSpinnerAssessmentType = spinner.getSelectedItem().toString();


            switch (action) {
                //create a new note
                case Intent.ACTION_INSERT:
                    if (newAssessmentName.length() == 0 && newSpinnerAssessmentType.length() == 0 && newAssessmentStartDate.length() == 0 && newAssessmentEndDate.length() == 0) {
                        setResult(RESULT_CANCELED);
                    } else {
                        insertAssessment(newAssessmentName, newAssessmentStartDate, newAssessmentEndDate, newSpinnerAssessmentType, courseSaver);
                        if (newAssessmentStartDate.length() > 0) {
                            alarmManager(newAssessmentStartDate, ALERTTypeAssessmentStartInt);
                        }
                        if (newAssessmentEndDate.length() > 0) {
                            alarmManager(newAssessmentEndDate, ALERTTypeAssessmentEndInt);
                        }
                    }
                    break;
                //update or delete the term
                case Intent.ACTION_EDIT:
                    //if the length is 0 delete the term elseif the text is the same as it was before do nothing else update the term
                    if (newAssessmentName.length() == 0) {
                        deleteAssessment();
                    } else if (oldAssessmentName.equals(newAssessmentName) && oldAssessmentStartDate.equals(newAssessmentStartDate) && oldAssessmentEndDate.equals(newAssessmentEndDate) && oldAssessmentSelection.equals(newSpinnerAssessmentType)) {
                        setResult(RESULT_CANCELED);
                    } else {
                        updateAssessment(newAssessmentName, newAssessmentStartDate, newAssessmentEndDate, newSpinnerAssessmentType, courseSaver);
                        if (newAssessmentStartDate.length() > 0) {
                            alarmManager(newAssessmentStartDate, ALERTTypeAssessmentStartInt);
                        }
                        if (newAssessmentEndDate.length() > 0) {
                            alarmManager(newAssessmentEndDate, ALERTTypeAssessmentEndInt);
                        }
                    }
        }
        finish();
    }

    private void alarmManager(String inputDateAndRequestCode, int alertTypeInt) {
        //used to create create pendingIntents that will popup notifications when the date happens
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Intent intent = new Intent(this, BroadcastReceiverClass.class);

        String requestCodeFormatted = inputDateAndRequestCode.replaceAll("[/]", "");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, Integer.parseInt(requestCodeFormatted), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //break the string up to be able to input into Calendar
        String[] parts = inputDateAndRequestCode.split("/");
        Calendar objCalendar = Calendar.getInstance();
//        calendar.set(Integer.parseInt(parts[2]), Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
//        year month day
        objCalendar.set(Calendar.YEAR, Integer.parseInt(parts[2]));
        objCalendar.set(Calendar.MONTH, (Integer.parseInt(parts[0]) - 1));
        objCalendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(parts[1]));
        objCalendar.set(Calendar.HOUR_OF_DAY, 11);
        objCalendar.set(Calendar.MINUTE, 59);
        objCalendar.set(Calendar.SECOND, alertTypeInt);
        objCalendar.set(Calendar.MILLISECOND, 0);
        objCalendar.set(Calendar.AM_PM, Calendar.PM);
        alarmManager.set(AlarmManager.RTC_WAKEUP, objCalendar.getTimeInMillis(), pendingIntent);

        Toast toast = Toast.makeText(getApplicationContext(), "Alert Created", Toast.LENGTH_SHORT);
        toast.show();
    }

    private void updateAssessment(String newAssessmentName, String newAssessmentStartDate, String newAssessmentEndDate, String newSpinnerAssessmentType, String courseSaver) {
        //create a values object and put the text into it
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.ASSESSMENT_TITLE, newAssessmentName);
        values.put(DBOpenHelper.ASSESSMENT_START_DATE, newAssessmentStartDate);
        values.put(DBOpenHelper.ASSESSMENT_END_DATE, newAssessmentEndDate);
        values.put(DBOpenHelper.ASSESSMENT_TYPE, newSpinnerAssessmentType);
        values.put(DBOpenHelper.COURSE_FK, courseSaver);
        //update the data in the database
        getContentResolver().update(DatabaseProvider.MENTOR_URI, values, assessmentFilter, null);

        //Display a toast MSG showing that the Term has been updated
        Toast.makeText(this, "Assessment Updated", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
    }

    private void insertAssessment(String newAssessmentName, String newAssessmentStartDate, String newAssessmentEndDate, String newSpinnerAssessmentType, String courseSaver) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.ASSESSMENT_TITLE, newAssessmentName);
        values.put(DBOpenHelper.ASSESSMENT_START_DATE, newAssessmentStartDate);
        values.put(DBOpenHelper.ASSESSMENT_END_DATE, newAssessmentEndDate);
        values.put(DBOpenHelper.ASSESSMENT_TYPE, newSpinnerAssessmentType);
        values.put(DBOpenHelper.COURSE_FK, courseSaver);

        getContentResolver().insert(DatabaseProvider.ASSESSMENT_URI, values);
        setResult(RESULT_OK);
    }

    @Override
    public void onBackPressed() {
        finishEditing();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (action.equals(Intent.ACTION_EDIT)) {
            getMenuInflater().inflate(R.menu.menu_editor, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finishEditing();
                break;
            case R.id.action_delete:
                deleteAssessment();
                break;
        }

        return true;
    }

    private void deleteAssessment() {
        //call getContentResolver to run the delete query and send in the values for th item to be deleted
        getContentResolver().delete(DatabaseProvider.ASSESSMENT_URI, assessmentFilter, null);
        Toast.makeText(this, "Mentor Deleted", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

}
