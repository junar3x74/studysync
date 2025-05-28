package com.example.studysync.models;

public class Deck {
    private String id;
    private String user_id;
    private String title;
    private long createdAt;
    private boolean isFavorite;
    private int cardCount;
    private int sync_status; // 0 = not synced, 1 = synced

    public Deck() {}

    public Deck(String id, String user_id, String title, long createdAt, boolean isFavorite, int cardCount, int sync_status) {
        this.id = id;
        this.user_id = user_id;
        this.title = title;
        this.createdAt = createdAt;
        this.isFavorite = isFavorite;
        this.cardCount = cardCount;
        this.sync_status = sync_status;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUser_id() { return user_id; }
    public void setUser_id(String user_id) { this.user_id = user_id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public boolean getIsFavorite() { return isFavorite; }
    public void setIsFavorite(boolean favorite) { isFavorite = favorite; }

    public int getCardCount() { return cardCount; }
    public void setCardCount(int cardCount) { this.cardCount = cardCount; }

    public int getSync_status() { return sync_status; }
    public void setSync_status(int sync_status) { this.sync_status = sync_status; }
}