package com.jamith.booksformecustomer.model;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CartItem implements Serializable {
    private String cartItemId;
    private String bookId;
    private String bookStockId;
    private String sellerId;
    private Date createdAt;
    private String imageUrl;
    private double price;
    private int quantity;
    private String title;
}
