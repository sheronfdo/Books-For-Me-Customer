package com.jamith.booksformecustomer.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Order {
    private String orderId;
    private Date orderDate;
    private double totalPrice;
    private String paymentStatus;
    private String status;
}
