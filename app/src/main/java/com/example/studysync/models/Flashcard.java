package com.example.studysync.models;

public class Flashcard {
    private String id;
    private String deckId;
    private String frontText;
    private String backText;

    // Default constructor required for Firebase
    public Flashcard() {
    }

    public Flashcard(String id, String deckId, String frontText, String backText) {
        this.id = id;
        this.deckId = deckId;
        this.frontText = frontText;
        this.backText = backText;
    }

    // Getters and setters (all required for Firebase)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeckId() {
        return deckId;
    }

    public void setDeckId(String deckId) {
        this.deckId = deckId;
    }

    public String getFrontText() {
        return frontText;
    }

    public void setFrontText(String frontText) {
        this.frontText = frontText;
    }

    public String getBackText() {
        return backText;
    }

    public void setBackText(String backText) {
        this.backText = backText;
    }
}