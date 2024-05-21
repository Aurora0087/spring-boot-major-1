package com.ss.tst1.admin;

import lombok.*;

import java.util.List;

@Data
public class ChangeUserPermissionRequest {
    private List<ChangeUserPermission> userPermissions;
}
