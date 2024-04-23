package com.ss.tst1.videoContent;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Getter
@Setter
public class VideoContentForUser {
    @JsonProperty("vid")
    private Integer id;
    @JsonProperty("title")
    private String title;
    @JsonProperty("description")
    private String description;
    @JsonProperty("price")
    private Float price;
    @JsonProperty("category")
    private String category;
    @JsonProperty("thumbnailUrl")
    private String imgUrl;
    @JsonProperty("createAt")
    private Date createdAt;
    @JsonProperty("likeList")
    private List<Integer> likeList;
}
