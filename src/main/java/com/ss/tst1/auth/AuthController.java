package com.ss.tst1.auth;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticateUser(
            @RequestBody LoginRequest request
    ){
        return authService.signIn(request);
    }

    @GetMapping("/log-out")
    public ResponseEntity<String> logout(){
        return authService.deleteUserCookies();
    }

    @GetMapping(path = "/auth")
    public ResponseEntity<AuthUserResponse> auth(
            @CookieValue(name = "token")String token
    ){
        return authService.authenticateUser(token);
    }
}
