package com.ss.tst1.profile;

import com.ss.tst1.aws.AmazonS3Service;
import com.ss.tst1.jwt.JwtService;
import com.ss.tst1.user.Role;
import com.ss.tst1.user.User;
import com.ss.tst1.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ProfileService {

    @Autowired
    private UserService userService;

    @Autowired
    private AmazonS3Service s3Service;

    @Autowired
    private JwtService jwtService;

    public ResponseEntity<ProfileInformationResponse> getProfileInformation(String userId,String token){

        if (!userId.matches(".*\\d.*")){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ProfileInformationResponse("Property not given properly."));
        }

        String email = jwtService.extractUsername(token);
        User currentUser = userService.getUserByEmailId(email).orElseThrow(()-> new IllegalArgumentException("User do not exist."));

        Optional<User> getUser = userService.getUserById(Integer.valueOf(userId));

        if (getUser.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ProfileInformationResponse("User with "+ userId+" do not exist."));
        }

        if (getUser.get().getLocked() && !currentUser.getRole().toString().equalsIgnoreCase(Role.ADMIN.toString())){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ProfileInformationResponse("User with "+ userId+" Locked/Blocked."));
        }

        ProfileInformationResponse response =new ProfileInformationResponse();

        String avatarUrl = s3Service.generatePreSignedUrl(getUser.get().getImageUrl(),new Date(System.currentTimeMillis()+1000*60*60*6)).toString();
        String bgImage = "";

        if (!getUser.get().getBgImage().isEmpty()){
            bgImage = s3Service.generatePreSignedUrl(getUser.get().getBgImage(),new Date(System.currentTimeMillis()+1000*60*60*6)).toString();
        }

        response.setMessage("Done");
        response.setProfileId(getUser.get().getId());
        response.setBio(getUser.get().getBio());
        response.setAvatarUrl(avatarUrl);
        response.setBgImage(bgImage);
        response.setFirstName(getUser.get().getFirstName());
        response.setLastName(getUser.get().getLastName());
        response.setUserName(getUser.get().getUsername());

        if (currentUser.getId() == getUser.get().getId() || currentUser.getRole().toString().equalsIgnoreCase(Role.ADMIN.toString())){
            response.setCanChange(true);
            response.setEmail(getUser.get().getEmail());
        }

        return ResponseEntity.ok(response);
    }
}
