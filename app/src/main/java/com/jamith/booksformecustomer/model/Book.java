package com.jamith.booksformecustomer.model;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class Book {
    private String title;
    private String author;
    private String isbn;
    private String publisher;
    private String category;
    private String description;
    private String coverImage;
    private int publicationYear;
    private String language;
    private List<String> tags;
    private String createdUser;
    private boolean featured;
    private Date createdAt;
    private Date updatedAt;
}
