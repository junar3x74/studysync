package com.example.studysync.models;


public class Flashcard {
    private String id;
    private String deckId;
    private String frontText;
    private String backText;

    public Flashcard(String id, String deckId, String frontText, String backText) {
        this.id = id;
        this.deckId = deckId;
        this.frontText = frontText;
        this.backText = backText;
    }

    // Getters & setters
    public String getId() { return id; }
    public String getDeckId() { return deckId; }
    public String getFrontText() { return frontText; }
    public String getBackText() { return backText; }
    public void setFrontText(String frontText) { this.frontText = frontText; }
    public void setBackText(String backText) { this.backText = backText; }
}
