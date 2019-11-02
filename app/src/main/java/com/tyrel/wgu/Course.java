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
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;


public class Course extends AppCompatActivity {

    private static final int MENTOR_EDITOR_REQUEST_CODE = 100;
    private static final int ASSESSMENT_EDITOR_REQUEST_CODE = 200;
    private static final String CHANNEL_ID = "1";
    private static final int ALERTTypeCourseStartInt = 1;
    private static final int ALERTTypeCourseEndInt = 2;
    private EditText courseStartDate;
    private EditText courseEndDate;
    private EditText courseNote;
    private DatePickerDialog.OnDateSetListener courseStartDateSetListener;
    private DatePickerDialog.OnDateSetListener courseEndDateSetListener;
    private String action;
    private EditText editor;
    private String courseFilter;
    private String oldCourseText;
    private String oldCourseStartDate;
    private String oldCourseEndDate;
    private String oldCourseSelection;
    private Spinner spinner;
    private String termSaver;
    private String courseSaver;
    private String oldCourseNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        //get data out of the fields in the app
        editor = findViewById(R.id.courseTitleText);
        courseStartDate = findViewById(R.id.courseStartDate);
        courseEndDate = findViewById(R.id.courseEndDate);
        courseNote = findViewById(R.id.courseNoteText);
        spinner = findViewById(R.id.spinner);

        loadCourseData();
        loadMentorData();
        loadAssessmentData();
        spinner();
        datePicker();

