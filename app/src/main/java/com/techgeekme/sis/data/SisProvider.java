package com.techgeekme.sis.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by anirudh on 14/02/16.
 */
public class SisProvider extends ContentProvider {
    static final int COURSE = 100;
    static final int TEST = 200;
    static final int ASSIGNMENT = 300;
    static final int TEST_WITH_COURSE = 201;
    static final int ASSIGNMENT_WITH_COURSE = 301;
    static final int ALL_TABLES = 400;
    private static final String LOG_TAG = SisProvider.class.getSimpleName();
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private SisDbHelper mOpenHelper;

    static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = SisContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, SisContract.PATH_COURSE, COURSE);
        matcher.addURI(authority, SisContract.PATH_ASSIGNMENT, ASSIGNMENT);
        matcher.addURI(authority, SisContract.PATH_TEST, TEST);
        matcher.addURI(authority, SisContract.PATH_ASSIGNMENT + "/*", ASSIGNMENT_WITH_COURSE);
        matcher.addURI(authority, SisContract.PATH_TEST + "/*", TEST_WITH_COURSE);
        matcher.addURI(authority, SisContract.PATH_ALL_TABLES, ALL_TABLES);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new SisDbHelper(getContext());
        return true;
    }

    private Cursor getAssignmentsByCourseCode(Uri uri, String[] projection, String sortOrder) {
        String courseCode = SisContract.AssignmentEntry.getCourseCodeFromUri(uri);
        String selection = "course_code = ?";
        String[] selectionArgs = new String[]{courseCode};
        return mOpenHelper.getReadableDatabase().query(
                SisContract.AssignmentEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getTestsByCourseCode(Uri uri, String[] projection, String sortOrder) {
        String courseCode = SisContract.TestEntry.getCourseCodeFromUri(uri);
        String selection = "course_code = ?";
        String[] selectionArgs = new String[]{courseCode};
        return mOpenHelper.getReadableDatabase().query(
                SisContract.TestEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor = null;

        switch (sUriMatcher.match(uri)) {
            case COURSE:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        SisContract.CourseEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case TEST_WITH_COURSE:
                retCursor = getTestsByCourseCode(uri, projection, sortOrder);
                break;
            case ASSIGNMENT_WITH_COURSE:
                retCursor = getAssignmentsByCourseCode(uri, projection, sortOrder);
                break;
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }


    @Nullable
    @Override
    public String getType(Uri uri) {
        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            // Student: Uncomment and fill out these two cases
            case ALL_TABLES:
                return SisContract.CONTENT_TYPE;
            case COURSE:
                return SisContract.CourseEntry.CONTENT_TYPE;
            case TEST:
            case TEST_WITH_COURSE:
                return SisContract.TestEntry.CONTENT_TYPE;
            case ASSIGNMENT:
            case ASSIGNMENT_WITH_COURSE:
                return SisContract.AssignmentEntry.CONTENT_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;
        switch (match) {
            case COURSE: {
                long _id = db.insert(SisContract.CourseEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = SisContract.CourseEntry.buildCourseUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case TEST: {
                long _id = db.insert(SisContract.TestEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = SisContract.TestEntry.buildTestUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case ASSIGNMENT: {
                long _id = db.insert(SisContract.AssignmentEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = SisContract.AssignmentEntry.buildAssignmentUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if (null == selection) selection = "1";
        switch (match) {
            case ALL_TABLES:
                rowsDeleted = db.delete(
                        SisContract.CourseEntry.TABLE_NAME, selection, selectionArgs);
                rowsDeleted += db.delete(
                        SisContract.AssignmentEntry.TABLE_NAME, selection, selectionArgs);
                rowsDeleted += db.delete(
                        SisContract.TestEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case COURSE:
                rowsDeleted = db.delete(
                        SisContract.CourseEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TEST_WITH_COURSE:
                rowsDeleted = db.delete(
                        SisContract.TestEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case ASSIGNMENT_WITH_COURSE:
                rowsDeleted = db.delete(
                        SisContract.AssignmentEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        String tableName;
        switch (match) {
            case COURSE:
                tableName = SisContract.CourseEntry.TABLE_NAME;
                break;
            case ASSIGNMENT:
                tableName = SisContract.AssignmentEntry.TABLE_NAME;
                break;
            case TEST:
                tableName = SisContract.TestEntry.TABLE_NAME;
                break;
            default:
                return super.bulkInsert(uri, values);
        }

        db.beginTransaction();
        int returnCount = 0;
        try {
            for (ContentValues value : values) {
                long _id = db.insert(tableName, null, value);
                if (_id != -1) {
                    returnCount++;
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnCount;

    }


}
