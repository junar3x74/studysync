package com.example.studysync.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "studysync.db";
    private static final int DB_VERSION = 1;

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
                        "question TEXT, " +
                        "answer TEXT, " +
                        "createdAt INTEGER, " +
                        "isFavorite INTEGER, " +
                        "sync_status INTEGER DEFAULT 0)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Simple upgrade: drop and recreate
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS decks");
        db.execSQL("DROP TABLE IF EXISTS flashcards");
        onCreate(db);
    }
}