package com.ss.tst1.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class ChangeUserPermission {

    private String id;
    private Boolean locked;
    private Boolean enabled;
    private String role;
}
