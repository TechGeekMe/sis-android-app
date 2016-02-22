package com.techgeekme.sis;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;

import com.techgeekme.sis.data.SisContract;

import java.util.ArrayList;

import static com.techgeekme.sis.LogUtils.LOGD;

/**
 * Created by anirudh on 14/02/16.
 */
public class SisLoader extends AsyncTaskLoader<ArrayList<Course>> {
    private static final String LOG_TAG = SisLoader.class.getSimpleName();
    private static final String[] TEST_COLUMNS = new String[]{SisContract.TestEntry.COLUMN_MARKS};
    // Tied to TEST_COLUMNS that are used for projections
    private static final int COL_TEST_MARKS = 0;
    private static final String[] ASSIGNMENT_COLUMNS = new String[]{SisContract.AssignmentEntry.COLUMN_MARKS};
    // Tied to ASSIGNMENT_COLUMNS that are used for projections
    private static final int COL_ASSIGNMENT_MARKS = 0;

    public SisLoader(Context context) {
        super(context);
    }

    @Override
    public ArrayList<Course> loadInBackground() {
        ArrayList<Course> courseList = new ArrayList<>();
        String testSortOrder = SisContract.TestEntry.COLUMN_MARKS;
        String assignmentSortOrder = SisContract.AssignmentEntry.COLUMN_MARKS;

        Cursor courseCursor = getContext().getContentResolver().query(
                SisContract.CourseEntry.CONTENT_URI,
                CourseColumns.COURSE_COLUMN_NAMES,
                null,
                null,
                null);
        if (courseCursor != null) {
            while (courseCursor.moveToNext()) {
                Course c = new Course();
                c.courseName = courseCursor.getString(CourseColumns.COL_COURSE_NAME);
                c.credits = courseCursor.getString(CourseColumns.COL_CREDITS);
                c.attendancePercent = courseCursor.getString(CourseColumns.COL_ATTENDANCE_PERCENT);
                c.classesAttended = courseCursor.getString(CourseColumns.COL_CLASSES_ATTENDED);
                c.classesHeld = courseCursor.getString(CourseColumns.COL_CLASSES_HELD);
                c.courseCode = courseCursor.getString(CourseColumns.COL_COURSE_CODE);
                Cursor testCursor = getContext().getContentResolver().query(
                        SisContract.TestEntry.buildTest(c.courseCode),
                        TEST_COLUMNS,
                        null,
                        null,
                        testSortOrder
                );
                if (testCursor != null) {
                    while (testCursor.moveToNext()) {
                        c.tests.add(testCursor.getString(COL_TEST_MARKS));
                    }
                    testCursor.close();
                }

                Cursor assignmentCursor = getContext().getContentResolver().query(
                        SisContract.AssignmentEntry.buildAssignment(c.courseCode),
                        ASSIGNMENT_COLUMNS,
                        null,
                        null,
                        assignmentSortOrder
                );
                if (assignmentCursor != null) {
                    while (assignmentCursor.moveToNext()) {
                        c.assignments.add(assignmentCursor.getString(COL_ASSIGNMENT_MARKS));
                    }
                    assignmentCursor.close();
                }
                courseList.add(c);
            }
            courseCursor.close();
        }
        return courseList;
    }

    @Override
    protected void onStartLoading() {
        LOGD(LOG_TAG, "On start loading");
        forceLoad();
    }

    @Override
    protected void onStopLoading() {
        super.onStopLoading();
        LOGD(LOG_TAG, "On stop loading");
    }

    static class CourseColumns {

        // These indices are tied to COURSE_COLUMN_NAMES.  If COURSE_COLUMN_NAMES changes, these
        // must change.
        static final int COL_COURSE_CODE = 0;
        static final int COL_COURSE_NAME = 1;
        static final int COL_CREDITS = 2;
        static final int COL_ATTENDANCE_PERCENT = 3;
        static final int COL_CLASSES_ATTENDED = 4;
        static final int COL_CLASSES_HELD = 5;
        private static final String[] COURSE_COLUMN_NAMES = {
                SisContract.CourseEntry.COLUMN_COURSE_CODE,
                SisContract.CourseEntry.COLUMN_COURSE_NAME,
                SisContract.CourseEntry.COLUMN_CREDITS,
                SisContract.CourseEntry.COLUMN_ATTENDANCE_PERCENT,
                SisContract.CourseEntry.COLUMN_CLASSES_ATTENDED,
                SisContract.CourseEntry.COLUMN_CLASSES_HELD
        };
    }
}
