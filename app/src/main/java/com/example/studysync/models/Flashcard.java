package com.example.studysync.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Flashcard implements Parcelable {
    private String front;
    private String back;

    public Flashcard(String front, String back) {
        this.front = front;
        this.back = back;
    }

    protected Flashcard(Parcel in) {
        front = in.readString();
        back = in.readString();
    }

    public static final Creator<Flashcard> CREATOR = new Creator<Flashcard>() {
        @Override
        public Flashcard createFromParcel(Parcel in) {
            return new Flashcard(in);
        }
        @Override
        public Flashcard[] newArray(int size) {
            return new Flashcard[size];
        }
    };

    public String getFront() { return front; }
    public String getBack() { return back; }
    public void setFront(String front) { this.front = front; }
    public void setBack(String back) { this.back = back; }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(front);
        dest.writeString(back);
    }
}