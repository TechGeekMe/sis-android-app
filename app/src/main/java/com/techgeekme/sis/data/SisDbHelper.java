package com.techgeekme.sis.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.techgeekme.sis.data.SisContract.AssignmentEntry;
import com.techgeekme.sis.data.SisContract.CourseEntry;
import com.techgeekme.sis.data.SisContract.TestEntry;

/**
 * Created by Apeksha on 03-12-2015.
 */
public class SisDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    // Database name
    private static final String DATABASE_NAME = "sis.db";

    public SisDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_COURSES_TABLE = "CREATE TABLE " + CourseEntry.TABLE_NAME + "(" +
                CourseEntry.COLUMN_COURSE_CODE + " TEXT PRIMARY KEY, " +
                CourseEntry.COLUMN_COURSE_NAME + " TEXT, " +
                CourseEntry.COLUMN_ATTENDANCE_PERCENT + " TEXT, " +
                CourseEntry.COLUMN_CLASSES_ATTENDED + " TEXT, " +
                CourseEntry.COLUMN_CLASSES_HELD + " TEXT, " +
                CourseEntry.COLUMN_CLASSES_ABSENT + " TEXT, " +
                CourseEntry.COLUMN_CLASSES_REMAINING + " TEXT" + ")";

        final String SQL_CREATE_TEST_TABLE = "CREATE TABLE " + TestEntry.TABLE_NAME + "(" +
                TestEntry.COLUMN_COURSE_KEY + " TEXT," +
                TestEntry.COLUMN_TEST_NUMBER + " INTEGER," +
                TestEntry.COLUMN_MARKS + " TEXT, " +
                "FOREIGN KEY (" + TestEntry.COLUMN_COURSE_KEY + ") REFERENCES " +
                CourseEntry.TABLE_NAME + "(" + CourseEntry.COLUMN_COURSE_CODE + "))";

        final String SQL_CREATE_ASSIGNMENT_TABLE = "CREATE TABLE " + AssignmentEntry.TABLE_NAME + "(" +
                AssignmentEntry.COLUMN_COURSE_KEY + " TEXT," +
                AssignmentEntry.COLUMN_TEST_NUMBER + " INTEGER," +
                AssignmentEntry.COLUMN_MARKS + " TEXT, " +
                "FOREIGN KEY (" +
                AssignmentEntry.COLUMN_COURSE_KEY + ") REFERENCES " +
                CourseEntry.TABLE_NAME + "(" + CourseEntry.COLUMN_COURSE_CODE + "))";

        db.execSQL(SQL_CREATE_COURSES_TABLE);
        db.execSQL(SQL_CREATE_TEST_TABLE);
        db.execSQL(SQL_CREATE_ASSIGNMENT_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if exists
        db.execSQL("DROP TABLE IF EXISTS " + CourseEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TestEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + AssignmentEntry.TABLE_NAME);

        // Create tables again
        onCreate(db);
    }
}
