package com.jamith.booksformecustomer.model;

import java.util.Date;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Profile {
    private String email;
    private String displayName;
    private String firstName;
    private String imageUri;
    private String lastName;
    private String phoneNumber;
    private String role;
    private String uid;
}
