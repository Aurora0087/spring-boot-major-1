package com.ss.tst1.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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
