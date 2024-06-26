package com.ss.tst1.comment;

import com.ss.tst1.profile.PostProfileResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponse {

    private Boolean isPrivate;
    private String message;
    private Integer commentId;
    private String text;
    private PostProfileResponse author;
    private Integer likeCount;
    private Boolean isLiked;
    private Integer commentCount;

    public CommentResponse(String message) {
        this.message = message;
    }

    public CommentResponse(Boolean isPrivate,String message) {
        this.isPrivate = isPrivate;
        this.message = message;
    }
}
