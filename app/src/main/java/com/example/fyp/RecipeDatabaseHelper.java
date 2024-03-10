package com.example.fyp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class RecipeDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "Recipes";
    private static final int DB_VERSION = 1;
    private static final String USER_TABLE_NAME = "Recipe";
    private static final String USER_TABLE_ID_COL = "ID";
    private static final String USER_TABLE_AUTHOR_COL = "Author";
    private static final String USER_TABLE_NAME_COL = "Name";
    private static final String USER_TABLE_INGREDIENTS_COL = "Ingredients";
    private static final String USER_TABLE_STEPS_COL = "Steps";

    public RecipeDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUserTableQuery = "CREATE TABLE "
                + USER_TABLE_NAME + " ("
                + USER_TABLE_ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + USER_TABLE_AUTHOR_COL + " TEXT, "
                + USER_TABLE_NAME_COL + " TEXT NOT NULL, "
                + USER_TABLE_INGREDIENTS_COL + " TEXT NOT NULL, "
                + USER_TABLE_STEPS_COL + " TEXT NOT NULL "+ ");";
        db.execSQL(createUserTableQuery);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE_NAME);
        onCreate(db);
    }

    public void addRecipe(String author, String name, String ingredients, String steps) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_TABLE_AUTHOR_COL, author);
        values.put(USER_TABLE_NAME_COL, name);
        values.put(USER_TABLE_INGREDIENTS_COL, ingredients);
        values.put(USER_TABLE_STEPS_COL, steps);
        db.insert(USER_TABLE_NAME, null, values);
        db.close();
    }
    public List<Recipe> getRecipesByAuthor(String author) {
        List<Recipe> recipes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + USER_TABLE_NAME + " WHERE " + USER_TABLE_AUTHOR_COL + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{author});
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(USER_TABLE_ID_COL));
                String recipeAuthor = cursor.getString(cursor.getColumnIndex(USER_TABLE_AUTHOR_COL));
                String name = cursor.getString(cursor.getColumnIndex(USER_TABLE_NAME_COL));
                String ingredients = cursor.getString(cursor.getColumnIndex(USER_TABLE_INGREDIENTS_COL));
                String steps = cursor.getString(cursor.getColumnIndex(USER_TABLE_STEPS_COL));

                Recipe recipe = new Recipe(id, recipeAuthor, name, ingredients, steps);
                recipes.add(recipe);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return recipes;
    }
    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + USER_TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(USER_TABLE_ID_COL));
                String recipeAuthor = cursor.getString(cursor.getColumnIndex(USER_TABLE_AUTHOR_COL));
                String name = cursor.getString(cursor.getColumnIndex(USER_TABLE_NAME_COL));
                String ingredients = cursor.getString(cursor.getColumnIndex(USER_TABLE_INGREDIENTS_COL));
                String steps = cursor.getString(cursor.getColumnIndex(USER_TABLE_STEPS_COL));

                Recipe recipe = new Recipe(id, recipeAuthor, name, ingredients, steps);
                recipes.add(recipe);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return recipes;
    }

    public void updateRecipe(int recipeId, String newName, String newIngredients, String newInstructions) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        if (newName != null && !newName.isEmpty()) {
            values.put(USER_TABLE_NAME_COL, newName);
        }
        if (newIngredients != null) {
            values.put(USER_TABLE_INGREDIENTS_COL, newIngredients);
        }
        if (newInstructions != null) {
            values.put(USER_TABLE_STEPS_COL, newInstructions);
        }

        try {
            db.update(USER_TABLE_NAME, values, USER_TABLE_ID_COL + "=?", new String[]{String.valueOf(recipeId)});
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    public List<Recipe> searchRecipesByName(String query) {
        List<Recipe> recipes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                USER_TABLE_NAME,
                new String[]{USER_TABLE_ID_COL, USER_TABLE_AUTHOR_COL, USER_TABLE_NAME_COL, USER_TABLE_INGREDIENTS_COL, USER_TABLE_STEPS_COL},
                USER_TABLE_NAME_COL + " LIKE ?",
                new String[]{"%" + query + "%"},
                null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(USER_TABLE_ID_COL));
                String author = cursor.getString(cursor.getColumnIndex(USER_TABLE_AUTHOR_COL));
                String name = cursor.getString(cursor.getColumnIndex(USER_TABLE_NAME_COL));
                String ingredients = cursor.getString(cursor.getColumnIndex(USER_TABLE_INGREDIENTS_COL));
                String steps = cursor.getString(cursor.getColumnIndex(USER_TABLE_STEPS_COL));

                Recipe recipe = new Recipe(id, author, name, ingredients, steps);
                recipes.add(recipe);
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return recipes;
    }
    public List<Recipe> searchRecipesByNameOrAuthor(String query) {
        List<Recipe> recipes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                USER_TABLE_NAME,
                new String[]{USER_TABLE_ID_COL, USER_TABLE_AUTHOR_COL, USER_TABLE_NAME_COL, USER_TABLE_INGREDIENTS_COL, USER_TABLE_STEPS_COL},
                USER_TABLE_NAME_COL + " LIKE ? OR " + USER_TABLE_AUTHOR_COL + " LIKE ?",
                new String[]{"%" + query + "%", "%" + query + "%"},
                null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(USER_TABLE_ID_COL));
                String author = cursor.getString(cursor.getColumnIndex(USER_TABLE_AUTHOR_COL));
                String name = cursor.getString(cursor.getColumnIndex(USER_TABLE_NAME_COL));
                String ingredients = cursor.getString(cursor.getColumnIndex(USER_TABLE_INGREDIENTS_COL));
                String steps = cursor.getString(cursor.getColumnIndex(USER_TABLE_STEPS_COL));

                Recipe recipe = new Recipe(id, author, name, ingredients, steps);
                recipes.add(recipe);
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return recipes;
    }
    public void deleteRecipe(Recipe recipe) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(USER_TABLE_NAME, USER_TABLE_ID_COL + "=?", new String[]{String.valueOf(recipe.getId())});
        db.close();
    }

}

