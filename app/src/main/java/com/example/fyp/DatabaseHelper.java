package com.example.fyp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "FYP";
    private static final int DB_VERSION = 2;
    private static final String USER_TABLE_NAME = "User";
    private static final String USER_TABLE_EMAIL_COL = "Email";
    private static final String USER_TABLE_FIRST_NAME_COL = "FirstName";
    private static final String USER_TABLE_LAST_NAME_COL = "LastName";
    private static final String USER_TABLE_PW_COL = "Password";

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
        db.execSQL(createUserTableQuery);
    }

    /* This method is automatically executed only when DB_VERSION is incremented. It should be used
    to drop tables, add tables, or do anything else needed to upgrade the database to a new
    schema version. */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE_NAME);
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
}
