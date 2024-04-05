package com.ss.tst1.likes;

import com.ss.tst1.comment.Comment;
import com.ss.tst1.comment.CommentService;
import com.ss.tst1.user.User;
import com.ss.tst1.user.UserService;
import com.ss.tst1.videoContent.VideoContent;
import com.ss.tst1.videoContent.VideoContentService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LikesService {

    @Autowired
    private LikesRepo likesRepo;

    @Autowired
    private UserService userService;

    @Autowired
    private VideoContentService videoContentService;

    @Autowired
    private CommentService commentService;

    public ResponseEntity<String> doLike(Integer userId,Integer contentId,ContentType contentType){
        try{
            User user = userService.getUserById(userId)
                    .orElseThrow(()-> new IllegalArgumentException("User do not exist."));

            if (contentType==ContentType.Video){
                VideoContent videoContent = videoContentService.getVideoContent(contentId)
                        .orElseThrow(()-> new IllegalArgumentException("Video Content do not exist."));

                Likes newLike = new Likes(user,ContentType.Video,contentId);
                
                likesRepo.save(newLike);
                
                return ResponseEntity.ok("Successes.");
            } else if (contentType==ContentType.Comment) {
                Comment comment = commentService.getComment(contentId)
                        .orElseThrow(()-> new IllegalArgumentException("Comment do not exist."));

                Likes newLike = new Likes(user,ContentType.Comment,contentId);

                likesRepo.save(newLike);

                return ResponseEntity.ok("Successes.");
            }
            else {
                return ResponseEntity.ok("You cant like this.");
            }


        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }
}
