package com.jamith.booksformecustomer.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BookItem implements Serializable {
    private String title;
    private String author;
    private String imageUrl;
    private String bookId;
    private String sellerId;
    private String bookStockId;
    private int stock;
    private double price;
    private String condition;
    private String isbn;
    private String publisher;
    private String category;
    private String description;
    private String coverImage;
    private int publicationYear;
    private String language;
    private List<String> tags;
    private boolean featured;
    private Date createdAt;
    private Date updatedAt;
}