package com.example.studysync.models;

public class User {
    private String id;
    private String name;
    private String email;
    private long createdAt;
    private int sync_status; // 0 = not synced, 1 = synced

    public User() {}

    public User(String id, String name, String email, long createdAt, int sync_status) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.createdAt = createdAt;
        this.sync_status = sync_status;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public int getSync_status() { return sync_status; }
    public void setSync_status(int sync_status) { this.sync_status = sync_status; }
}