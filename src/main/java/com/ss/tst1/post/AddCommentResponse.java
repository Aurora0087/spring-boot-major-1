package com.ss.tst1.post;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AddCommentResponse {
    private String message;
    private Integer commentId;

    public AddCommentResponse(String message) {
        this.message = message;
    }
}
