package com.example.studysync.models;

import java.util.Objects;

public class Deck {
    private String id, user_id, title;
    private long createdAt;
    private boolean isFavorite;
    private int cardCount;

    // Default constructor required for Firebase
    public Deck() {
    }

    public Deck(String id, String user_id, String title, long createdAt,
                boolean isFavorite, int cardCount) {
        this.id         = id;
        this.user_id    = user_id;
        this.title      = title;
        this.createdAt  = createdAt;
        this.isFavorite = isFavorite;
        this.cardCount  = cardCount;
    }

    // Getters and setters (all required for Firebase)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    // Note: Firebase expects "isFavorite" getter for boolean fields
    public boolean getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public int getCardCount() {
        return cardCount;
    }

    public void setCardCount(int cardCount) {
        this.cardCount = cardCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Deck)) return false;
        Deck deck = (Deck) o;
        return Objects.equals(id, deck.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}