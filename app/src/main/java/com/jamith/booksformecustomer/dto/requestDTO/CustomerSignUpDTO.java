package com.jamith.booksformecustomer.dto.requestDTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerSignUpDTO {
    String uid;
    String displayName;
    String imageUri;
    String firstName;
    String lastName;
    String phoneNumber;
    String email;
}
