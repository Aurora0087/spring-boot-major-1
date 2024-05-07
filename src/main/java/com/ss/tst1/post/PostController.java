package com.ss.tst1.post;

import com.ss.tst1.comment.CommentResponse;
import com.ss.tst1.likes.LikeResponse;
import com.ss.tst1.videoContent.CreateVideoContentResponse;
import com.ss.tst1.videoContent.VideoContentForUser;
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

    @PostMapping("/update/video/{vcid}")
    public ResponseEntity<CreateVideoContentResponse> updateVideoContent(
            @RequestParam("title")String title,
            @RequestParam("description")String description,
            @RequestParam("price")String price,
            @PathVariable String vcid,
            @CookieValue(name = "uuid")String uid
    ){
        return postService.updateVideoContent(vcid,uid,title,description,price);
    }

    @DeleteMapping("/delete/video/{vcid}")
    public ResponseEntity<Boolean> deleteVideoContent(
            @PathVariable String vcid,
            @CookieValue(name = "uuid")String uid
    ){
        return postService.deleteVideoContent(vcid,uid);
    }

    @GetMapping("/get/categorys")
    public ResponseEntity<?> getAllCategory(){
        return postService.getAllCategory();
    }

    @PostMapping("/add/category")
    public ResponseEntity<String> addNewCategory(@RequestParam("category")String category){
        return postService.createNewCategory(category);
    }

    @PostMapping("/get/videocontents")
    public ResponseEntity<VideoContentResponseToUser> getVideoContentsForUsers(
            @RequestBody GetVideoContentRequest request
    ){
        return postService.getAllVideoContentNotBaned(request.getIgnore(), request.getLimit());
    }

    @GetMapping("/get/videocontent/{vcid}")
    public ResponseEntity<GetVideoContentDetailsByIdResponse> getVideoContentForUsers(
            @PathVariable String vcid
    ){
        return postService.getVideoById(vcid);
    }

    @PostMapping("/watch/{vcid}")
    public ResponseEntity<WatchResponse> watchVideoContent(
            @PathVariable String vcid,
            @CookieValue(name = "uuid")String uid
    ){
        return postService.watchVideo(vcid,uid);
    }

    @PostMapping("/search/byCategory/{category}")
    public ResponseEntity<VideoContentResponseToUser> getVideoContentsForUsers(
            @RequestBody GetVideoContentRequest request,
             @PathVariable String category
    ){
        return postService.getAllVideoContentWithSameCategory(request.getIgnore(),request.getLimit(),category);
    }

    @PostMapping("/search/{topic}")
    public ResponseEntity<VideoContentResponseToUser> searchVideoContentsForUsers(
            @RequestBody GetVideoContentRequest request,
            @PathVariable String topic
    ){
        return postService.searchAllVideoContentWithTopic(request.getIgnore(), request.getLimit(), topic);
    }

    @PostMapping("/like/video/{vcid}")
    public ResponseEntity<LikeResponse> toggleVideoContentLikes(
            @CookieValue(name = "uuid")String uid,
            @PathVariable String vcid
    ){
        return postService.toggleVideoLikes(uid,vcid);
    }

    @PostMapping("/add/comment")
    public ResponseEntity<String> addComment(
            @CookieValue(name = "uuid")String uid,
            @RequestBody CreateCommentRequest request
    ){
        return postService.createComment(request.getParentId(),uid, request.getText(), request.getParentType());
    }

    @PostMapping("/like/comment/{commentid}")
    public ResponseEntity<LikeResponse> toggleCommentLike(
            @CookieValue(name = "uuid")String uid,
            @PathVariable String commentid
    ){
        return postService.likeComment(commentid,uid);
    }

    @PostMapping("/edit/comment")
    public ResponseEntity<String> updateComment(
            @CookieValue(name = "uuid")String uid,
            @RequestBody UpdateCommentRequest request
    ){
        return postService.updateComment(request.getCommentId(),uid, request.getNewText());
    }

    @PostMapping("/edit/comment/private")
    public ResponseEntity<Boolean> makePrivateComments(
            @CookieValue(name = "uuid")String uid,
            @RequestBody PrivatePublicCommentRequest request
    ){
        return postService.makePrivateComments(request.getCommentIds(),uid);
    }

    @PostMapping("/edit/comment/public")
    public ResponseEntity<Boolean> makePublicComments(
            @CookieValue(name = "uuid")String uid,
            @RequestBody PrivatePublicCommentRequest request
    ){
        return postService.makePrivateComments(request.getCommentIds(),uid);
    }

    @GetMapping("/get/comment/{commentId}")
    public ResponseEntity<CommentResponse> getCommentById(
            @PathVariable String commentId
    ){
        return postService.getCommendById(commentId);
    }
    @PostMapping("/get/childComment")
    public ResponseEntity<GetCommentsResponse> getCommentsWithSameParent(
            @RequestBody GetCommentsRequest request
    ){
        return postService.getComments(request.getParentId(), request.getParentType(), request.getIgnore(), request.getLimit());
    }
}
