package com.tyrel.wgu;

import android.app.DatePickerDialog;
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
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class TermEditorActivity extends AppCompatActivity {

    private static final int COURSE_EDITOR_REQUEST_CODE = 200;
    //used to detect if inserting or updating a term
    private String action;
    private EditText editor;
    private String termFilter;
    private String oldTermText;
    private String oldTermStartDate;
    private String oldTermEndDate;
    private EditText termStartDisplayDate;
    private EditText termEndDisplayDate;
    private DatePickerDialog.OnDateSetListener termStartDateSetListener;
    private DatePickerDialog.OnDateSetListener termEndDateSetListener;
    private String termSaver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_editor);


        loadTermData();
        loadCourseData();
    }


    private void loadTermData() {
        editor = findViewById(R.id.editText);
        termStartDisplayDate = findViewById(R.id.editTermStartDate);
        termEndDisplayDate = findViewById(R.id.editTermEndDate);
        Button addCourseButton = findViewById(R.id.termCourseButton);
        ListView courseList = findViewById(R.id.courseList);
        Intent intent = getIntent();
        Uri uri = intent.getParcelableExtra(DatabaseProvider.TERM_CONTENT_ITEM_TYPE);
        Uri uriTemp = intent.getParcelableExtra(DatabaseProvider.TERM_CONTENT_ITEM_TYPE);
        if (uriTemp != null) {
            termSaver = uriTemp.getLastPathSegment();
        }
        //if the ur is null then create a new note else display the currently selected notes text
        if (uri == null) {
            action = Intent.ACTION_INSERT;
            setTitle(R.string.new_term);
            //if its a new term cant add courses untill it has been added to the database so hide button
            addCourseButton.setVisibility(View.GONE);
            courseList.setVisibility(View.GONE);

        } else {
            //show button
            addCourseButton.setVisibility(View.VISIBLE);
            courseList.setVisibility(View.VISIBLE);
            action = Intent.ACTION_EDIT;
            //make a where clause for the query uri.getLastPathSegment() gets the id from the selected item
            termFilter = DBOpenHelper.TERM_ID + "=" + uri.getLastPathSegment();
            //run the query and save the results to the cursor
            Cursor cursor = getContentResolver().query(uri, DBOpenHelper.ALL_TERM_COLUMNS, termFilter, null, null);
            cursor.moveToFirst();
            //set the oldTermText string to the value retrieved from the query
            oldTermText = cursor.getString(cursor.getColumnIndex(DBOpenHelper.TERM_TITLE));
            //set the editor text to the oldTermText
            editor.setText(oldTermText);
            //select the terms start date with the cursor
            oldTermStartDate = cursor.getString(cursor.getColumnIndex(DBOpenHelper.TERM_START_DATE));
            //set the display to show the term start date
            termStartDisplayDate.setText(oldTermStartDate);
            //select the term end date with the cursor
            oldTermEndDate = cursor.getString(cursor.getColumnIndex(DBOpenHelper.TERM_END_DATE));
            //set the display to show the term end date
            termEndDisplayDate.setText(oldTermEndDate);
            //requestFocus() to place the cursor at the end of the text
            editor.requestFocus();
        }
        datePicker();


    }

    private void datePicker() {
        //term start date
        termStartDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        TermEditorActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, termStartDateSetListener, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Clear", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        termStartDisplayDate.setText("");
                    }
                });
                dialog.show();
            }
        });

        termStartDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;

                String date1 = month + "/" + day + "/" + year;
                termStartDisplayDate.setText(date1);
            }
        };
        //term end date
        termEndDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        TermEditorActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, termEndDateSetListener, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Clear", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        termEndDisplayDate.setText("");
                    }
                });
                dialog.show();
            }
        });

        termEndDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;

                String date = month + "/" + day + "/" + year;
                termEndDisplayDate.setText(date);
            }
        };
    }

    private void finishEditing() {
        String newText = editor.getText().toString().trim();
        String newTermStartDate = termStartDisplayDate.getText().toString();
        String newTermEndDate = termEndDisplayDate.getText().toString();

        switch (action) {
            //create a new note
            case Intent.ACTION_INSERT:
                if (newText.length() == 0) {
                    setResult(RESULT_CANCELED);
                } else {
                    insertTerm(newText, newTermStartDate, newTermEndDate);
                }
                break;
            //update or delete the term
            case Intent.ACTION_EDIT:
                //if the length is 0 delete the term elseif the text is the same as it was before do nothing else update the term
                if (newText.length() == 0) {
                    deleteTerm();
                } else if (oldTermText.equals(newText) && oldTermStartDate.equals(newTermStartDate) && oldTermEndDate.equals(newTermEndDate)) {
                    setResult(RESULT_CANCELED);
                } else {
                    updateTerm(newText, newTermStartDate, newTermEndDate);
                }
        }
        finish();
    }

    private void updateTerm(String newText, String newTermStartDate, String newTermEndDate) {
        //create a values object and put the text into it
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.TERM_TITLE, newText);
        values.put(DBOpenHelper.TERM_START_DATE, newTermStartDate);
        values.put(DBOpenHelper.TERM_END_DATE, newTermEndDate);
        //update the data in the database
        getContentResolver().update(DatabaseProvider.TERM_URI, values, termFilter, null);

        //Display a toast MSG showing that the Term has been updated
        Toast.makeText(this, "Term Updated", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
    }

    private void insertTerm(String newText, String newTermStartDate, String newTermEndDate) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.TERM_TITLE, newText);
        values.put(DBOpenHelper.TERM_START_DATE, newTermStartDate);
        values.put(DBOpenHelper.TERM_END_DATE, newTermEndDate);
        getContentResolver().insert(DatabaseProvider.TERM_URI, values);
        setResult(RESULT_OK);
    }

    @Override
    public void onBackPressed() {
        finishEditing();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finishEditing();
                break;
            case R.id.action_delete:

                deleteTerm();
                break;
        }

        return true;
    }

    private void deleteTerm() {
        //create a reference to the listView object using the id defined in the xml
        ListView courseListView = findViewById(R.id.courseList);
        if (courseListView.getAdapter().getCount() == 0) {
            //call getContentResolver to run the delete query and send in the values for th item to be deleted
            getContentResolver().delete(DatabaseProvider.TERM_URI, termFilter, null);
            Toast.makeText(this, "Term Deleted", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "All Courses must be removed before deleting a Term", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (action.equals(Intent.ACTION_EDIT)) {
            getMenuInflater().inflate(R.menu.menu_editor, menu);
        }
        return true;
    }

    private void loadCourseData() {
        //load the data from the database onto the main screen by running a query
        Cursor cursor = getContentResolver().query(DatabaseProvider.COURSE_URI, DBOpenHelper.ALL_COURSE_COLUMNS, null, null, null, null);
        //array of strings including a list of the columns that you want to display in the layout
        String[] from = {DBOpenHelper.COURSE_TITLE};
        //list of resource id's to display the list
        int[] to = {android.R.id.text1};
        //create a reference to the listView object using the id defined in the xml
        ListView courseListView = findViewById(R.id.courseList);
        //create a new SimpleCursorAdapter to go through the data
        CursorAdapter cursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, cursor, from, to, 0);
        //pass the data to the list using the cursorAdapter
        courseListView.setAdapter(cursorAdapter);

        //click handler

        courseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //intent to go to the TermEditorActivity
                Intent intent = new Intent(TermEditorActivity.this, Course.class);
                //Uri object that represents the primary key value from the currently selected item in the list from the id argument
                Uri uri = Uri.parse(DatabaseProvider.COURSE_URI + "/" + id);
                //add the uri to the intent with an extra this is to send the data to the activity
                intent.putExtra(DatabaseProvider.COURSE_CONTENT_ITEM_TYPE, uri);
                intent.putExtra(DatabaseProvider.TERM_SAVER, termSaver);
                //start the activity
                startActivityForResult(intent, COURSE_EDITOR_REQUEST_CODE);
            }
        });
    }

    public void openEditorForNewCourse(View view) {
        //when you click the floating action button it opens the Course activity
        Intent intent = new Intent(this, Course.class);
        intent.putExtra(DatabaseProvider.TERM_SAVER, termSaver);
        startActivityForResult(intent, COURSE_EDITOR_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == COURSE_EDITOR_REQUEST_CODE && resultCode == RESULT_OK) {
            loadCourseData();
        }
    }


}
