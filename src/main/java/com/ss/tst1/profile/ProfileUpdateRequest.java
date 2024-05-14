package com.ss.tst1.profile;

import lombok.Data;

@Data
public class ProfileUpdateRequest {
    private String newFirstName;
    private String newLastName;
    private String newBio;
}
