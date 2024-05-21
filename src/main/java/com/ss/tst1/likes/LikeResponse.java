package com.ss.tst1.likes;


import lombok.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Setter
@Getter
public class LikeResponse {
    private String message;
    private List<Integer> likeList;
    public LikeResponse(String message) {
        this.message = message;
        this.likeList = new ArrayList<>();
    }
}
