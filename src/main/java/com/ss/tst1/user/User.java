package com.ss.tst1.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User implements UserDetails {

    @SequenceGenerator(
            name = "user_sequence",
            sequenceName = "user_sequence",
            initialValue = 1000,
            allocationSize = 1
    )
    @Id
    @GeneratedValue(
            strategy = GenerationType.AUTO,
            generator = "user_sequence"
    )
    private Integer id;
    @Column(nullable = false,unique = true,updatable = false)
    private String email;
    @Column(nullable = false,unique = true)
    private String password;
    private String firstName;
    private String lastName;

    private String imageUrl;
    private String bgImage;

    private String bio;
    private Boolean locked;
    private Boolean enabled;

    @Enumerated(EnumType.STRING)
    private Role role;

    //appUser constructor without id
    public User(String firstName, String lastName, String email, String password,Role role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.bio="";
        this.imageUrl="avatar/defImage.jpg";
        this.bgImage="";
        this.role = role;
        this.locked = false;
        this.enabled =true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
