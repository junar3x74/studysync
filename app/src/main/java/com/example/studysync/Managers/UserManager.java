package com.example.studysync.Managers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.studysync.Database.DBHelper;
import com.example.studysync.models.User;

import java.util.ArrayList;
import java.util.List;

public class UserManager {
    private final DBHelper dbHelper;

    public UserManager(Context context) {
        dbHelper = new DBHelper(context);
    }

    public void insertOrUpdateUser(User user, int syncStatus) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", user.getId());
        values.put("name", user.getName());
        values.put("email", user.getEmail());
        values.put("createdAt", user.getCreatedAt());
        values.put("sync_status", syncStatus);
        db.insertWithOnConflict("users", null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public List<User> getUnsyncedUsers() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<User> result = new ArrayList<>();
        Cursor cursor = db.query("users", null, "sync_status = 0", null, null, null, null);
        while (cursor.moveToNext()) {
            result.add(fromCursor(cursor));
        }
        cursor.close();
        return result;
    }

    public void updateUserSyncStatus(String userId, int syncStatus) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("sync_status", syncStatus);
        db.update("users", values, "id = ?", new String[]{userId});
    }

    public User getUser(String userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("users", null, "id = ?", new String[]{userId}, null, null, null);
        User user = null;
        if (cursor.moveToFirst()) {
            user = fromCursor(cursor);
        }
        cursor.close();
        return user;
    }

    // Find user by email (for offline login)
    public User getUserByEmail(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("users", null, "email = ?", new String[]{email}, null, null, null);
        User user = null;
        if (cursor.moveToFirst()) {
            user = fromCursor(cursor);
        }
        cursor.close();
        return user;
    }

    // Optional: delete user by ID
    public void deleteUser(String userId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("users", "id = ?", new String[]{userId});
    }

    // Optional: update an existing user
    public void updateUser(User user, int syncStatus) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", user.getName());
        values.put("email", user.getEmail());
        values.put("createdAt", user.getCreatedAt());
        values.put("sync_status", syncStatus);
        db.update("users", values, "id = ?", new String[]{user.getId()});
    }

    private User fromCursor(Cursor cursor) {
        return new User(
                cursor.getString(cursor.getColumnIndexOrThrow("id")),
                cursor.getString(cursor.getColumnIndexOrThrow("name")),
                cursor.getString(cursor.getColumnIndexOrThrow("email")),
                cursor.getLong(cursor.getColumnIndexOrThrow("createdAt")),
                cursor.getInt(cursor.getColumnIndexOrThrow("sync_status"))
        );
    }
}