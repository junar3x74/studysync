package com.example.studysync.models;

import java.util.Objects;

public class Deck {
    private String id, title;
    private long createdAt;
    private boolean isFavorite;
    private int cardCount;


    public Deck(){

    }

    public Deck(String id, String title, long createdAt,
                boolean isFavorite, int cardCount) {
        this.id         = id;
        this.title      = title;
        this.createdAt  = createdAt;
        this.isFavorite = isFavorite;
        this.cardCount  = cardCount;
    }

    // getters & setters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public long getCreatedAt() { return createdAt; }
    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean fav) { isFavorite = fav; }
    public int getCardCount() { return cardCount; }
    public void setCardCount(int c) { cardCount = c; }

    public void setTitle(String title) {
        this.title = title;
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