package com.techgeekme.sis;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Apekshaa on 03-12-2015.
 */
public class DatabaseManager extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    // Database Name
    private static final String DATABASE_NAME = "coursesDB";
    // Contacts table name
    private static final String TABLE_COURSE = "course";
    private static final String TABLE_TEST = "test";
    private static final String TABLE_ASSIGNMENT = "assignment";
    // Contacts Table Columns names
    private static final String KEY_COURSE_CODE = "course_code";
    private static final String KEY_COURSE_NAME = "course_name";
    private static final String KEY_CREDITS = "credits";
    private static final String KEY_ATTENDANCE_PERCENT = "attendance_percent";
    private static final String KEY_CLASSES_ATTENDED = "classes_attended";
    private static final String KEY_CLASSES_HELD = "classes_held";
    private static final String KEY_TEST_NUMBER = "test_num";
    private static final String KEY_MARKS = "marks_obtained";
    private final String TAG = getClass().getSimpleName();


    public DatabaseManager(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.i(TAG, "supposed to create db");
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "in OnCreate DBM");
        String CREATE_COURSES_TABLE = "CREATE TABLE " + TABLE_COURSE + "("
                + KEY_COURSE_CODE + " TEXT PRIMARY KEY," + KEY_COURSE_NAME + " TEXT," + KEY_CREDITS + " TEXT,"
                + KEY_ATTENDANCE_PERCENT + " TEXT," + KEY_CLASSES_ATTENDED + " TEXT," + KEY_CLASSES_HELD + " TEXT" + ")";
        String CREATE_TEST_TABLE = "CREATE TABLE " + TABLE_TEST + "("
                + KEY_COURSE_CODE + " TEXT," + KEY_TEST_NUMBER + " INTEGER," + KEY_MARKS + " TEXT, FOREIGN KEY (" + KEY_COURSE_CODE + ") REFERENCES " + TABLE_COURSE + " (course_code))";
        String CREATE_ASSIGNMENT_TABLE = "CREATE TABLE " + TABLE_ASSIGNMENT + "("
                + KEY_COURSE_CODE + " TEXT," + KEY_TEST_NUMBER + " INTEGER," + KEY_MARKS + " TEXT, FOREIGN KEY (" + KEY_COURSE_CODE + ") REFERENCES " + TABLE_COURSE + " (course_code))";
        db.execSQL(CREATE_COURSES_TABLE);
        db.execSQL(CREATE_TEST_TABLE);
        db.execSQL(CREATE_ASSIGNMENT_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "in Onupgrade DBM");
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEST);


        // Create tables again
        onCreate(db);
    }

    public void putCourses(ArrayList<Course> courses) {
        Log.e(TAG, "addStudent entered");
        SQLiteDatabase db = getWritableDatabase();

        for (int i = 0; i < courses.size(); i++) {
            ContentValues values1 = new ContentValues();
            Course course = courses.get(i);
            values1.put(KEY_COURSE_CODE, course.courseCode);
            values1.put(KEY_COURSE_NAME, course.courseName);
            values1.put(KEY_CREDITS, course.credits);
            values1.put(KEY_ATTENDANCE_PERCENT, course.attendancePercent);
            values1.put(KEY_CLASSES_HELD, course.classesHeld);
            values1.put(KEY_CLASSES_ATTENDED, course.classesAttended);
            ArrayList<String> tests = courses.get(i).tests;
            for (int j = 0; j < tests.size(); j++) {
                ContentValues values2 = new ContentValues();
                values2.put(KEY_COURSE_CODE, course.courseCode);
                values2.put(KEY_TEST_NUMBER, j);
                values2.put(KEY_MARKS, tests.get(j));
                db.insert(TABLE_TEST, null, values2);
            }

            ArrayList<String> assignments = courses.get(i).assignments;
            for (int j = 0; j < assignments.size(); j++) {
                ContentValues values2 = new ContentValues();
                values2.put(KEY_COURSE_CODE, course.courseCode);
                values2.put(KEY_TEST_NUMBER, j);
                values2.put(KEY_MARKS, assignments.get(j));
                db.insert(TABLE_ASSIGNMENT, null, values2);
            }
            db.insert(TABLE_COURSE, null, values1);

        }
        db.close();
    }

    public ArrayList<Course> getCourses() {
        Log.i(TAG, "get student entered");
        String selectQuery = "SELECT * FROM " + TABLE_COURSE;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<Course> courseList = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                Course c = new Course();
                c.courseName = cursor.getString(cursor.getColumnIndex(KEY_COURSE_NAME));
                c.credits = cursor.getString(cursor.getColumnIndex(KEY_CREDITS));
                c.attendancePercent = cursor.getString(cursor.getColumnIndex(KEY_ATTENDANCE_PERCENT));
                c.classesAttended = cursor.getString(cursor.getColumnIndex(KEY_CLASSES_ATTENDED));
                c.classesHeld = cursor.getString(cursor.getColumnIndex(KEY_CLASSES_HELD));
                c.courseCode = cursor.getString(cursor.getColumnIndex(KEY_COURSE_CODE));
                String selectQuery1 = "SELECT  * FROM " + TABLE_TEST + " WHERE " + KEY_COURSE_CODE + "='" + c.courseCode + "'" + " ORDER BY " + KEY_TEST_NUMBER;
                Cursor cursor1 = db.rawQuery(selectQuery1, null);
                if (cursor1.moveToFirst()) {
                    do {
                        c.tests.add(cursor1.getString(cursor1.getColumnIndex(KEY_MARKS)));
                    } while (cursor1.moveToNext());
                }
                selectQuery1 = "SELECT  * FROM " + TABLE_ASSIGNMENT + " WHERE " + KEY_COURSE_CODE + "='" + c.courseCode + "'" + " ORDER BY " + KEY_TEST_NUMBER;
                cursor1 = db.rawQuery(selectQuery1, null);
                if (cursor1.moveToFirst()) {
                    do {
                        c.assignments.add(cursor1.getString(cursor1.getColumnIndex(KEY_MARKS)));
                    } while (cursor1.moveToNext());
                }
                courseList.add(c);
            } while (cursor.moveToNext());
        }

        return courseList;

    }

    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_COURSE);
        db.execSQL("delete from " + TABLE_TEST);
        db.execSQL("delete from " + TABLE_ASSIGNMENT);
    }

}