        Button button = findViewById(R.id.emailNoteButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "", null));
                intent.putExtra(Intent.EXTRA_SUBJECT, "Course Note");
                intent.putExtra(Intent.EXTRA_TEXT, courseNote.getText());
                startActivity(Intent.createChooser(intent, "Choose an Email client :"));

            }
        });


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

    private void loadCourseData() {

        Intent intent = getIntent();
        Uri uri = intent.getParcelableExtra(DatabaseProvider.COURSE_CONTENT_ITEM_TYPE);
        termSaver = intent.getStringExtra(DatabaseProvider.TERM_SAVER);
        Button addMentorButton = findViewById(R.id.addMentorButton);
        Button addAssessmentButton = findViewById(R.id.addAssessmentButton);
        ListView mentorList = findViewById(R.id.mentorList);
        ListView assessmentList = findViewById(R.id.assessmentList);
        //save the selected course id to pass on to mentor class
        Uri uriTemp = intent.getParcelableExtra(DatabaseProvider.COURSE_CONTENT_ITEM_TYPE);
        if (uriTemp != null) {
            courseSaver = uriTemp.getLastPathSegment();
        }

        //if the ur is null then create a new note else display the currently selected notes text
        if (uri == null) {
            action = Intent.ACTION_INSERT;
            setTitle(R.string.new_course);
            addMentorButton.setVisibility(View.GONE);
            addAssessmentButton.setVisibility(View.GONE);
            mentorList.setVisibility(View.GONE);
            assessmentList.setVisibility(View.GONE);
        } else {
            addMentorButton.setVisibility(View.VISIBLE);
            addAssessmentButton.setVisibility(View.VISIBLE);
            mentorList.setVisibility(View.VISIBLE);
            assessmentList.setVisibility(View.VISIBLE);
            action = Intent.ACTION_EDIT;
            //make a where clause for the query uri.getLastPathSegment() gets the id from the selected item
            courseFilter = DBOpenHelper.COURSE_ID + "=" + uri.getLastPathSegment();
            //run the query and save the results to the cursor
            Cursor cursor = getContentResolver().query(uri, DBOpenHelper.ALL_COURSE_COLUMNS, courseFilter, null, null);
            cursor.moveToFirst();
            //set the oldCourseText string to the value retrieved from the query
            oldCourseText = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_TITLE));
            editor.setText(oldCourseText);
            //select the terms start date with the cursor
            oldCourseStartDate = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_START_DATE));
            courseStartDate.setText(oldCourseStartDate);
            //select the term end date with the cursor
            oldCourseEndDate = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_END_DATE));
            courseEndDate.setText(oldCourseEndDate);
            //load the course note and set the text
            oldCourseNote = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_NOTE));
            courseNote.setText(oldCourseNote);
            //get the course status from the database and set the spinner to the correct selection
            oldCourseSelection = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_STATUS));
            spinner.setSelection(getIndexOfSpinner(oldCourseSelection));
            //requestFocus() to place the cursor at the end of the text
            editor.requestFocus();
        }
        datePicker();
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
                R.array.spinner_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }

    private void loadMentorData() {

        //load the data from the database onto the main screen by running a query
        Cursor cursor = getContentResolver().query(DatabaseProvider.MENTOR_URI, DBOpenHelper.ALL_MENTOR_COLUMNS, null, null, null, null);
        //array of strings including a list of the columns that you want to display in the layout
        String[] from = {DBOpenHelper.MENTOR_NAME};
        //list of resource id's to display the list
        int[] to = {android.R.id.text1};
        //create a new SimpleCursorAdapter to go through the data
        CursorAdapter cursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, cursor, from, to, 0);
        //create a reference to the listView object using the id defined in the xml
        ListView listView = findViewById(R.id.mentorList);
        //pass the data to the list using the cursorAdapter
        listView.setAdapter(cursorAdapter);

        //click handler
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //intent to go to the TermEditorActivity
                Intent intent = new Intent(Course.this, Mentor.class);
                //Uri object that represents the primary key value from the currently selected item in the list from the id argument
                Uri uri = Uri.parse(DatabaseProvider.MENTOR_URI + "/" + id);
                //add the uri to the intent with an extra this is to send the data to the activity
                intent.putExtra(DatabaseProvider.MENTOR_CONTENT_ITEM_TYPE, uri);
                intent.putExtra(DatabaseProvider.COURSE_SAVER, courseSaver);
                //start the activity
                startActivityForResult(intent, MENTOR_EDITOR_REQUEST_CODE);
            }
        });
    }

    public void openEditorForNewMentor(View view) {
        //when you click the floating action button it opens the Course activity
        Intent intent = new Intent(this, Mentor.class);
        intent.putExtra(DatabaseProvider.COURSE_SAVER, courseSaver);
        startActivityForResult(intent, MENTOR_EDITOR_REQUEST_CODE);
    }

    private void loadAssessmentData() {

        //load the data from the database onto the main screen by running a query
        Cursor cursor = getContentResolver().query(DatabaseProvider.ASSESSMENT_URI, DBOpenHelper.ALL_ASSESSMENT_COLUMNS, null, null, null, null);
        //array of strings including a list of the columns that you want to display in the layout
        String[] from = {DBOpenHelper.ASSESSMENT_TITLE};
        //list of resource id's to display the list
        int[] to = {android.R.id.text1};
        //create a new SimpleCursorAdapter to go through the data
        CursorAdapter cursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, cursor, from, to, 0);
        //create a reference to the listView object using the id defined in the xml
        ListView listView = findViewById(R.id.assessmentList);
        //pass the data to the list using the cursorAdapter
        listView.setAdapter(cursorAdapter);

        //click handler
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //intent to go to the TermEditorActivity
                Intent intent = new Intent(Course.this, Assessment.class);
                //Uri object that represents the primary key value from the currently selected item in the list from the id argument
                Uri uri = Uri.parse(DatabaseProvider.ASSESSMENT_URI + "/" + id);
                //add the uri to the intent with an extra this is to send the data to the activity
                intent.putExtra(DatabaseProvider.ASSESSMENT_CONTENT_ITEM_TYPE, uri);
                intent.putExtra(DatabaseProvider.COURSE_SAVER, courseSaver);
                //start the activity
                startActivityForResult(intent, ASSESSMENT_EDITOR_REQUEST_CODE);
            }
        });
    }

    public void openEditorForNewAssessment(View view) {
        //when you click the floating action button it opens the Course activity
        Intent intent = new Intent(this, Assessment.class);
        intent.putExtra(DatabaseProvider.COURSE_SAVER, courseSaver);
        startActivityForResult(intent, ASSESSMENT_EDITOR_REQUEST_CODE);
    }

    private void datePicker() {
        courseStartDate = findViewById(R.id.courseStartDate);
        courseEndDate = findViewById(R.id.courseEndDate);

        courseStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        Course.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, courseStartDateSetListener, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Clear", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        courseStartDate.setText("");
                    }
                });
                dialog.show();
            }
        });

        courseStartDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;

                String date1 = month + "/" + day + "/" + year;
                courseStartDate.setText(date1);
            }
        };

        courseEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        Course.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, courseEndDateSetListener, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Clear", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        courseEndDate.setText("");
                    }
                });
                dialog.show();
            }
        });

        courseEndDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;

                String date1 = month + "/" + day + "/" + year;
                courseEndDate.setText(date1);
            }
        };
    }


    private void finishEditing() {
        String newCourseName = editor.getText().toString().trim();
        String newCourseStartDate = courseStartDate.getText().toString();
        String newCourseEndDate = courseEndDate.getText().toString();
        String newCourseNote = courseNote.getText().toString();
        String newSpinnerCourseStatus = spinner.getSelectedItem().toString();

            switch (action) {
                //create a new note
                case Intent.ACTION_INSERT:
                    if (newCourseName.length() == 0 && newCourseStartDate.length() == 0 && newCourseEndDate.length() == 0 && newCourseNote.length() == 0) {
                        setResult(RESULT_CANCELED);
                    } else {
                        insertCourse(newCourseName, newCourseStartDate, newCourseEndDate, newSpinnerCourseStatus, newCourseNote, termSaver);
                        if (newCourseStartDate.length() > 0) {
                            alarmManager(newCourseStartDate, ALERTTypeCourseStartInt);
                        }
                        if (newCourseEndDate.length() > 0) {
                            alarmManager(newCourseEndDate, ALERTTypeCourseEndInt);
                        }
                    }
                    break;
                //update or delete the term
                case Intent.ACTION_EDIT:
                    //if the length is 0 delete the term elseif the text is the same as it was before do nothing else update the term
                    if (oldCourseText.equals(newCourseName) && oldCourseStartDate.equals(newCourseStartDate) && oldCourseEndDate.equals(newCourseEndDate) && oldCourseSelection.equals(newSpinnerCourseStatus) && oldCourseNote.equals(newCourseNote)) {
                        setResult(RESULT_CANCELED);
                    } else {
                        updateCourse(newCourseName, newCourseStartDate, newCourseEndDate, newSpinnerCourseStatus, newCourseNote, termSaver);
                        if (newCourseStartDate.length() > 0) {
                            alarmManager(newCourseStartDate, ALERTTypeCourseStartInt);
                        }
                        if (newCourseEndDate.length() > 0) {
                            alarmManager(newCourseEndDate, ALERTTypeCourseEndInt);
                        }
                    }

        }

        finish();
    }

    private void updateCourse(String newText, String newTermStartDate, String newTermEndDate, String newSpinnerCourseStatus, String newCourseName, String termSaver) {
        //create a values object and put the text into it
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.COURSE_TITLE, newText);
        values.put(DBOpenHelper.COURSE_START_DATE, newTermStartDate);
        values.put(DBOpenHelper.COURSE_END_DATE, newTermEndDate);
        values.put(DBOpenHelper.COURSE_STATUS, newSpinnerCourseStatus);
        values.put(DBOpenHelper.COURSE_NOTE, newCourseName);
        values.put(DBOpenHelper.COURSE_TERM_ID_FK, termSaver);
        //update the data in the database
        getContentResolver().update(DatabaseProvider.COURSE_URI, values, courseFilter, null);

        //Display a toast MSG showing that the Term has been updated
        Toast.makeText(this, "Course Updated", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
    }

    private void insertCourse(String newText, String newTermStartDate, String newTermEndDate, String newSpinnerCourseStatus, String courseNote, String termSaver) {
        //create a new value and add the data needed for the insert query into it
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.COURSE_TITLE, newText);
        values.put(DBOpenHelper.COURSE_START_DATE, newTermStartDate);
        values.put(DBOpenHelper.COURSE_END_DATE, newTermEndDate);
        values.put(DBOpenHelper.COURSE_STATUS, newSpinnerCourseStatus);
        values.put(DBOpenHelper.COURSE_TERM_ID_FK, termSaver);
        values.put(DBOpenHelper.COURSE_NOTE, courseNote);
        getContentResolver().insert(DatabaseProvider.COURSE_URI, values);
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
                deleteCourse();
                break;
        }

        return true;
    }

    private void deleteCourse() {
        //create a reference to the listView object using the id defined in the xml
        ListView mentorListView = findViewById(R.id.mentorList);
        ListView assessmentListView = findViewById(R.id.assessmentList);
        if (mentorListView.getAdapter().getCount() == 0 && assessmentListView.getAdapter().getCount() == 0) {
            //call getContentResolver to run the delete query and send in the values for th item to be deleted
            getContentResolver().delete(DatabaseProvider.COURSE_URI, courseFilter, null);
            Toast.makeText(this, "Course Deleted", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "All Mentors and Assessments must be removed before deleting a Course", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == MENTOR_EDITOR_REQUEST_CODE && resultCode == RESULT_OK) {
            loadMentorData();
        }
        if (requestCode == ASSESSMENT_EDITOR_REQUEST_CODE && resultCode == RESULT_OK) {
            loadAssessmentData();
        }
    }
}
