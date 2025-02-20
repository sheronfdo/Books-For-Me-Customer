package com.jamith.booksformecustomer.dto.requestDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerUpdateDTO {
    private String id;
    private String firstName;
    private String lastName;
    private String displayName;
    private String phoneNumber;
    private String imageUrl;
}
