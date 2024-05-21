package com.ss.tst1.profile;

import lombok.Data;

@Data
public class UpdatePasswordRequest {
    private String oldPassword;
    private String newPassword;
    private String confirmNewPassword;
}
