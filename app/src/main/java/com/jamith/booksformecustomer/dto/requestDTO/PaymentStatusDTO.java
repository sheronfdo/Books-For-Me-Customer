package com.jamith.booksformecustomer.dto.requestDTO;


import com.jamith.booksformecustomer.util.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PaymentStatusDTO {

    private String orderId;
    private PaymentStatus paymentStatus;

}
