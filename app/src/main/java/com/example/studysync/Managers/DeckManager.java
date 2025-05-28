package com.example.studysync.Managers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.studysync.Database.DBHelper;
import com.example.studysync.models.Deck;

import java.util.ArrayList;
import java.util.List;

public class DeckManager {
    private final DBHelper dbHelper;

    public DeckManager(Context context) {
        dbHelper = new DBHelper(context);
    }

    public void insertOrUpdateDeck(Deck deck, int syncStatus) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", deck.getId());
        values.put("user_id", deck.getUser_id());
        values.put("title", deck.getTitle());
        values.put("createdAt", deck.getCreatedAt());
        values.put("isFavorite", deck.getIsFavorite() ? 1 : 0);
        values.put("cardCount", deck.getCardCount());
        values.put("sync_status", syncStatus);
        db.insertWithOnConflict("decks", null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public List<Deck> getUnsyncedDecks() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Deck> result = new ArrayList<>();
        Cursor cursor = db.query("decks", null, "sync_status = 0", null, null, null, null);
        while (cursor.moveToNext()) {
            result.add(fromCursor(cursor));
        }
        cursor.close();
        return result;
    }

    public void updateDeckSyncStatus(String deckId, int syncStatus) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("sync_status", syncStatus);
        db.update("decks", values, "id = ?", new String[]{deckId});
    }

    public List<Deck> getAllDecks(String userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Deck> result = new ArrayList<>();
        Cursor cursor = db.query("decks", null, "user_id = ?", new String[]{userId}, null, null, null);
        while (cursor.moveToNext()) {
            result.add(fromCursor(cursor));
        }
        cursor.close();
        return result;
    }

    private Deck fromCursor(Cursor cursor) {
        return new Deck(
                cursor.getString(cursor.getColumnIndexOrThrow("id")),
                cursor.getString(cursor.getColumnIndexOrThrow("user_id")),
                cursor.getString(cursor.getColumnIndexOrThrow("title")),
                cursor.getLong(cursor.getColumnIndexOrThrow("createdAt")),
                cursor.getInt(cursor.getColumnIndexOrThrow("isFavorite")) == 1,
                cursor.getInt(cursor.getColumnIndexOrThrow("cardCount")),
                cursor.getInt(cursor.getColumnIndexOrThrow("sync_status"))
        );
    }
}