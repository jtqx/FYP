package com.example.fyp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CalorieDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "calorie_goal_database";
    private static final int DB_VERSION = 2;

    private static final String LOG_TABLE_NAME = "calorie_goal";
    private static final String LOG_TABLE_ID_COL = "id";
    private static final String LOG_TABLE_AUTHOR_COL = "author";
    private static final String LOG_TABLE_CALORIE_GOAL_COL = "calorie_goal";
    private static final String LOG_TABLE_CALORIE_COUNT_COL = "calorie_count";

    public CalorieDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createLogTableQuery = "CREATE TABLE "
                + LOG_TABLE_NAME + " ("
                + LOG_TABLE_ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + LOG_TABLE_AUTHOR_COL + " TEXT, "
                + LOG_TABLE_CALORIE_GOAL_COL + " INTEGER NOT NULL DEFAULT 0, "
                + LOG_TABLE_CALORIE_COUNT_COL + " INTEGER NOT NULL DEFAULT 0);";
        db.execSQL(createLogTableQuery);
    }
    public void addCalorieGoal(String author, int calorieGoal, int currentCount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LOG_TABLE_AUTHOR_COL, author);
        values.put(LOG_TABLE_CALORIE_GOAL_COL, calorieGoal);
        values.put(LOG_TABLE_CALORIE_COUNT_COL, currentCount);
        db.insert(LOG_TABLE_NAME, null, values);
        db.close();
    }

    public int getCalorieGoalByAuthor(String author) {
        int calorieGoal = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + LOG_TABLE_NAME + " WHERE " + LOG_TABLE_AUTHOR_COL + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{author});
        if (cursor.moveToFirst()) {
            calorieGoal = cursor.getInt(cursor.getColumnIndex(LOG_TABLE_CALORIE_GOAL_COL));
        }
        cursor.close();
        db.close();
        return calorieGoal;
    }

    public int getCalorieCountByAuthor(String author) {
        int calorieCount = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + LOG_TABLE_NAME + " WHERE " + LOG_TABLE_AUTHOR_COL + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{author});
        if (cursor.moveToFirst()) {
            calorieCount = cursor.getInt(cursor.getColumnIndex(LOG_TABLE_CALORIE_COUNT_COL));
        }
        cursor.close();
        db.close();
        return calorieCount;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + LOG_TABLE_NAME);
        onCreate(db);
    }
}

