package com.example.studysync.models;

public class Flashcard {
    private String id;
    private String deck_id;
    private String user_id; // Added user_id for ownership/tracking
    private String question;
    private String answer;
    private long createdAt;
    private boolean isFavorite;
    private int sync_status; // 0 = not synced, 1 = synced

    public Flashcard() {}

    public Flashcard(String id, String deck_id, String user_id, String question, String answer, long createdAt, boolean isFavorite, int sync_status) {
        this.id = id;
        this.deck_id = deck_id;
        this.user_id = user_id;
        this.question = question;
        this.answer = answer;
        this.createdAt = createdAt;
        this.isFavorite = isFavorite;
        this.sync_status = sync_status;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDeck_id() { return deck_id; }
    public void setDeck_id(String deck_id) { this.deck_id = deck_id; }

    public String getUser_id() { return user_id; }
    public void setUser_id(String user_id) { this.user_id = user_id; }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public boolean getIsFavorite() { return isFavorite; }
    public void setIsFavorite(boolean favorite) { isFavorite = favorite; }

    public int getSync_status() { return sync_status; }
    public void setSync_status(int sync_status) { this.sync_status = sync_status; }
}