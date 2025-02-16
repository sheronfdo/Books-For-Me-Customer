package com.jamith.booksformecustomer.dto.requestDTO;

import lombok.Data;

@Data
public class PaymentDetailsDTO
{
    private String paymentId;
    private String paymentStatus;
    private String createdTime;
    private String intent;
}
