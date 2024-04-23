package com.ss.tst1.post;

import com.ss.tst1.comment.CommentResponse;
import com.ss.tst1.likes.LikeResponse;
import com.ss.tst1.videoContent.CreateVideoContentResponse;
import com.ss.tst1.videoContent.VideoContentResponseToUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@CrossOrigin(origins = "http://localhost:3000")
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

    @PostMapping("/add/category")
    public ResponseEntity<String> addNewCategory(@RequestParam("category")String category){
        return postService.createNewCategory(category);
    }

    @GetMapping("/get/videocontent")
    public ResponseEntity<VideoContentResponseToUser> getVideoContentForUsers(
            @RequestBody GetVideoContentRequest request
    ){
        return postService.getAllVideoContentNotBaned(request.getIgnore(), request.getLimit());
    }

    @PostMapping("/like/video")
    public ResponseEntity<LikeResponse> toggleVideoContentLikes(
            @CookieValue(name = "uuid")String uid,
            @RequestParam String vid
    ){
        return postService.toggleVideoLikes(uid,vid);
    }

    @PostMapping("/add/comment")
    public ResponseEntity<String> addComment(
            @CookieValue(name = "uuid")String uid,
            @RequestBody CreateCommentRequest request
    ){
        return postService.createComment(request.getParentId(),uid, request.getText(), request.getParentType());
    }

    @PostMapping("/edit/comment")
    public ResponseEntity<String> updateComment(
            @CookieValue(name = "uuid")String uid,
            @RequestBody UpdateCommentRequest request
    ){
        return postService.updateComment(request.getCommentId(),uid, request.getNewText());
    }

    @GetMapping("/get/comment/{commentId}")
    public ResponseEntity<CommentResponse> getCommentById(
            @PathVariable String commentId
    ){
        return postService.getCommendById(commentId);
    }
    @GetMapping("/get/childComment")
    public ResponseEntity<GetCommentsResponse> getCommentsWithSameParent(
            @RequestBody GetCommentsRequest request
    ){
        return postService.getComments(request.getParentId(), request.getParentType(), request.getIgnore(), request.getLimit());
    }
}
