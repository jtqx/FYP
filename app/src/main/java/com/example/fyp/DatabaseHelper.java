package com.example.fyp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "FYP";
    private static final int DB_VERSION = 4;
    private static final String USER_TABLE_NAME = "User";
    private static final String USER_TABLE_EMAIL_COL = "Email";
    private static final String USER_TABLE_FIRST_NAME_COL = "FirstName";
    private static final String USER_TABLE_LAST_NAME_COL = "LastName";
    private static final String USER_TABLE_PW_COL = "Password";
    private static final String MEAL_RECORD_TABLE_NAME = "MealRecord";
    private static final String MEAL_RECORD_ID_COL = "Id";
    private static final String MEAL_RECORD_DATE_COL = "Date";
    private static final String MEAL_RECORD_MEAL_TYPE_COL = "MealType";
    private static final String MEAL_RECORD_MEAL_NAME_COL = "MealName";
    private static final String MEAL_RECORD_CALORIES_COL = "Calories";
    private static final String MEAL_RECORD_CARBS_COL = "Carbs";
    private static final String MEAL_RECORD_FATS_COL = "Fats";
    private static final String MEAL_RECORD_PROTEIN_COL = "Protein";
    private static final String MEAL_RECORD_EMAIL_COL = "Email";
    private static final String BODY_PROFILE_TABLE_NAME = "BodyProfile";
    private static final String BODY_PROFILE_ID_COL = "Id";
    private static final String BODY_PROFILE_HEIGHT_COL = "Height";
    private static final String BODY_PROFILE_WEIGHT_COL = "Weight";
    private static final String BODY_PROFILE_BMI_COL = "Bmi";
    private static final String BODY_PROFILE_EMAIL_COL = "Email";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // This method is automatically executed only when the database is created for the first time
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUserTableQuery = "CREATE TABLE "
                + USER_TABLE_NAME + " ("
                + USER_TABLE_EMAIL_COL + " TEXT PRIMARY KEY, "
                + USER_TABLE_FIRST_NAME_COL + " TEXT, "
                + USER_TABLE_LAST_NAME_COL + " TEXT, "
                + USER_TABLE_PW_COL + " TEXT NOT NULL" + ");";

        String createMealRecordTableQuery = "CREATE TABLE "
                + MEAL_RECORD_TABLE_NAME + " ("
                + MEAL_RECORD_ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + MEAL_RECORD_DATE_COL + " TEXT, "
                + MEAL_RECORD_MEAL_TYPE_COL + " TEXT, "
                + MEAL_RECORD_MEAL_NAME_COL + " TEXT, "
                + MEAL_RECORD_CALORIES_COL + " INTEGER, "
                + MEAL_RECORD_CARBS_COL + " INTEGER, "
                + MEAL_RECORD_FATS_COL + " INTEGER, "
                + MEAL_RECORD_PROTEIN_COL + " INTEGER, "
                + MEAL_RECORD_EMAIL_COL + " TEXT, "
                + "FOREIGN KEY (" + MEAL_RECORD_EMAIL_COL + ") "
                + "REFERENCES " + USER_TABLE_NAME + "(" + USER_TABLE_EMAIL_COL + "));";

        String createBodyProfileTableQuery = "CREATE TABLE "
                + BODY_PROFILE_TABLE_NAME + " ("
                + BODY_PROFILE_ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BODY_PROFILE_HEIGHT_COL + " INTEGER, "
                + BODY_PROFILE_WEIGHT_COL + " INTEGER, "
                + BODY_PROFILE_BMI_COL + " INTEGER, "
                + BODY_PROFILE_EMAIL_COL + " TEXT, "
                + "FOREIGN KEY (" + BODY_PROFILE_EMAIL_COL + ") "
                + "REFERENCES " + USER_TABLE_NAME + "(" + USER_TABLE_EMAIL_COL + "));";

        db.execSQL(createUserTableQuery);
        db.execSQL(createMealRecordTableQuery);
        db.execSQL(createBodyProfileTableQuery);
    }

    /* This method is automatically executed only when DB_VERSION is incremented. It should be used
    to drop tables, add tables, or do anything else needed to upgrade the database to a new
    schema version. */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MEAL_RECORD_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + BODY_PROFILE_TABLE_NAME);
        onCreate(db);
    }

    public Cursor getUser(String email) {
        String query = "SELECT * FROM " + USER_TABLE_NAME
                + " WHERE " + USER_TABLE_EMAIL_COL + " = '" + email + "';";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() == 0)
            cursor.close();
        return cursor;
    }

    public boolean logInUser(String email, String password) {
        Cursor checkIfUserExists = getUser(email);
        if (checkIfUserExists.isClosed())
            return false;
        else {
            checkIfUserExists.moveToFirst();
            if (!checkIfUserExists.getString(3).equals(password))
                return false;
            return true;
        }
    }

    public boolean createUser(String email, String password) {
        Cursor checkIfUserExists = getUser(email);
        if (!checkIfUserExists.isClosed())
            return false;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_TABLE_EMAIL_COL, email);
        values.put(USER_TABLE_PW_COL, password);
        db.insert(USER_TABLE_NAME, null, values);
        db.close();
        return true;
    }

    public boolean updateUser(String email, String newFirstName, String newLastName) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            String query = "UPDATE " + USER_TABLE_NAME
                    + " SET "
                    + USER_TABLE_FIRST_NAME_COL + " = " + "'" + newFirstName + "', "
                    + USER_TABLE_LAST_NAME_COL + " = " + "'" + newLastName + "'"
                    + " WHERE " + USER_TABLE_EMAIL_COL + " = " + "'" + email + "'" + ";";
            db.execSQL(query);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean addMealRecord(String date, String mealType, String mealName,
                                 int calories, int carbs, int fats, int protein, String email) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(MEAL_RECORD_DATE_COL, date);
            values.put(MEAL_RECORD_MEAL_TYPE_COL, mealType);
            values.put(MEAL_RECORD_MEAL_NAME_COL, mealName);
            values.put(MEAL_RECORD_CALORIES_COL, calories);
            values.put(MEAL_RECORD_CARBS_COL, carbs);
            values.put(MEAL_RECORD_FATS_COL, fats);
            values.put(MEAL_RECORD_PROTEIN_COL, protein);
            values.put(MEAL_RECORD_EMAIL_COL, email);
            db.insert(MEAL_RECORD_TABLE_NAME, null, values);
            db.close();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public Cursor getMealRecordByMealType(String email, String date, String mealType) {
        String query = "SELECT " + MEAL_RECORD_MEAL_NAME_COL + ", " + MEAL_RECORD_CALORIES_COL
                + " FROM " + MEAL_RECORD_TABLE_NAME
                + " WHERE " + MEAL_RECORD_EMAIL_COL + " = " + "\"" + email + "\""
                + " AND " + MEAL_RECORD_DATE_COL + " = " + "\"" + date + "\""
                + " AND " + MEAL_RECORD_MEAL_TYPE_COL + " = " + "\"" + mealType + "\"" + ";";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        return cursor;
    }

    public Cursor getMealRecordByDateTypeName(String email, String date, String mealType,
                                          String mealName) {
        String query = "SELECT " + MEAL_RECORD_MEAL_NAME_COL + ", " + MEAL_RECORD_CALORIES_COL + ", "
                + MEAL_RECORD_CARBS_COL + ", " + MEAL_RECORD_FATS_COL + ", "
                + MEAL_RECORD_PROTEIN_COL
                + " FROM " + MEAL_RECORD_TABLE_NAME
                + " WHERE " + MEAL_RECORD_EMAIL_COL + " = " + "\"" + email + "\""
                + " AND " + MEAL_RECORD_DATE_COL + " = " + "\"" + date + "\""
                + " AND " + MEAL_RECORD_MEAL_TYPE_COL + " = " + "\"" + mealType + "\""
                + " AND " + MEAL_RECORD_MEAL_NAME_COL + " = " + "\"" + mealName + "\"" + ";";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        return cursor;
    }

    public boolean deleteMealRecord(String email, String date, String mealType,
                                    String mealName) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            String query = "DELETE FROM " + MEAL_RECORD_TABLE_NAME
                    + " WHERE " + MEAL_RECORD_EMAIL_COL + " = " + "\"" + email + "\""
                    + " AND " + MEAL_RECORD_DATE_COL + " = " + "\"" + date + "\""
                    + " AND " + MEAL_RECORD_MEAL_TYPE_COL + " = " + "\"" + mealType + "\""
                    + " AND " + MEAL_RECORD_MEAL_NAME_COL + " = " + "\"" + mealName + "\"" + ";";
            db.execSQL(query);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean createBodyProfile(int height, int weight, int bmi, String email) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor checkIfBodyProfileExists = getBodyProfile(email);
            if (checkIfBodyProfileExists.isClosed()) {
                ContentValues values = new ContentValues();
                values.put(BODY_PROFILE_HEIGHT_COL, height);
                values.put(BODY_PROFILE_WEIGHT_COL, weight);
                values.put(BODY_PROFILE_BMI_COL, bmi);
                values.put(BODY_PROFILE_EMAIL_COL, email);
                db.insert(BODY_PROFILE_TABLE_NAME, null, values);
                db.close();
                return true;
            } else {
                String query = "UPDATE " + BODY_PROFILE_TABLE_NAME
                        + " SET " + BODY_PROFILE_HEIGHT_COL + " = " + height + ", "
                        + BODY_PROFILE_WEIGHT_COL + " = " + weight + ", "
                        + BODY_PROFILE_BMI_COL + " = " + bmi
                        + " WHERE " + BODY_PROFILE_EMAIL_COL + " = " + "'" + email + "'" + ";";
                db.execSQL(query);
                return true;
            }
        } catch (SQLException e) {
            return false;
        }
    }

    public Cursor getBodyProfile(String email) {
        String query = "SELECT * FROM " + BODY_PROFILE_TABLE_NAME
                + " WHERE " + BODY_PROFILE_EMAIL_COL + " = '" + email + "';";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() == 0)
            cursor.close();
        return cursor;
    }
    public int getTotalCaloriesForDay(String email, String date) {
        int totalCalories = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(" + MEAL_RECORD_CALORIES_COL + ") AS total_calories"
                + " FROM " + MEAL_RECORD_TABLE_NAME
                + " WHERE " + MEAL_RECORD_EMAIL_COL + " = ?"
                + " AND " + MEAL_RECORD_DATE_COL + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email, date});
        if (cursor.moveToFirst()) {
            totalCalories = cursor.getInt(cursor.getColumnIndex("total_calories"));
        }
        cursor.close();
        db.close();
        return totalCalories;
    }
}
