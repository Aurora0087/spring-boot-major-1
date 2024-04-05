package com.ss.tst1.auth;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthUserResponse {
    @JsonProperty("avatar")
    private String avatar;
    @JsonProperty("uid")
    private String uid;
}
