package com.jamith.booksformecustomer.dto.responseDTO;

import lombok.Data;

@Data
public class ErrorResponse {
    private String timestamp;
    private int status;
    private String error;
    private String message;

}