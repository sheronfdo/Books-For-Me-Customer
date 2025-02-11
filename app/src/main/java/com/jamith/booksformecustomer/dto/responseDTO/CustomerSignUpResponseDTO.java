package com.jamith.booksformecustomer.dto.responseDTO;

import lombok.Data;

import java.util.Date;

@Data
public class CustomerSignUpResponseDTO {
    private String id;
    private Date createdTime;
}
