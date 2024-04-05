package com.ss.tst1.videoContent;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
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
