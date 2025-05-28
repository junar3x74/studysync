package com.example.studysync.Managers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.studysync.Database.DBHelper;
import com.example.studysync.models.Flashcard;

import java.util.ArrayList;
import java.util.List;

public class FlashcardManager {
    private final DBHelper dbHelper;

    public FlashcardManager(Context context) {
        dbHelper = new DBHelper(context);
    }

    public void insertOrUpdateFlashcard(Flashcard card, int syncStatus) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", card.getId());
        values.put("deck_id", card.getDeck_id());
        values.put("question", card.getQuestion());
        values.put("answer", card.getAnswer());
        values.put("createdAt", card.getCreatedAt());
        values.put("isFavorite", card.getIsFavorite() ? 1 : 0);
        values.put("sync_status", syncStatus);
        db.insertWithOnConflict("flashcards", null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public List<Flashcard> getUnsyncedFlashcards() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Flashcard> result = new ArrayList<>();
        Cursor cursor = db.query("flashcards", null, "sync_status = 0", null, null, null, null);
        while (cursor.moveToNext()) {
            result.add(fromCursor(cursor));
        }
        cursor.close();
        return result;
    }

    public void updateFlashcardSyncStatus(String cardId, int syncStatus) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("sync_status", syncStatus);
        db.update("flashcards", values, "id = ?", new String[]{cardId});
    }

    public List<Flashcard> getAllFlashcards(String deckId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Flashcard> result = new ArrayList<>();
        Cursor cursor = db.query("flashcards", null, "deck_id = ?", new String[]{deckId}, null, null, null);
        while (cursor.moveToNext()) {
            result.add(fromCursor(cursor));
        }
        cursor.close();
        return result;
    }

    private Flashcard fromCursor(Cursor cursor) {
        return new Flashcard(
                cursor.getString(cursor.getColumnIndexOrThrow("id")),
                cursor.getString(cursor.getColumnIndexOrThrow("deck_id")),
                cursor.getString(cursor.getColumnIndexOrThrow("question")),
                cursor.getString(cursor.getColumnIndexOrThrow("answer")),
                cursor.getLong(cursor.getColumnIndexOrThrow("createdAt")),
                cursor.getInt(cursor.getColumnIndexOrThrow("isFavorite")) == 1,
                cursor.getInt(cursor.getColumnIndexOrThrow("sync_status"))
        );
    }
}