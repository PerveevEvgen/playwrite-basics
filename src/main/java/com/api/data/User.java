package com.api.data;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    private String id;
    private String name;
    private String email;
    private int age;
    private String phoneNumber;
    private String address;
    private String role;
    private String referralCode;
    private String createdAt;
    private String createdBy;
    private Object decodedToken;
}
