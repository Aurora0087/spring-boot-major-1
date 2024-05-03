package com.ss.tst1.post;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ss.tst1.videoContent.VideoContentForUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GetVideoContentDetailsByIdResponse {
    @JsonProperty("message")
    private String message;
    private VideoContentForUser videoContent;

    public GetVideoContentDetailsByIdResponse(String message) {
        this.message = message;
        this.videoContent= new VideoContentForUser();
    }
}
