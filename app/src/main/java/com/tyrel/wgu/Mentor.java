package com.tyrel.wgu;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class Mentor extends AppCompatActivity {

    private static final int COURSE_EDITOR_REQUEST_CODE = 200;
    private String action;
    private EditText mentorNameDisplay;
    private String mentorFilter;
    private String oldMentorName;
    private String oldMentorPhoneNumber;
    private String oldMentorEmail;
    private EditText mentorPhoneNumberDisplay;
    private EditText mentorEmailDisplay;
    private String courseSaver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mentor);

        mentorNameDisplay = findViewById(R.id.mentorTitleText);
        mentorPhoneNumberDisplay = findViewById(R.id.mentorPhoneNumber);
        mentorEmailDisplay = findViewById(R.id.mentorEmail);

        loadMentorData();
    }


    private void loadMentorData() {

        Intent intent = getIntent();
        Uri uri = intent.getParcelableExtra(DatabaseProvider.MENTOR_CONTENT_ITEM_TYPE);
        courseSaver = intent.getStringExtra(DatabaseProvider.COURSE_SAVER);
        //if the ur is null then create a new note else display the currently selected notes text
        if (uri == null) {
            action = Intent.ACTION_INSERT;
            setTitle(R.string.new_mentor);
        } else {
            action = Intent.ACTION_EDIT;
            //make a where clause for the query uri.getLastPathSegment() gets the id from the selected item
            mentorFilter = DBOpenHelper.MENTOR_ID + "=" + uri.getLastPathSegment();
            //run the query and save the results to the cursor
            Cursor cursor = getContentResolver().query(uri, DBOpenHelper.ALL_MENTOR_COLUMNS, mentorFilter, null, null);
            cursor.moveToFirst();
            //set the oldMentorName string to the value retrieved from the query
            oldMentorName = cursor.getString(cursor.getColumnIndex(DBOpenHelper.MENTOR_NAME));
            //set the mentorNameDisplay text to the oldMentorName
            mentorNameDisplay.setText(oldMentorName);
            //select the terms start date with the cursor
            oldMentorPhoneNumber = cursor.getString(cursor.getColumnIndex(DBOpenHelper.MENTOR_PHONE));
            //set the display to show the term start date
            mentorPhoneNumberDisplay.setText(oldMentorPhoneNumber);
            //select the term end date with the cursor
            oldMentorEmail = cursor.getString(cursor.getColumnIndex(DBOpenHelper.MENTOR_EMAIL));
            //set the display to show the term end date
            mentorEmailDisplay.setText(oldMentorEmail);
            //requestFocus() to place the cursor at the end of the text
            mentorNameDisplay.requestFocus();
        }
    }

    private void finishEditing() {
        String newMentorName = mentorNameDisplay.getText().toString().trim();
        String newMentorPhoneNumber = mentorPhoneNumberDisplay.getText().toString().trim();
        String newMentorEmail = mentorEmailDisplay.getText().toString().trim();

        switch (action) {
            //create a new note
            case Intent.ACTION_INSERT:
                if (newMentorName.length() == 0 && newMentorEmail.length() == 0 && newMentorPhoneNumber.length() == 0) {
                    setResult(RESULT_CANCELED);
                } else {
                    insertMentor(newMentorName, newMentorPhoneNumber, newMentorEmail, courseSaver);
                }
                break;
            //update or delete the term
            case Intent.ACTION_EDIT:
                //if the length is 0 delete the term elseif the text is the same as it was before do nothing else update the term
                if (newMentorName.length() == 0) {
                    deleteMentor();
                } else if (oldMentorName.equals(newMentorName) && oldMentorPhoneNumber.equals(newMentorPhoneNumber) && oldMentorEmail.equals(newMentorEmail)) {
                    setResult(RESULT_CANCELED);
                } else {
                    updateMentor(newMentorName, newMentorPhoneNumber, newMentorEmail, courseSaver);
                }
        }
        finish();
    }

    private void updateMentor(String newMentorName, String newMentorPhoneNumber, String newMentorEmail, String courseSaver) {
        //create a values object and put the text into it
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.MENTOR_NAME, newMentorName);
        values.put(DBOpenHelper.MENTOR_PHONE, newMentorPhoneNumber);
        values.put(DBOpenHelper.MENTOR_EMAIL, newMentorEmail);
        values.put(DBOpenHelper.COURSE_FK, courseSaver);
        //update the data in the database
        getContentResolver().update(DatabaseProvider.MENTOR_URI, values, mentorFilter, null);

        //Display a toast MSG showing that the Term has been updated
        Toast.makeText(this, "Mentor Updated", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
    }

    private void insertMentor(String newMentorName, String newMentorPhoneNumber, String newMentorEmail, String courseSaver) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.MENTOR_NAME, newMentorName);
        values.put(DBOpenHelper.MENTOR_PHONE, newMentorPhoneNumber);
        values.put(DBOpenHelper.MENTOR_EMAIL, newMentorEmail);
        values.put(DBOpenHelper.COURSE_FK, courseSaver);

        getContentResolver().insert(DatabaseProvider.MENTOR_URI, values);
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
                deleteMentor();
                break;
        }

        return true;
    }

    private void deleteMentor() {
        //call getContentResolver to run the delete query and send in the values for th item to be deleted
        getContentResolver().delete(DatabaseProvider.MENTOR_URI, mentorFilter, null);

        Toast.makeText(this, "Mentor Deleted", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

}