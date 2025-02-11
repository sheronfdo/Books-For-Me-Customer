package com.jamith.booksformecustomer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BookItem {
    private String title;
    private String author;
    private double price;
    private String imageUrl;
}