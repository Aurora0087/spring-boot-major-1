package com.ss.tst1.register;


import com.ss.tst1.user.Role;
import com.ss.tst1.user.User;
import com.ss.tst1.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@AllArgsConstructor
public class RegistrationService {

    @Autowired
    private UserService userService;

    //registering users as user in database
    public ResponseEntity<RegisterResponse> register(RegistrationRequest request){

        if (!Objects.equals(request.getPassword(), request.getConfirmPassword())){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new RegisterResponse("The passwords did not match."));
        }

        ResponseEntity<String> response=null;
        if (Objects.equals(request.getEmail(), "debrajbanshi1@gmail.com")){
            response = userService.signUpUser(
                    new User(
                            request.getFirstName(),
                            request.getLastName(),
                            request.getEmail(),
                            request.getPassword(),
                            Role.ADMIN)
            );
        }
        else {
            response = userService.signUpUser(
                    new User(
                            request.getFirstName(),
                            request.getLastName(),
                            request.getEmail(),
                            request.getPassword(),
                            Role.USER)
            );
        }
        return ResponseEntity.status(response.getStatusCode()).body(new RegisterResponse(response.getBody()));
    }
}
