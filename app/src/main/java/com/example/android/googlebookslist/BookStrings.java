package com.example.android.googlebookslist;

/**
 * Created by kyle on 12/27/16.
 */

public class BookStrings {

    private String mTitle;
    private String mAuthor;
    private String mRating;
    private String mDate;

    public BookStrings(String title, String author, String rating, String date) {

        mTitle = "";
        mRating = "";
        mDate = "";
        mAuthor = "";

        mAuthor = author;
        mTitle = title;
        mRating = rating;
        mDate = date;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public void setRating(String rating) {
        this.mRating = rating;
    }

    public void setDate(String date) {
        this.mDate = date;
    }

    public void setAuthor(String author) { this.mAuthor = author; }

    public String getTitle() {
        return mTitle;
    }

    public String getRating() {

        return mRating;
    }

    public String getDate() {

        return mDate;
    }

    public String getAuthor() {
        return mAuthor;
    }
}


