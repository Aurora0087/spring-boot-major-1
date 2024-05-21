package com.ss.tst1.comment;

import com.ss.tst1.aws.AmazonS3Service;
import com.ss.tst1.jwt.JwtService;
import com.ss.tst1.likes.ContentType;
import com.ss.tst1.likes.Likes;
import com.ss.tst1.likes.LikesService;
import com.ss.tst1.profile.PostProfileResponse;
import com.ss.tst1.user.User;
import com.ss.tst1.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CommentService {

    @Autowired
    private CommentRepo commentRepo;

    @Autowired
    private LikesService likesService;

    @Autowired
    private AmazonS3Service s3Service;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;


    public Optional<Comment> getComment(Integer commentId) {
        return commentRepo.findById(commentId);
    }



    public Integer createComment(Integer parentId, String text, User author, CommentParentType parentType){
        Comment comment = commentRepo.save(new Comment(parentId,text,author,parentType));

        return comment.getId();
    }

    public List<CommentResponse> getCommentFromSemeParent(Integer parentId,CommentParentType parentType,String token){

        String email = jwtService.extractUsername(token);

        Optional<User> currentUser = userService.getUserByEmailId(email);

        List<Comment> commentsWithSameParent =  commentRepo.commentsWithSameParent(parentId,parentType);

        List<CommentResponse> commentResponseList = new ArrayList<>();

        for(Comment comment:commentsWithSameParent){

            CommentResponse response = new CommentResponse();

            response.setCommentId(comment.getId());
            response.setIsPrivate(comment.getIsPrivate());

            if (!comment.getIsPrivate()){
                response.setText(comment.getText());

                ContentType contentType = parentType == CommentParentType.VIDEO ? ContentType.Video : ContentType.Comment;

                List<Integer> likedUserIds =  likesService.getLikedUsersIds(comment.getId(),contentType);
                int likeCount = likedUserIds.size();

                response.setLikeCount(likeCount);
                response.setIsLiked(likedUserIds.contains(currentUser.get().getId()));

                PostProfileResponse profile = new PostProfileResponse();

                profile.setProfileId(comment.getAuthor().getId());

                String avatarUrl = s3Service.generatePreSignedUrl(comment.getAuthor().getImageUrl(),new Date(System.currentTimeMillis()+1000*60*60*6)).toString();

                profile.setAvatarUrl(avatarUrl);
                profile.setUserName(comment.getAuthor().getUsername());
                profile.setFirstName(comment.getAuthor().getFirstName());
                profile.setLastName(comment.getAuthor().getLastName());

                response.setAuthor(profile);
            }

            commentResponseList.add(response);
        }
        return commentResponseList;
    }

    public Integer updateComment(Integer commentId,String text){
        commentRepo.updateComment(commentId,text);

        return commentId;
    }

    public Integer makeCommentPrivate(Integer commentId){
        commentRepo.privateComment(commentId,true);

        return commentId;
    }

    public Integer makeCommentPublic(Integer commentId){
        commentRepo.privateComment(commentId,false);

        return commentId;
    }

    public void deleteCommentAndChildesPermanently(Integer commentId){

        List<Comment> commentsWithSameParent =  commentRepo.commentsWithSameParent(commentId,CommentParentType.COMMENT);

        for (Comment comment:commentsWithSameParent){
            deleteCommentAndChildesPermanently(comment.getId());
        }

        commentRepo.deleteById(commentId);
    }

    public List<Integer> likeComment(Integer commentId,Integer uid){
        return likesService.toggleLike(uid,commentId, ContentType.Comment);
    }
}
