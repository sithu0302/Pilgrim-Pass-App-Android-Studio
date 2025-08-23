package com.pilgrimpass;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;

public class BookingDbHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "pilgrim_pass.db";
    public static final int DB_VERSION = 1;

    public static final String TBL_BOOKINGS = "bookings";
    public static final String COL_ID = "id";
    public static final String COL_DATE = "date";
    public static final String COL_TIME = "time";

    public BookingDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS " + TBL_BOOKINGS + " (" +
                        COL_ID   + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COL_DATE + " TEXT NOT NULL, " +
                        COL_TIME + " TEXT NOT NULL" +
                        ");"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS " + TBL_BOOKINGS);
        onCreate(db);
    }

    // ---- CRUD helpers ----
    public long insertBooking(String date, String time) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_DATE, date);
        cv.put(COL_TIME, time);
        return db.insert(TBL_BOOKINGS, null, cv);
    }

    public Cursor getAllBookings() {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(
                TBL_BOOKINGS,
                new String[]{COL_ID, COL_DATE, COL_TIME},
                null, null, null, null,
                COL_ID + " DESC"
        );
    }

    public int getBookingsCount() {
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + TBL_BOOKINGS, null)) {
            if (c.moveToFirst()) return c.getInt(0);
            return 0;
        }
    }
}
