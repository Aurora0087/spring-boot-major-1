package com.ss.tst1.auth;

import com.ss.tst1.aws.AmazonS3Service;
import com.ss.tst1.jwt.JwtService;
import com.ss.tst1.user.User;
import com.ss.tst1.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;

    @Autowired
    private final JwtService jwtService;

    @Autowired
    private final UserService userService;

    @Autowired
    private  final AmazonS3Service s3Service;

    public ResponseEntity<AuthenticationResponse> signIn(
            LoginRequest request
    ){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        if (authentication.isAuthenticated()){

            String generatedToken = jwtService.generateToken(request.getEmail());

            Optional<User> user = userService.getUserByEmailId(request.getEmail());

            ResponseCookie token = ResponseCookie.from("token",generatedToken)
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(TimeUnit.DAYS.toSeconds(1))
                    .build();

            ResponseCookie uuid = ResponseCookie.from("uuid",(user.get().getId()).toString())
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(TimeUnit.DAYS.toSeconds(1))
                    .build();

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.SET_COOKIE,token.toString());
            headers.add(HttpHeaders.SET_COOKIE,uuid.toString());

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .body
                            (new AuthenticationResponse("success",generatedToken));
        }
        else {
            throw new UsernameNotFoundException("User not found.");
        }
    }

    public ResponseEntity<AuthUserResponse> authenticateUser(String token){
        String email = jwtService.extractUsername(token);
        User user = userService.getUserByEmailId(email).orElseThrow(()-> new IllegalArgumentException("User do not exist."));

        URL avatarUrl = s3Service.generatePreSignedUrl("avatar/"+user.getImageUrl(),new Date(System.currentTimeMillis()+1000*60*60*6));

        return ResponseEntity.ok(new AuthUserResponse(avatarUrl.toString(),user.getId().toString()));
    }

    public ResponseEntity<String> deleteUserCookies(){

        ResponseCookie token = ResponseCookie.from("token",null)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();

        ResponseCookie uuid = ResponseCookie.from("uuid",null)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE,token.toString());
        headers.add(HttpHeaders.SET_COOKIE,uuid.toString());

        return ResponseEntity.ok().headers(headers).body("Log-out.");
    }
}