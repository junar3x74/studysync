package com.example.studysync.models;

import java.util.List;

public class Deck {
    private String id, title;
    private long createdAt;
    private boolean isFavorite;
    private int cardCount;
    private List<Flashcard> flashcards;

    public Deck(String id, String title, long createdAt,
                boolean isFavorite, List<Flashcard> flashcards) {
        this.id = id;
        this.title = title;
        this.createdAt = createdAt;
        this.isFavorite = isFavorite;
        this.flashcards = flashcards;
        this.cardCount = flashcards != null ? flashcards.size() : 0;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public long getCreatedAt() { return createdAt; }
    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean fav) { isFavorite = fav; }
    public int getCardCount() { return cardCount; }
    public void setCardCount(int c) { cardCount = c; }
    public List<Flashcard> getFlashcards() { return flashcards; }
    public void setFlashcards(List<Flashcard> flashcards) {
        this.flashcards = flashcards;
        this.cardCount = flashcards != null ? flashcards.size() : 0;
    }
    public void setTitle(String title) { this.title = title; }
}