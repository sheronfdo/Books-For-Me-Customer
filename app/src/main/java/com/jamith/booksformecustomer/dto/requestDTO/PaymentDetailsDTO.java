package com.jamith.booksformecustomer.dto.requestDTO;

import java.io.Serializable;

import lombok.Data;

@Data
public class PaymentDetailsDTO  implements Serializable
{
    private String paymentId;
    private String paymentStatus;
    private String createdTime;
    private String intent;
}
