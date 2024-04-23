package com.ss.tst1.post;

import com.ss.tst1.comment.CommentResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetCommentsResponse {
    private String message;
    private List<CommentResponse> commentList;
    private Boolean isMore;

    public GetCommentsResponse(String message) {
        this.message = message;
        this.commentList = new ArrayList<>();
        this.isMore = false;
    }
}
