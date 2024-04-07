package com.ss.tst1.user;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(()->new UsernameNotFoundException("User do not exits."));
    }


    //user register
    public ResponseEntity<String> signUpUser(User user){

        if (isUserExistWithEmail(user.getEmail())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exist with this email.");
        }
        if (isUserExistWithUserName(user.getUsername())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exist with this User-Name.");
        }

        String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.OK).body("User registration done.");
    }

    public Optional<User> getUserByEmailId(String email){
        return userRepository.findByEmail(email);
    }

    public Optional<User> getUserById(Integer id){
        return userRepository.findById(id);
    }


    //user exist functions
    public Boolean isUserExistWithEmail(String email){
        return userRepository.findByEmail(email).isPresent();
    }

    public Boolean isUserExistWithId(Integer id){
        return userRepository.findById(id).isPresent();
    }
    public Boolean isUserExistWithUserName(String userName){
        return userRepository.findByUserName(userName).isPresent();
    }

    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    public Boolean updateUsersPermissions(Integer uid,Boolean locked,Boolean enable,Role userRole){
        Optional<User> user = userRepository.findById(uid);

        if (user.isEmpty()){
            return false;
        }
        userRepository.updateUserByAdmin(uid,locked,enable,userRole);

        return true;
    }
}
