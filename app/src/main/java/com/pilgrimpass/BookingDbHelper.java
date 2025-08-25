package com.pilgrimpass;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;
import android.database.Cursor;

public class BookingDbHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "booking.db";
    public static final int DB_VERSION = 1;

    public static final String TABLE_BOOKINGS = "bookings";
    public static final String COL_ID = "_id";
    public static final String COL_DATE = "date_text";   // e.g. 2025-08-25
    public static final String COL_TIME = "time_text";   // e.g. 14:30
    public static final String COL_CREATED_AT = "created_at";

    private static final String SQL_CREATE =
            "CREATE TABLE " + TABLE_BOOKINGS + " (" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_DATE + " TEXT NOT NULL, " +
                    COL_TIME + " TEXT NOT NULL, " +
                    COL_CREATED_AT + " INTEGER NOT NULL" +
                    ");";

    private static final String SQL_DROP =
            "DROP TABLE IF EXISTS " + TABLE_BOOKINGS;

    public BookingDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP);
        onCreate(db);
    }

    /** Insert a single booking. Returns new rowId or -1 on failure. */
    public long insertBooking(String dateText, String timeText) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_DATE, dateText);
        cv.put(COL_TIME, timeText);
        cv.put(COL_CREATED_AT, System.currentTimeMillis());
        return db.insert(TABLE_BOOKINGS, null, cv);
    }

    /** Get all bookings sorted by created_at DESC. */
    public Cursor getAllBookings() {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(
                TABLE_BOOKINGS,
                new String[]{COL_ID, COL_DATE, COL_TIME, COL_CREATED_AT},
                null, null, null, null,
                COL_CREATED_AT + " DESC"
        );
    }

    /** Get total count. */
    public int getTotalCount() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_BOOKINGS, null);
        int count = 0;
        if (c.moveToFirst()) count = c.getInt(0);
        c.close();
        return count;
    }

    /** Count by date (GROUP BY). */
    public Cursor getCountsByDate() {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT " + COL_DATE + ", COUNT(*) AS cnt FROM " + TABLE_BOOKINGS +
                " GROUP BY " + COL_DATE + " ORDER BY " + COL_DATE + " DESC";
        return db.rawQuery(sql, null);
    }
}
