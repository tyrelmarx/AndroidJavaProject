package com.tyrel.wgu;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {
//SQLiteOpenHelper is used to define databases and manage connections to it

    //Constants for identifying table and columns, and creating the database
    public static final String TABLE_TERM = "term";
    public static final String TERM_ID = "_id";
    public static final String TERM_TITLE = "termText";
    public static final String TERM_CREATED = "termCreated";
    public static final String TERM_START_DATE = "termStartDate";
    public static final String TERM_END_DATE = "termEndDate";
    public static final String[] ALL_TERM_COLUMNS = {TERM_ID, TERM_TITLE, TERM_CREATED, TERM_START_DATE, TERM_END_DATE};
    //course table
    public static final String TABLE_COURSE = "course";
    public static final String COURSE_ID = "_id";
    public static final String COURSE_TITLE = "courseTitle";
    public static final String COURSE_START_DATE = "courseStartDate";
    public static final String COURSE_END_DATE = "courseEndDate";
    public static final String COURSE_STATUS = "courseStatus";
    public static final String COURSE_NOTE = "courseNote";
    public static final String COURSE_TERM_ID_FK = "termIdFk";
    public static final String[] ALL_COURSE_COLUMNS = {COURSE_ID, COURSE_TITLE, COURSE_START_DATE, COURSE_END_DATE, COURSE_STATUS, COURSE_NOTE, COURSE_TERM_ID_FK};
    //assessment table
    public static final String TABLE_ASSESSMENT = "assessment";
    public static final String ASSESSMENT_ID = "_id";
    public static final String ASSESSMENT_TITLE = "assessmentTitle";
    public static final String ASSESSMENT_TYPE = "assessmentType";
    public static final String ASSESSMENT_START_DATE = "assessmentStartDate";
    public static final String ASSESSMENT_END_DATE = "assessmentEndDate";
    public static final String COURSE_FK = "courseFk";
    public static final String[] ALL_ASSESSMENT_COLUMNS = {ASSESSMENT_ID, ASSESSMENT_TITLE, ASSESSMENT_TYPE, ASSESSMENT_START_DATE, ASSESSMENT_END_DATE, COURSE_FK};
    //mentor table
    public static final String TABLE_MENTOR = "mentor";
    public static final String MENTOR_ID = "_id";
    public static final String MENTOR_NAME = "mentorName";
    public static final String MENTOR_PHONE = "mentorPhone";
    public static final String MENTOR_EMAIL = "mentorEmail";
    public static final String[] ALL_MENTOR_COLUMNS = {MENTOR_ID, MENTOR_NAME, MENTOR_PHONE, MENTOR_EMAIL, COURSE_FK};

    //Constants for db name and version
    private static final String DATABASE_NAME = "term.db";
    private static final int DATABASE_VERSION = 1;
    //SQL to create table
    private static final String TABLE_TERM_CREATE =
            "CREATE TABLE " + TABLE_TERM + " (" +
                    TERM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TERM_TITLE + " TEXT, " +
                    TERM_START_DATE + " TEXT, " +
                    TERM_END_DATE + " TEXT, " +
                    TERM_CREATED + " TEXT default CURRENT_TIMESTAMP" +
                    ")";

    private static final String TABLE_COURSE_CREATE = "CREATE TABLE " + TABLE_COURSE + " ( " +
            COURSE_ID + " integer NOT NULL CONSTRAINT course_pk PRIMARY KEY AUTOINCREMENT, " +
            COURSE_TITLE + " text, " +
            COURSE_START_DATE + " text, " +
            COURSE_END_DATE + " text, " +
            COURSE_STATUS + " text, " +
            COURSE_NOTE + " text, " +
            COURSE_TERM_ID_FK + " integer NOT NULL, " +
            "CONSTRAINT course_term FOREIGN KEY (termIdFk) " +
            "REFERENCES term (_id) " +
            ")";


    private static final String TABLE_ASSESSMENT_CREATE = "CREATE TABLE " + TABLE_ASSESSMENT + " ( " +
            ASSESSMENT_ID + " integer NOT NULL CONSTRAINT assessment_pk PRIMARY KEY AUTOINCREMENT, " +
            ASSESSMENT_TITLE + " text, " +
            ASSESSMENT_TYPE + " text, " +
            ASSESSMENT_START_DATE + " text, " +
            ASSESSMENT_END_DATE + " text, " +
            COURSE_FK + " integer NOT NULL, " +
            "CONSTRAINT assessment_course FOREIGN KEY (courseFk) " +
            "REFERENCES course (_id) " +
            ")";

    private static final String TABLE_MENTOR_CREATE = "CREATE TABLE " + TABLE_MENTOR + " ( " +
            MENTOR_ID + " integer NOT NULL CONSTRAINT mentor_pk PRIMARY KEY AUTOINCREMENT, " +
            MENTOR_NAME + " text, " +
            MENTOR_PHONE + " text, " +
            MENTOR_EMAIL + " text, " +
            COURSE_FK + " integer NOT NULL, " +
            "CONSTRAINT mentor_course FOREIGN KEY (courseFk) " +
            "REFERENCES course (_id) " +
            ")";


    public DBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_TERM_CREATE);
        db.execSQL(TABLE_COURSE_CREATE);
        db.execSQL(TABLE_ASSESSMENT_CREATE);
        db.execSQL(TABLE_MENTOR_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TERM);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ASSESSMENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MENTOR);
        onCreate(db);
    }
}
