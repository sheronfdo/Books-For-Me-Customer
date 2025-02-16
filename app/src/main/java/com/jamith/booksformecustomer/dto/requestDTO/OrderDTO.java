package com.jamith.booksformecustomer.dto.requestDTO;

import com.jamith.booksformecustomer.model.CartItem;

import java.util.List;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class OrderDTO {

    private String recieverName;
    private String recieverAddress;
    private String recieverPhoneNumber;
    private String recieverEmail;
    private String customerId;
    private List<CartItem> cartItems;
    private int itemCount;
    private double totalPrice;

}
