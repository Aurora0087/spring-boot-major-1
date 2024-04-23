package com.ss.tst1.likes;


import lombok.*;

import java.util.List;

@AllArgsConstructor
@Setter
@Getter
public class LikeResponse {
    private String message;
    private List<Integer> likeList;
}
