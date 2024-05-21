package com.ss.tst1.videoContent;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class VideoContentResponseToUser {
    @JsonProperty("message")
    private String message;
    @JsonProperty("contents")
    private List<VideoContentForUser> videoContents;
    @JsonProperty("isMore")
    private Boolean isMore;

    public VideoContentResponseToUser(String message) {
        this.message = message;
        this.videoContents = new ArrayList<>();
        this.isMore = false;
    }
}
