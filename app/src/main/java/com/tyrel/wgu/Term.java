package com.tyrel.wgu;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class Term extends AppCompatActivity {

    private static final int TERM_EDITOR_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term);

        loadData();
    }


    private void loadData() {
        //load the data from the database onto the main screen by running a query
        Cursor cursor = getContentResolver().query(DatabaseProvider.TERM_URI, DBOpenHelper.ALL_TERM_COLUMNS, null, null, null, null);
        //array of strings including a list of the columns that you want to display in the layout
        String[] from = {DBOpenHelper.TERM_TITLE};
        //list of resource id's to display the list
        int[] to = {android.R.id.text1};
        //create a new SimpleCursorAdapter to go through the data
        CursorAdapter cursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, cursor, from, to, 0);
        //create a reference to the listView object using the id defined in the xml
        ListView listView = findViewById(R.id.list);
        //pass the data to the list using the cursorAdapter
        listView.setAdapter(cursorAdapter);

        //click handler
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //intent to go to the TermEditorActivity
                Intent intent = new Intent(Term.this, TermEditorActivity.class);
                //Uri object that represents the primary key value from the currently selected item in the list from the id argument
                Uri uri = Uri.parse(DatabaseProvider.TERM_URI + "/" + id);
                //add the uri to the intent with an extra this is to send the data to the activity
                intent.putExtra(DatabaseProvider.TERM_CONTENT_ITEM_TYPE, uri);
                //start the activity
                startActivityForResult(intent, TERM_EDITOR_REQUEST_CODE);
            }
        });
    }


    public void openEditorForNewTerm(View view) {
        //when you click the floating action button it opens the term editor activity
        Intent intent = new Intent(this, TermEditorActivity.class);
        startActivityForResult(intent, TERM_EDITOR_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == TERM_EDITOR_REQUEST_CODE && resultCode == RESULT_OK){
            loadData();
        }
    }

}
