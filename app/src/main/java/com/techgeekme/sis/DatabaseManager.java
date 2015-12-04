package com.techgeekme.sis;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Apekshaa on 03-12-2015.
 */
public class DatabaseManager extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "coursesDB";

    // Contacts table name
    private static final String TABLE_COURSES = "courses";
    private static final String TABLE_DETAILS = "details";


    // Contacts Table Columns names
    private static final String KEY_COURSE_NAME = "courseName";
    private static final String KEY_CREDITS = "credits";
    private static final String KEY_ATTENDANCE_PERCENT = "attendancePercent";
    private static final String KEY_CLASSES_ATTENDED = "classesAttended";
    private static final String KEY_CLASSES_HELD = "classesHeld";
    private static final String KEY_TEST1 = "test1";
    private static final String KEY_TEST2 = "test2";
    private static final String KEY_TEST3 = "test3";

    private static final String KEY_NAME = "name";
    private static final String KEY_USN = "usn";

    public DatabaseManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_COURSES_TABLE = "CREATE TABLE " + TABLE_COURSES + "("
                + KEY_COURSE_NAME + " TEXT," + KEY_CREDITS + " TEXT,"
                + KEY_ATTENDANCE_PERCENT + " TEXT," + KEY_CLASSES_ATTENDED + " TEXT," + KEY_CLASSES_HELD + " TEXT" + KEY_TEST1 + " TEXT," + KEY_TEST2 + " TEXT," + KEY_TEST3 + " TEXT," +")";
        String CREATE_DETAILS_TABLE = "CREATE TABLE " + TABLE_DETAILS + "("
                + KEY_NAME + " INTEGER PRIMARY KEY," + KEY_USN + " TEXT," + ")";
        db.execSQL(CREATE_COURSES_TABLE);
        db.execSQL(CREATE_DETAILS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DETAILS);



        // Create tables again
        onCreate(db);
    }
    public void addStudent(Student student) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values2 = new ContentValues();
        values2.put(KEY_NAME, student.studentName);
        values2.put(KEY_USN, student.usn);
        db.insert(TABLE_DETAILS, null, values2);
        for (int i = 0; i < student.courses.size(); i++ ) {
            ContentValues values = new ContentValues();
            values.put(KEY_COURSE_NAME, student.courses.get(i).courseName);
            values.put(KEY_CREDITS, student.courses.get(i).credits);
            values.put(KEY_ATTENDANCE_PERCENT, student.courses.get(i).attendancePercent);
            values.put(KEY_CLASSES_HELD, student.courses.get(i).classesHeld);
            values.put(KEY_CLASSES_ATTENDED, student.courses.get(i).classesAttended);
            values.put(KEY_TEST1, student.courses.get(i).tests.get(0));
            values.put(KEY_TEST2, student.courses.get(i).tests.get(1));
            values.put(KEY_TEST3, student.courses.get(i).tests.get(3));
            db.insert(TABLE_COURSES, null, values);
        }
        db.close();
    }

    public Student getStudent() {
        String selectQuery = "SELECT  * FROM " + TABLE_COURSES;
        String selectQuery1 = "SELECT  * FROM " + TABLE_DETAILS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        Cursor cursor1 = db.rawQuery(selectQuery, null);
        Student student = new Student();
        ArrayList<Course> courseList = new ArrayList<>();
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Course c =  new Course();
                c.courseName = cursor.getString(0);
                c.credits = cursor.getString(1);
                c.attendancePercent = cursor.getString(2);
                c.classesAttended = cursor.getString(3);
                c.classesHeld = cursor.getString(4);
                // Adding contact to list
                courseList.add(c);
            } while (cursor.moveToNext());
        }
        student.courses = (ArrayList<Course>)courseList.clone();

        if (cursor1.moveToFirst()) {
            do {
                student.studentName = cursor1.getString(0);
                student.usn = cursor1.getString(1);
            } while (cursor.moveToNext());
        }
        return student;

    }

}
