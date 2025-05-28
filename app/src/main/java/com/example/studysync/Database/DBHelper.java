package com.example.studysync.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "studysync.db";
    private static final int DB_VERSION = 2; // Bumped version for user_id

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE users (" +
                        "id TEXT PRIMARY KEY, " +
                        "name TEXT, " +
                        "email TEXT, " +
                        "createdAt INTEGER, " +
                        "sync_status INTEGER DEFAULT 0)"
        );
        db.execSQL(
                "CREATE TABLE decks (" +
                        "id TEXT PRIMARY KEY, " +
                        "user_id TEXT, " +
                        "title TEXT, " +
                        "createdAt INTEGER, " +
                        "isFavorite INTEGER, " +
                        "cardCount INTEGER, " +
                        "sync_status INTEGER DEFAULT 0)"
        );
        db.execSQL(
                "CREATE TABLE flashcards (" +
                        "id TEXT PRIMARY KEY, " +
                        "deck_id TEXT, " +
                        "user_id TEXT, " + // Consistently add user_id
                        "question TEXT, " +
                        "answer TEXT, " +
                        "createdAt INTEGER, " +
                        "isFavorite INTEGER, " +
                        "sync_status INTEGER DEFAULT 0)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE flashcards ADD COLUMN user_id TEXT");
        }
        // Add future migrations here
    }
}