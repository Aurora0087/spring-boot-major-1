package com.ss.tst1.admin;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class UserDetails {

    private String id;
    private String userName;
    private String email;
    private String firstName;
    private String lastName;
    private Boolean locked;
    private Boolean enabled;
    private String role;
}
