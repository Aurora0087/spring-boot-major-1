package com.ss.tst1.register;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class RegistrationRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String confirmPassword;
}
