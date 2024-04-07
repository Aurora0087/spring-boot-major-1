package com.ss.tst1.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/v1")
public class AdminController {


    @Autowired
    private AdminService adminService;


    @GetMapping("/getusers")
    public ResponseEntity<UserDetailsResponse> getUsers(
            @CookieValue(name = "uuid")String uid,
            @CookieValue(name = "token")String token,
            @RequestBody GetAllUsersRequest request
    ){
        return adminService.getAllUser(uid, token,request.getIgnore(), request.getLimit());
    }

    @PostMapping("/update/userspermissions")
    public ResponseEntity<UserDetailsResponse> editUsers(
            @CookieValue(name = "uuid")String uid,
            @CookieValue(name = "token")String token,
            @RequestBody ChangeUserPermissionRequest request
    ){
        return adminService.updateUsersPermissions(uid, token,request.getUserPermissions());
    }
}
