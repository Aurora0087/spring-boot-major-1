package com.ss.tst1.profile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class ProfileController {

    @Autowired
    private ProfileService profileService;


    @PostMapping("/profile/{uid}")
    public ResponseEntity<ProfileInformationResponse> getProfile(
            @CookieValue(name = "token")String token,
            @PathVariable String uid
    ){
        return profileService.getProfileInformation(uid,token);
    }
}
