package com.example.library;

public class Book {
    public String title;
    public String url;

    public Book() {
        // Default constructor required for calls to DataSnapshot.getValue(Book.class)
    }

    public Book(String title, String url) {
        this.title = title;
        this.url = url;
    }
}
