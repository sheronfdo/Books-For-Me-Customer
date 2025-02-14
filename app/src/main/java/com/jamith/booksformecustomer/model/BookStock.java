package com.jamith.booksformecustomer.model;

import java.util.Date;

import lombok.Data;

@Data
public class BookStock {
    private String bookStockId;
    private String bookId;
    private String sellerId;
    private int stock;
    private double price;
    private String condition;
    private Date createdAt;
    private Date updatedAt;
}
