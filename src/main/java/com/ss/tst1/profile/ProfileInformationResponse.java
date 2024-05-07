package com.ss.tst1.profile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileInformationResponse {

    private String message;
    private Integer profileId;
    private String userName;
    private String firstName;
    private String lastName;
    private String avatarUrl;
    private String email;
    private String bgImage;
    private String bio;
    private Boolean canChange = false;

    public ProfileInformationResponse(String message) {
        this.message = message;
    }
}
