package com.jamith.booksformecustomer.util;


import java.io.Serializable;

public enum PaymentStatus implements Serializable {
    PAYMENT_STATUS_CANCELLED, PAYMENT_STATUS_COMPLETED, PAYMENT_STATUS_FAILED, PAYMENT_PENDING;
}
