package com.ss.tst1.comment;

import com.ss.tst1.likes.ContentType;
import com.ss.tst1.likes.LikesService;
import com.ss.tst1.profile.PostProfileResponse;
import com.ss.tst1.user.User;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CommentService {

    @Autowired
    private CommentRepo commentRepo;

    @Autowired
    private LikesService likesService;


    public Optional<Comment> getComment(Integer commentId) {
        return commentRepo.findById(commentId);
    }



    public Integer createComment(Integer parentId, String text, User author, CommentParentType parentType){
        Comment comment = commentRepo.save(new Comment(parentId,text,author,parentType));

        return comment.getId();
    }

    public List<CommentResponse> getCommentFromSemeParent(Integer parentId,CommentParentType parentType){

        List<Comment> commentsWithSameParent =  commentRepo.commentsWithSameParent(parentId,parentType);

        List<CommentResponse> commentResponseList = new ArrayList<>();

        for(Comment comment:commentsWithSameParent){

            CommentResponse response = new CommentResponse();

            response.setCommentId(comment.getId());
            response.setIsPrivate(comment.getIsPrivate());

            if (!comment.getIsPrivate()){
                response.setText(comment.getText());

                PostProfileResponse profile = new PostProfileResponse();

                profile.setProfileId(comment.getAuthor().getId());
                profile.setAvatarUrl(comment.getAuthor().getImageUrl());
                profile.setUserName(comment.getAuthor().getUsername());
                profile.setFirstName(comment.getAuthor().getFirstName());
                profile.setLastName(comment.getAuthor().getLastName());

                response.setAuthor(profile);
            }

            commentResponseList.addLast(response);
        }
        return commentResponseList;
    }

    public Integer updateComment(Integer commentId,String text){
        Comment updatedComment = commentRepo.updateComment(commentId,text);

        return updatedComment.getId();
    }

    public Integer makeCommentPrivate(Integer commentId){
        Comment comment = commentRepo.privateComment(commentId,true);

        return comment.getId();
    }

    public Integer makeCommentPublic(Integer commentId){
        Comment comment = commentRepo.privateComment(commentId,false);

        return comment.getId();
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
