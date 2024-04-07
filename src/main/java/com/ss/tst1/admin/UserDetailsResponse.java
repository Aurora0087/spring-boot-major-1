package com.ss.tst1.admin;

import com.ss.tst1.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class UserDetailsResponse {
    private String message;
    private List<UserDetails> userLists;
    private Boolean isMore;

    public UserDetailsResponse(String message) {
        this.message = message;
        this.userLists = new ArrayList<>();
        this.isMore =false;
    }
}
