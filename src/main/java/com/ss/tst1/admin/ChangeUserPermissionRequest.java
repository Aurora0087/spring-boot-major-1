package com.ss.tst1.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ChangeUserPermissionRequest {
    private List<ChangeUserPermission> userPermissions;
}
