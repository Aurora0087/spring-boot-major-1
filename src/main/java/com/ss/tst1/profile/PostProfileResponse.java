package com.ss.tst1.profile;


import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
public class PostProfileResponse {
    private Integer profileId;
    private String userName;
    private String firstName;
    private String lastName;
    private String avatarUrl;
}
