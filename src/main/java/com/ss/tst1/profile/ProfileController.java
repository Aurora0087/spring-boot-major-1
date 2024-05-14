package com.ss.tst1.profile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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

    @PostMapping("/profile/edit/{uid}")
    public ResponseEntity<?> editProfile(
            @RequestBody ProfileUpdateRequest request,
            @PathVariable String uid,
            @CookieValue(name = "token")String token
    ){
        return profileService.editProfile(request.getNewFirstName(),request.getNewLastName(),request.getNewBio(),uid,token);
    }

    @PostMapping("/profile/edit/images/{uid}")
    public ResponseEntity<?> updateProfileImages(
            @RequestParam("bgImage") MultipartFile bgImage,
            @RequestParam("avatar")MultipartFile avatar,
            @PathVariable String uid,
            @CookieValue(name = "token")String token
    ) throws IOException {
        return profileService.updateProfileImages(bgImage,avatar,uid,token);
    }
}
