package com.tyrel.wgu;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

// ContentProvider provides access to the data
public class DatabaseProvider extends ContentProvider {


    public static final String TERM_CONTENT_ITEM_TYPE = "Term";
    public static final String COURSE_CONTENT_ITEM_TYPE = "Course";
    public static final String MENTOR_CONTENT_ITEM_TYPE = "Mentor";
    public static final String ASSESSMENT_CONTENT_ITEM_TYPE = "Assessment";
    public static final String TERM_SAVER = "termSaver";
    public static final String COURSE_SAVER = "courseSaver";
    //AUTHORITY identifies the ContentProvider to the app
    private static final String AUTHORITY = "com.tyrel.wgu.termprovider";
    public static final Uri COURSE_URI = Uri.parse("content://" + AUTHORITY + "/" + DBOpenHelper.TABLE_COURSE);
    public static final Uri MENTOR_URI = Uri.parse("content://" + AUTHORITY + "/" + DBOpenHelper.TABLE_MENTOR);
    public static final Uri ASSESSMENT_URI = Uri.parse("content://" + AUTHORITY + "/" + DBOpenHelper.TABLE_ASSESSMENT);
    //BASE_PATH represents the data set
    private static final String BASE_PATH = "term";
    //identify the authority and base path together
    public static final Uri TERM_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);
    // Constant to identify the requested operation
    private static final int TERM = 1;
    private static final int TERM_ID = 2;
    private static final int COURSE = 3;
    private static final int COURSE_ID = 4;
    private static final int MENTOR = 5;
    private static final int MENTOR_ID = 6;
    private static final int ASSESSMENT = 7;
    private static final int ASSESSMENT_ID = 8;
    //Uri matcher purpose is to parse the uri and tell what operation is requested
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    //static initializer run the first time anything is called from this class used to check what query to run
    static {
        uriMatcher.addURI(AUTHORITY, BASE_PATH, TERM);
        uriMatcher.addURI(AUTHORITY, BASE_PATH + "/#", TERM_ID);
        uriMatcher.addURI(AUTHORITY, DBOpenHelper.TABLE_COURSE, COURSE);
        uriMatcher.addURI(AUTHORITY, DBOpenHelper.TABLE_COURSE + "/#", COURSE_ID);
        uriMatcher.addURI(AUTHORITY, DBOpenHelper.TABLE_MENTOR, MENTOR);
        uriMatcher.addURI(AUTHORITY, DBOpenHelper.TABLE_MENTOR + "/#", MENTOR_ID);
        uriMatcher.addURI(AUTHORITY, DBOpenHelper.TABLE_ASSESSMENT, ASSESSMENT);
        uriMatcher.addURI(AUTHORITY, DBOpenHelper.TABLE_MENTOR + "/#", MENTOR_ID);
    }

    //save the id for the currently selected term for queries
    String termSelectedSaved;
    String courseSelectedSaved;
    private SQLiteDatabase database;

    @Override
    public boolean onCreate() {
        DBOpenHelper helper = new DBOpenHelper(getContext());
        database = helper.getWritableDatabase();
        return true;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String table;
        String[] columns;

        if (uriMatcher.match(uri) == TERM) {
            table = DBOpenHelper.TABLE_TERM;
            columns = DBOpenHelper.ALL_TERM_COLUMNS;
        } else if (uriMatcher.match(uri) == TERM_ID) {
            //making a where clause for the query if there is a selected item
            table = DBOpenHelper.TABLE_TERM;
            columns = DBOpenHelper.ALL_TERM_COLUMNS;
            selection = DBOpenHelper.TERM_ID + "=" + uri.getLastPathSegment();
            termSelectedSaved = uri.getLastPathSegment();
        } else if (uriMatcher.match(uri) == COURSE) {
            //making a where clause for the query if there is a selected item
            table = DBOpenHelper.TABLE_COURSE;
            columns = DBOpenHelper.ALL_COURSE_COLUMNS;
            selection = DBOpenHelper.COURSE_TERM_ID_FK + "=" + termSelectedSaved;
        } else if (uriMatcher.match(uri) == COURSE_ID) {
            //making a where clause for the query if there is a selected item
            table = DBOpenHelper.TABLE_COURSE;
            columns = DBOpenHelper.ALL_COURSE_COLUMNS;
            selection = DBOpenHelper.COURSE_ID + "=" + uri.getLastPathSegment();
            courseSelectedSaved = uri.getLastPathSegment();
        } else if (uriMatcher.match(uri) == MENTOR) {
            table = DBOpenHelper.TABLE_MENTOR;
            columns = DBOpenHelper.ALL_MENTOR_COLUMNS;
            selection = DBOpenHelper.COURSE_FK + "=" + courseSelectedSaved;
        } else if (uriMatcher.match(uri) == MENTOR_ID) {
            //making a where clause for the query if there is a selected item
            table = DBOpenHelper.TABLE_MENTOR;
            columns = DBOpenHelper.ALL_MENTOR_COLUMNS;
            selection = DBOpenHelper.MENTOR_ID + "=" + uri.getLastPathSegment();
        } else if (uriMatcher.match(uri) == ASSESSMENT) {
            table = DBOpenHelper.TABLE_ASSESSMENT;
            columns = DBOpenHelper.ALL_ASSESSMENT_COLUMNS;
            selection = DBOpenHelper.COURSE_FK + "=" + courseSelectedSaved;
        } else /*(uriMatcher.match(uri) == ASSESSMENT_ID)*/ {
            //making a where clause for the query if there is a selected item
            table = DBOpenHelper.TABLE_ASSESSMENT;
            columns = DBOpenHelper.ALL_ASSESSMENT_COLUMNS;
            selection = DBOpenHelper.ASSESSMENT_ID + "=" + uri.getLastPathSegment();
        }


        //return a Cursor with the data queried

        return database.query(table, columns, selection, null, null, null, null);
    }


    @Override
    public String getType(Uri uri) {
        return null;
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        //get the primary key value from the values
        long id = database.insert(uri.getLastPathSegment(), null, values);
        //put the primary key into the uri to be queried
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        //delete an item from the database based on the selection the user clicked on
        return database.delete(uri.getLastPathSegment(), selection, selectionArgs);

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        //update an item from the database based on the selection the user clicked on
        return database.update(uri.getLastPathSegment(), values, selection, selectionArgs);
    }
}
