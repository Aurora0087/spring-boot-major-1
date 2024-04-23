package com.ss.tst1.post;

import com.ss.tst1.aws.AmazonS3Service;
import com.ss.tst1.comment.Comment;
import com.ss.tst1.comment.CommentParentType;
import com.ss.tst1.comment.CommentResponse;
import com.ss.tst1.comment.CommentService;
import com.ss.tst1.likes.LikeResponse;
import com.ss.tst1.profile.PostProfileResponse;
import com.ss.tst1.user.Role;
import com.ss.tst1.user.User;
import com.ss.tst1.user.UserService;
import com.ss.tst1.videoContent.CreateVideoContentResponse;
import com.ss.tst1.videoContent.VideoContent;
import com.ss.tst1.videoContent.VideoContentResponseToUser;
import com.ss.tst1.videoContent.VideoContentService;
import com.ss.tst1.videoContentCategory.CategoryService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PostService {

    @Autowired
    private AmazonS3Service s3Service;

    @Autowired
    private UserService userService;

    @Autowired
    private VideoContentService videoContentService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CommentService commentService;


    //------------------------------------category
    public ResponseEntity<String> createNewCategory(String name){
        return categoryService.createCategory(name);
    }

    //------------------------------------ video
    public ResponseEntity<CreateVideoContentResponse> postVideoContent(
            MultipartFile thumbnail,
            MultipartFile video,
            String title,
            String categoryID,
            String description,
            String price,
            String uid
    ){
        if (thumbnail.isEmpty()){
            return ResponseEntity.badRequest().body(new CreateVideoContentResponse("Thumbnail is not given.",null));
        }

        if (video.isEmpty()){
            return ResponseEntity.badRequest().body(new CreateVideoContentResponse("Video is not given.",null));
        }

        if (!categoryService.isCategoryExist(Integer.valueOf(categoryID))){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new CreateVideoContentResponse("Category do not exist.",null));
        }

        if (!userService.isUserExistWithId(Integer.valueOf(uid))){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new CreateVideoContentResponse("User do not exist.",null));
        }

        try {
            String thumbnailName = s3Service.uploadFile(thumbnail,"images");
            String videoName = s3Service.uploadFile(video,"videos");
            Float contentPrice = 0F;

            if (price.matches(".*\\d.*")){
                contentPrice = Float.valueOf(price);
            }

            return videoContentService.createVideoContent(Integer.valueOf(uid),Integer.valueOf(categoryID),title,description,contentPrice,thumbnailName,videoName);

        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ResponseEntity<VideoContentResponseToUser> getAllVideoContentNotBaned(String ignore, String limit) {

        if (ignore.isEmpty() || !ignore.matches(".*\\d.*")){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new VideoContentResponseToUser("Ignore Dose not hold any number.",new ArrayList<>(),false));
        }

        if (limit.isEmpty() || !limit.matches(".*\\d.*")){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new VideoContentResponseToUser("Limit Dose not hold any number.",new ArrayList<>(),false));
        }

        return ResponseEntity.ok(videoContentService.getUnBanedContents(Integer.valueOf(ignore),Integer.valueOf(limit)));
    }


    //---------------------------------------------------like
    public ResponseEntity<LikeResponse> toggleVideoLikes(String uId,String vId){

        Integer currentUId = Integer.valueOf(uId);
        Integer currentVId = Integer.valueOf(vId);

        Optional<User> user = userService.getUserById(currentUId);
        Optional<VideoContent> videoContent = videoContentService.getVideoContent(currentVId);
        List<Integer> likeList = new ArrayList<>();

        if (videoContent.isEmpty()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LikeResponse("Video content do not exist.",likeList));
        }



        if (user.isEmpty()){

            likeList = videoContentService.getLikedUserIdByContentId(currentVId);

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LikeResponse("User do not exist.",likeList));
        }

        likeList = videoContentService.likeVideoContent(currentUId,currentVId);

        return ResponseEntity.ok(new LikeResponse("Done",likeList));
    }


    //------------------------------------------------- comment


    public ResponseEntity<String> createComment(String parentId,String uid,String text,String parentType){

        if (!parentId.matches(".*\\d.*") || !uid.matches(".*\\d.*")){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Parameters not given properly.");
        }

        User user = userService.getUserById(Integer.valueOf(uid)).orElseThrow(()->new UsernameNotFoundException("User do not exist, try to log in again."));

        CommentParentType commentParentType = CommentParentType.VIDEO;


        if (parentType.equalsIgnoreCase(CommentParentType.VIDEO.toString())){
            Optional<VideoContent> videoContent = videoContentService.getVideoContent(Integer.valueOf(parentId));

            if (videoContent.isEmpty()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Video content do not exist.");
            }

        }

        else if (parentType.equalsIgnoreCase(CommentParentType.COMMENT.toString())){
            Optional<Comment> comment = commentService.getComment(Integer.valueOf(parentId));

            if (comment.isEmpty()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Video content do not exist.");
            }
            commentParentType = CommentParentType.COMMENT;
        }

        else return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Proper parentType not given");

        Integer newCommentId =  commentService.createComment(Integer.valueOf(parentId),text,user,commentParentType);

        return ResponseEntity.ok(newCommentId.toString());
    }


    public ResponseEntity<String> updateComment(String commentId,String uid,String newText){

        Optional<Comment> comment = commentService.getComment(Integer.valueOf(commentId));
        User user = userService.getUserById(Integer.valueOf(uid)).orElseThrow(()->new UsernameNotFoundException("User do not exist, try to log in again."));

        if (comment.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Comment dose not exist.");
        }

        if (user.getId()==comment.get().getAuthor().getId() || user.getRole()== Role.ADMIN){

            commentService.updateComment(Integer.valueOf(commentId),newText);

            return ResponseEntity.ok("Edit's done.");
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not have authority to edit this.");
    }

    public ResponseEntity<CommentResponse> getCommendById(String commentId){

        if (!commentId.matches(".*\\d.*")){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CommentResponse("Comment id not given properly."));
        }

        Optional<Comment> comment = commentService.getComment(Integer.valueOf(commentId));

        if (comment.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CommentResponse("Comment do not exist."));
        }

        CommentResponse response = new CommentResponse();

        response.setCommentId(comment.get().getId());
        if (!comment.get().getIsPrivate()){

            response.setText(comment.get().getText());

            PostProfileResponse profile = new PostProfileResponse();

            profile.setProfileId(comment.get().getAuthor().getId());
            profile.setAvatarUrl(comment.get().getAuthor().getImageUrl());
            profile.setUserName(comment.get().getAuthor().getUsername());
            profile.setFirstName(comment.get().getAuthor().getFirstName());
            profile.setLastName(comment.get().getAuthor().getLastName());

            response.setAuthor(profile);
        }
        response.setIsPrivate(comment.get().getIsPrivate());

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<GetCommentsResponse> getComments(String parentId,String parentType,String ignore,String limit){

        int ignoreNumber = (ignore.matches(".*\\d.*") && Integer.parseInt(ignore) > 0) ? Integer.parseInt(ignore) : 0;
        int limitNumber = (limit.matches(".*\\d.*") && Integer.parseInt(ignore) > 0) ? Integer.parseInt(limit) : 5;

        if (!parentId.matches(".*\\d.*")){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new GetCommentsResponse("ParentId not given properly."));
        }

        if (parentType.equalsIgnoreCase(CommentParentType.VIDEO.toString())){
            Optional<VideoContent> videoContent = videoContentService.getVideoContent(Integer.valueOf(parentId));

            if (videoContent.isEmpty()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new GetCommentsResponse("Video do not exist."));
            }

            List<CommentResponse> commentList = commentService.getCommentFromSemeParent(videoContent.get().getId(),CommentParentType.VIDEO);

            Boolean isMore = commentList.size()>( ignoreNumber + limitNumber );

            GetCommentsResponse response = new GetCommentsResponse("Done.",commentList.subList(ignoreNumber, Math.min((ignoreNumber + limitNumber),commentList.size())),isMore);

            return ResponseEntity.ok(response);

        }

        else if (parentType.equalsIgnoreCase(CommentParentType.COMMENT.toString())){
            Optional<Comment> comment = commentService.getComment(Integer.valueOf(parentId));

            if (comment.isEmpty()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new GetCommentsResponse("Comment do not exist."));
            }

            List<CommentResponse> commentList = commentService.getCommentFromSemeParent(comment.get().getId(),CommentParentType.COMMENT);

            Boolean isMore = commentList.size()>( ignoreNumber + limitNumber );

            GetCommentsResponse response = new GetCommentsResponse("Done.",commentList.subList(ignoreNumber, Math.min((ignoreNumber + limitNumber),commentList.size())),isMore);

            return ResponseEntity.ok(response);
        }

        else return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new GetCommentsResponse("Proper parent type not given."));
    }
}
