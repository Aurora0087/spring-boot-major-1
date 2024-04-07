package com.ss.tst1.post;

import com.ss.tst1.likes.LikeResponse;
import com.ss.tst1.videoContent.CreateVideoContentResponse;
import com.ss.tst1.videoContent.VideoContentResponseToUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
public class PostController {


    @Autowired
    private PostService postService;

    @PostMapping("/upload")
    public ResponseEntity<CreateVideoContentResponse> uploadFile(
            @RequestParam("thumbnail")MultipartFile thumbnail,
            @RequestParam("video")MultipartFile video,
            @RequestParam("title")String title,
            @RequestParam("categoryID")String categoryID,
            @RequestParam("description")String description,
            @RequestParam("price")String price,
            @CookieValue(name = "uuid")String uid
    )
    {
        return postService.postVideoContent(thumbnail,video,title,categoryID,description,price,uid);
    }

    @PostMapping("/addcategory")
    public ResponseEntity<String> addNewCategory(@RequestParam("category")String category){
        return postService.createNewCategory(category);
    }

    @GetMapping("/getvideocontent")
    public ResponseEntity<VideoContentResponseToUser> getVideoContentForUsers(
            @RequestBody GetVideoContentRequest request
    ){
        return postService.getAllVideoContentNotBaned(request.getIgnore(), request.getLimit());
    }

    @PostMapping("/likevideo")
    public ResponseEntity<LikeResponse> toggleVideoContentLikes(
            @CookieValue(name = "uuid")String uid,
            @RequestParam String vid
    ){
        return postService.toggleVideoLikes(uid,vid);
    }
}
