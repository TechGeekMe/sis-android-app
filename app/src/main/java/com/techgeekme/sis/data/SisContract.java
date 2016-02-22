package com.techgeekme.sis.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by anirudh on 13/02/16.
 */
public class SisContract {

    public static final String PATH_COURSE = "course";
    public static final String PATH_TEST = "test";
    public static final String PATH_ASSIGNMENT = "assignment";
    public static final String PATH_ALL_TABLES = "all_tables";
    public static final String CONTENT_AUTHORITY = "com.techgeekme.sis";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final Uri ALL_TABLES_CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_ALL_TABLES).build();

    public static final String CONTENT_TYPE =
            ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY;
    public static final String CONTENT_ITEM_TYPE =
            ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY;

    public static final class CourseEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_COURSE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_COURSE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_COURSE;

        public static final String TABLE_NAME = "course";
        public static final String COLUMN_COURSE_CODE = "course_code";
        public static final String COLUMN_COURSE_NAME = "course_name";
        public static final String COLUMN_CREDITS = "credits";
        public static final String COLUMN_ATTENDANCE_PERCENT = "attendance_percent";
        public static final String COLUMN_CLASSES_ATTENDED = "classes_attended";
        public static final String COLUMN_CLASSES_HELD = "classes_held";

        public static Uri buildCourseUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

    public static final class TestEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TEST).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TEST;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TEST;

        public static final String TABLE_NAME = "test";
        public static final String COLUMN_TEST_NUMBER = "test_num";
        public static final String COLUMN_MARKS = "marks_obtained";
        public static final String COLUMN_COURSE_KEY = "course_code";

        public static Uri buildTestUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildTest(String courseId) {
            return CONTENT_URI.buildUpon().appendPath(courseId).build();
        }

        public static String getCourseCodeFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    public static final class AssignmentEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_ASSIGNMENT).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ASSIGNMENT;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ASSIGNMENT;

        public static final String TABLE_NAME = "assignment";
        public static final String COLUMN_TEST_NUMBER = "test_num";
        public static final String COLUMN_MARKS = "marks_obtained";
        public static final String COLUMN_COURSE_KEY = "course_code";

        public static Uri buildAssignmentUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildAssignment(String courseId) {
            return CONTENT_URI.buildUpon().appendPath(courseId).build();
        }

        public static String getCourseCodeFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }
}
