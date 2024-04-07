package com.ss.tst1.admin;


import com.ss.tst1.jwt.JwtService;
import com.ss.tst1.user.Role;
import com.ss.tst1.user.User;
import com.ss.tst1.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AdminService {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    public ResponseEntity<UserDetailsResponse> getAllUser(String uid,String token,String ignore,String limit){

        if (ignore.isEmpty() || !ignore.matches(".*\\d.*")){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body( new UserDetailsResponse("Ignore parameter not provided properly."));
        }

        if (limit.isEmpty() || !limit.matches(".*\\d.*")){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body( new UserDetailsResponse("Limit parameter not provided properly."));
        }

        String currentUserEmail = jwtService.extractUsername(token);

        Optional<User> user = userService.getUserByEmailId(currentUserEmail);

        if (user.isPresent() && Objects.equals(user.get().getId(), Integer.valueOf(uid)))
        {
            Integer ignoreAmount= Integer.valueOf(ignore);
            Integer limitAmount = Integer.valueOf(limit);

            List<User> allUser = userService.getAllUser();
            List<UserDetails> responseUsersList = new ArrayList<>();

            int endLine = Math.min( ignoreAmount + limitAmount, allUser.size());

            for (int i = ignoreAmount; i < endLine; i++)
            {
                User responseUser = allUser.get(i);

                responseUsersList.addLast(
                        new UserDetails(
                                responseUser.getId().toString(),
                                responseUser.getUsername(),
                                responseUser.getEmail(),
                                responseUser.getFirstName(),
                                responseUser.getLastName(),
                                responseUser.getLocked(),
                                responseUser.getEnabled(),
                                responseUser.getRole().toString())
                );
            }

            Boolean isMoreExist = ignoreAmount+limitAmount < allUser.size();

            return ResponseEntity.ok( new UserDetailsResponse("Done", responseUsersList, isMoreExist));
        }

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body( new UserDetailsResponse("Your cookies are corrupted, login again then try again."));
    }


    public ResponseEntity<UserDetailsResponse> updateUsersPermissions(String uid, String token, List<ChangeUserPermission> request) {

        String currentUserEmail = jwtService.extractUsername(token);

        Optional<User> user = userService.getUserByEmailId(currentUserEmail);

        if (user.isPresent() && Objects.equals(user.get().getId(), Integer.valueOf(uid))){

            for(ChangeUserPermission userPermissions :request){

                if (Integer.valueOf(userPermissions.getId()).equals(user.get().getId())){

                    return ResponseEntity
                            .status(HttpStatus.FORBIDDEN)
                            .body(new UserDetailsResponse("You can not update your own Permissions."));
                }

                if (Objects.equals(user.get().getEmail(), "debrajbanshi1@gmail.com")){

                    return ResponseEntity
                            .status(HttpStatus.FORBIDDEN)
                            .body(new UserDetailsResponse("You can not update this account's Permissions."));
                }

                if (userPermissions.getId().matches(".*\\d.*"))
                {
                    Role updatedRole = userPermissions.getRole().equals("ADMIN") ? Role.ADMIN : Role.USER;

                    userService.updateUsersPermissions(Integer.valueOf(userPermissions.getId()), userPermissions.getLocked(), userPermissions.getEnabled(),updatedRole);
                }

            }

            return getAllUser(uid,token,"0","10");
        }
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new UserDetailsResponse("Your cookies are corrupted, login again then try again."));
    }
}
