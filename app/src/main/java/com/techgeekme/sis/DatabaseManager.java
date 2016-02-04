package com.techgeekme.sis;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Apeksha on 03-12-2015.
 */
public class DatabaseManager extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    // Database name
    private static final String DATABASE_NAME = "coursesDB";
    // Table names
    private static final String TABLE_COURSE = "course";
    private static final String TABLE_TEST = "test";
    private static final String TABLE_ASSIGNMENT = "assignment";
    // Table column names
    private static final String KEY_COURSE_CODE = "course_code";
    private static final String KEY_COURSE_NAME = "course_name";
    private static final String KEY_CREDITS = "credits";
    private static final String KEY_ATTENDANCE_PERCENT = "attendance_percent";
    private static final String KEY_CLASSES_ATTENDED = "classes_attended";
    private static final String KEY_CLASSES_HELD = "classes_held";
    private static final String KEY_TEST_NUMBER = "test_num";
    private static final String KEY_MARKS = "marks_obtained";

    public DatabaseManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
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
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ASSIGNMENT);

        // Create tables again
        onCreate(db);
    }

    public void putCourses(ArrayList<Course> courses) {
        SQLiteDatabase db = getWritableDatabase();

        for (int i = 0; i < courses.size(); i++) {
            ContentValues courseValues = new ContentValues();
            Course course = courses.get(i);
            courseValues.put(KEY_COURSE_CODE, course.courseCode);
            courseValues.put(KEY_COURSE_NAME, course.courseName);
            courseValues.put(KEY_CREDITS, course.credits);
            courseValues.put(KEY_ATTENDANCE_PERCENT, course.attendancePercent);
            courseValues.put(KEY_CLASSES_HELD, course.classesHeld);
            courseValues.put(KEY_CLASSES_ATTENDED, course.classesAttended);
            ArrayList<String> tests = course.tests;
            for (int j = 0; j < tests.size(); j++) {
                ContentValues testValues = new ContentValues();
                testValues.put(KEY_COURSE_CODE, course.courseCode);
                testValues.put(KEY_TEST_NUMBER, j);
                testValues.put(KEY_MARKS, tests.get(j));
                db.insert(TABLE_TEST, null, testValues);
            }

            ArrayList<String> assignments = courses.get(i).assignments;
            for (int j = 0; j < assignments.size(); j++) {
                ContentValues assignmentValues = new ContentValues();
                assignmentValues.put(KEY_COURSE_CODE, course.courseCode);
                assignmentValues.put(KEY_TEST_NUMBER, j);
                assignmentValues.put(KEY_MARKS, assignments.get(j));
                db.insert(TABLE_ASSIGNMENT, null, assignmentValues);
            }
            db.insert(TABLE_COURSE, null, courseValues);
        }
        db.close();
    }

    public ArrayList<Course> getCourses() {
        String selectQuery = "SELECT * FROM " + TABLE_COURSE;
        SQLiteDatabase db = getReadableDatabase();
        Cursor courseCursor = db.rawQuery(selectQuery, null);
        ArrayList<Course> courseList = new ArrayList<>();
        while (courseCursor.moveToNext()) {
            Course c = new Course();
            c.courseName = courseCursor.getString(courseCursor.getColumnIndex(KEY_COURSE_NAME));
            c.credits = courseCursor.getString(courseCursor.getColumnIndex(KEY_CREDITS));
            c.attendancePercent = courseCursor.getString(courseCursor.getColumnIndex(KEY_ATTENDANCE_PERCENT));
            c.classesAttended = courseCursor.getString(courseCursor.getColumnIndex(KEY_CLASSES_ATTENDED));
            c.classesHeld = courseCursor.getString(courseCursor.getColumnIndex(KEY_CLASSES_HELD));
            c.courseCode = courseCursor.getString(courseCursor.getColumnIndex(KEY_COURSE_CODE));
            String testQuery = "SELECT  * FROM " + TABLE_TEST + " WHERE " + KEY_COURSE_CODE + "='" + c.courseCode + "'" + " ORDER BY " + KEY_TEST_NUMBER;
            Cursor testCursor = db.rawQuery(testQuery, null);
            while (testCursor.moveToNext()) {
                c.tests.add(testCursor.getString(testCursor.getColumnIndex(KEY_MARKS)));
            }
            String assignmentQuery = "SELECT  * FROM " + TABLE_ASSIGNMENT + " WHERE " + KEY_COURSE_CODE + "='" + c.courseCode + "'" + " ORDER BY " + KEY_TEST_NUMBER;
            testCursor = db.rawQuery(assignmentQuery, null);
            while (testCursor.moveToNext()) {
                c.assignments.add(testCursor.getString(testCursor.getColumnIndex(KEY_MARKS)));
            }
            courseList.add(c);
        }
        courseCursor.close();
        db.close();
        return courseList;
    }

    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_COURSE);
        db.execSQL("delete from " + TABLE_TEST);
        db.execSQL("delete from " + TABLE_ASSIGNMENT);
        db.close();
    }

}
