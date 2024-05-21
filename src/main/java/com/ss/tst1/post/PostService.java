package com.ss.tst1.post;

import com.ss.tst1.aws.AmazonS3Service;
import com.ss.tst1.comment.Comment;
import com.ss.tst1.comment.CommentParentType;
import com.ss.tst1.comment.CommentResponse;
import com.ss.tst1.comment.CommentService;
import com.ss.tst1.jwt.JwtService;
import com.ss.tst1.likes.ContentType;
import com.ss.tst1.likes.LikeResponse;
import com.ss.tst1.likes.LikesService;
import com.ss.tst1.orders.OrderResponse;
import com.ss.tst1.orders.OrderStatus;
import com.ss.tst1.orders.Orders;
import com.ss.tst1.orders.OrdersService;
import com.ss.tst1.profile.PostProfileResponse;
import com.ss.tst1.user.Role;
import com.ss.tst1.user.User;
import com.ss.tst1.user.UserService;
import com.ss.tst1.videoContent.*;
import com.ss.tst1.videoContentCategory.Category;
import com.ss.tst1.videoContentCategory.CategoryService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

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

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private LikesService likesService;


    //------------------------------------category
    public ResponseEntity<String> createNewCategory(String name){
        return categoryService.createCategory(name);
    }

    public ResponseEntity<List<Category>> getAllCategory(){
        return ResponseEntity.ok(categoryService.getAllCategory());
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

            if (!Objects.requireNonNull(thumbnail.getContentType()).contains("image")){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new CreateVideoContentResponse("thumbnail must be a image file."));
            }
            if (!Objects.requireNonNull(video.getContentType()).contains("video")){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new CreateVideoContentResponse("video must be a Video file."));
            }

            String thumbnailName = s3Service.uploadFile(thumbnail,"images");
            String videoName = s3Service.uploadFile(video,"videos");
            float contentPrice = 0F;

            if (price.matches(".*\\d.*")){
                contentPrice = Float.parseFloat(price);
            }

            return videoContentService.createVideoContent(Integer.valueOf(uid),Integer.valueOf(categoryID),title,description,contentPrice,thumbnailName,videoName);

        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ResponseEntity<CreateVideoContentResponse> updateVideoContent(
            String contentId,
            String userid,
            String title,
            String description,
            String price
    ){
        if (!userid.matches(".*\\d.*") || !contentId.matches(".*\\d.*")){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new CreateVideoContentResponse("Properties not given properly.",null));
        }

        Float contentPrice = 0F;

        if (price.matches(".*\\d.*")){
            contentPrice = Float.valueOf(price);
        }

        return videoContentService.editVideoContent(Integer.valueOf(contentId),Integer.valueOf(userid),title,description,contentPrice);

    }

    public ResponseEntity<Boolean> deleteVideoContent(
            String contentId,
            String userid
    ){
        if (!userid.matches(".*\\d.*") || !contentId.matches(".*\\d.*")){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(false);
        }

        return videoContentService.deleteVideoContent(Integer.valueOf(contentId),Integer.valueOf(userid));
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

    public ResponseEntity<GetVideoContentDetailsByIdResponse> getVideoById(String vId,String token){
        return videoContentService.getVideoByIdForUser(vId,token);
    }


    //---------------------------------------------------Watch
    public ResponseEntity<WatchResponse> watchVideo(String contentId,String uid){

        if (!uid.matches(".*\\d.*") || !contentId.matches(".*\\d.*")){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new WatchResponse("Give proper properties."));
        }

        Optional<User> user =userService.getUserById(Integer.valueOf(uid));
        Optional<VideoContent> content = videoContentService.getVideoContent(Integer.valueOf(contentId));

        if (user.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new WatchResponse("user do not exist, please login again."));
        }
        if (content.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new WatchResponse("Content do not exist."));
        }

        if (Objects.equals(content.get().getAuthor().getId(), user.get().getId()) || user.get().getRole().toString().equalsIgnoreCase(Role.ADMIN.toString())){

            String videoUrl = s3Service.generatePreSignedUrl(content.get().getVideoUrl(),new Date(System.currentTimeMillis()+1000*60*60*6)).toString();

            return ResponseEntity.ok(new WatchResponse("Done",videoUrl));
        }
        if (content.get().getIsBlocked()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new WatchResponse("This content is blocked."));
        }
        if (content.get().getPrice()<=0 || ordersService.isBought(Integer.valueOf(uid),Integer.valueOf(contentId))){
            String videoUrl = s3Service.generatePreSignedUrl(content.get().getVideoUrl(),new Date(System.currentTimeMillis()+1000*60*60*6)).toString();

            return ResponseEntity.ok(new WatchResponse("Done",videoUrl));
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new WatchResponse("You have to buy this content to watch this."));

    }

    //---------------------------------------------------like
    public ResponseEntity<LikeResponse> toggleVideoLikes(String uId,String vId){

        Integer currentUId = Integer.valueOf(uId);
        Integer currentVId = Integer.valueOf(vId);

        Optional<User> user = userService.getUserById(currentUId);
        Optional<VideoContent> videoContent = videoContentService.getVideoContent(currentVId);
        List<Integer> likeList = new ArrayList<>();

        if (videoContent.isEmpty()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LikeResponse("Video content do not exist."));
        }



        if (user.isEmpty()){

            likeList = videoContentService.getLikedUserIdByContentId(currentVId);

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LikeResponse("User do not exist.",likeList));
        }

        likeList = videoContentService.likeVideoContent(currentUId,currentVId);

        return ResponseEntity.ok(new LikeResponse("Done",likeList));
    }


    //------------------------------------------------- comment


    public ResponseEntity<AddCommentResponse> createComment(String parentId,String uid,String text,String parentType){

        if (!parentId.matches(".*\\d.*") || !uid.matches(".*\\d.*")){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AddCommentResponse("Parameters not given properly."));
        }

        User user = userService.getUserById(Integer.valueOf(uid)).orElseThrow(()->new UsernameNotFoundException("User do not exist, try to log in again."));

        CommentParentType commentParentType = CommentParentType.VIDEO;


        if (parentType.equalsIgnoreCase(CommentParentType.VIDEO.toString())){
            Optional<VideoContent> videoContent = videoContentService.getVideoContent(Integer.valueOf(parentId));

            if (videoContent.isEmpty()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AddCommentResponse("Video content do not exist."));
            }

        }

        else if (parentType.equalsIgnoreCase(CommentParentType.COMMENT.toString())){
            Optional<Comment> comment = commentService.getComment(Integer.valueOf(parentId));

            if (comment.isEmpty()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AddCommentResponse("Comment content do not exist."));
            }
            commentParentType = CommentParentType.COMMENT;
        }

        else return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AddCommentResponse("Proper parentType not given"));

        Integer newCommentId =  commentService.createComment(Integer.valueOf(parentId),text,user,commentParentType);

        return ResponseEntity.ok(new AddCommentResponse("Done",newCommentId));
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

    public ResponseEntity<Boolean> makePrivateComments(List<String> commentIdList,String uid){

        if (!uid.matches(".*\\d.*")){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(false);
        }

        User user = userService.getUserById(Integer.valueOf(uid)).orElseThrow(()-> new UsernameNotFoundException("User do not exist, try to log in again."));

        for (String commentId:commentIdList){
            if (commentId.matches(".*\\d.*")){
                Optional<Comment> comment = commentService.getComment(Integer.valueOf(commentId));
                if (comment.isEmpty()){
                    continue;
                }

                if (Objects.equals(comment.get().getAuthor().getId(), user.getId()) || user.getRole().toString().equalsIgnoreCase(Role.ADMIN.toString())){
                    commentService.makeCommentPrivate(Integer.valueOf(commentId));
                }
            }
        }
        return ResponseEntity.ok(true);
    }

    public ResponseEntity<Boolean> makePublicComments(List<String> commentIdList,String uid){

        if (!uid.matches(".*\\d.*")){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(false);
        }

        User user = userService.getUserById(Integer.valueOf(uid)).orElseThrow(()-> new UsernameNotFoundException("User do not exist, try to log in again."));

        for (String commentId:commentIdList){
            if (commentId.matches(".*\\d.*")){
                Optional<Comment> comment = commentService.getComment(Integer.valueOf(commentId));
                if (comment.isEmpty()){
                    continue;
                }

                if (Objects.equals(comment.get().getAuthor().getId(), user.getId()) || user.getRole().toString().equalsIgnoreCase(Role.ADMIN.toString())){
                    commentService.makeCommentPublic(Integer.valueOf(commentId));
                }

            }
        }
        return ResponseEntity.ok(true);
    }

    public ResponseEntity<CommentResponse> getCommendById(String commentId,String token){

        String email = jwtService.extractUsername(token);

        Optional<User> currentUser = userService.getUserByEmailId(email);

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

            List<Integer> likedUserIds =  likesService.getLikedUsersIds(comment.get().getId(), ContentType.Comment);
            int likeCount = likedUserIds.size();

            response.setLikeCount(likeCount);
            response.setIsLiked(likedUserIds.contains(currentUser.get().getId()));

            PostProfileResponse profile = new PostProfileResponse();

            profile.setProfileId(comment.get().getAuthor().getId());

            String avatarUrl = s3Service.generatePreSignedUrl(comment.get().getAuthor().getImageUrl(),new Date(System.currentTimeMillis()+1000*60*60*6)).toString();
            profile.setAvatarUrl(avatarUrl);
            profile.setUserName(comment.get().getAuthor().getUsername());
            profile.setFirstName(comment.get().getAuthor().getFirstName());
            profile.setLastName(comment.get().getAuthor().getLastName());

            response.setAuthor(profile);
        }
        response.setIsPrivate(comment.get().getIsPrivate());

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<GetCommentsResponse> getComments(String parentId,String parentType,String ignore,String limit,String token){

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

            List<CommentResponse> commentList = commentService.getCommentFromSemeParent(videoContent.get().getId(),CommentParentType.VIDEO,token);

            Boolean isMore = commentList.size()>( ignoreNumber + limitNumber );

            GetCommentsResponse response = new GetCommentsResponse("Done.",commentList.subList(ignoreNumber, Math.min((ignoreNumber + limitNumber),commentList.size())),isMore);

            return ResponseEntity.ok(response);

        }

        else if (parentType.equalsIgnoreCase(CommentParentType.COMMENT.toString())){
            Optional<Comment> comment = commentService.getComment(Integer.valueOf(parentId));

            if (comment.isEmpty()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new GetCommentsResponse("Comment do not exist."));
            }

            List<CommentResponse> commentList = commentService.getCommentFromSemeParent(comment.get().getId(),CommentParentType.COMMENT,token);

            Boolean isMore = commentList.size()>( ignoreNumber + limitNumber );

            GetCommentsResponse response = new GetCommentsResponse("Done.",commentList.subList(ignoreNumber, Math.min((ignoreNumber + limitNumber),commentList.size())),isMore);

            return ResponseEntity.ok(response);
        }

        else return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new GetCommentsResponse("Proper parent type not given."));
    }

    public ResponseEntity<LikeResponse> likeComment(String commentId,String uid){
        if (!commentId.matches(".*\\d.*") || !uid.matches(".*\\d.*")){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new LikeResponse("Wrong parameters."));
        }

        Optional<Comment> comment = commentService.getComment(Integer.valueOf(commentId));
        User user = userService.getUserById(Integer.valueOf(uid)).orElseThrow(()->new UsernameNotFoundException("User do not exist,please login again."));

        if (comment.isEmpty() || comment.get().getIsPrivate()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new LikeResponse("Comment do not exist or its a private comment."));
        }

        LikeResponse response = new LikeResponse(
                "Done",
                commentService.likeComment(comment.get().getId(),user.getId())
        );

        return  ResponseEntity.ok(response);
    }


    //------------------------------------- search
    public ResponseEntity<VideoContentResponseToUser> getAllVideoContentWithSameCategory(String ignore, String limit,String category) {

        if (ignore.isEmpty() || !ignore.matches(".*\\d.*")){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new VideoContentResponseToUser("Ignore Dose not hold any number.",new ArrayList<>(),false));
        }

        if (limit.isEmpty() || !limit.matches(".*\\d.*")){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new VideoContentResponseToUser("Limit Dose not hold any number.",new ArrayList<>(),false));
        }

        return ResponseEntity.ok(videoContentService.getUnBanedContentsWithSameCategory(Integer.valueOf(ignore),Integer.valueOf(limit),category));
    }

    public ResponseEntity<VideoContentResponseToUser> searchAllVideoContentWithTopic(String ignore, String limit,String topic) {

        if (ignore.isEmpty() || !ignore.matches(".*\\d.*")){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new VideoContentResponseToUser("Ignore Dose not hold any number.",new ArrayList<>(),false));
        }

        if (limit.isEmpty() || !limit.matches(".*\\d.*")){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new VideoContentResponseToUser("Limit Dose not hold any number.",new ArrayList<>(),false));
        }

        return ResponseEntity.ok(videoContentService.searchAndGetUnBanedContentsWithTopic(Integer.valueOf(ignore),Integer.valueOf(limit),topic.trim()));
    }

    public ResponseEntity<VideoContentResponseToUser> findAllContentUploadedByUser(String ignore, String limit, String token) {

        if (ignore.isEmpty() || !ignore.matches(".*\\d.*")){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new VideoContentResponseToUser("Ignore Dose not hold any number.",new ArrayList<>(),false));
        }

        if (limit.isEmpty() || !limit.matches(".*\\d.*")){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new VideoContentResponseToUser("Limit Dose not hold any number.",new ArrayList<>(),false));
        }

        return videoContentService.findAllContentUploadedByUser(Integer.valueOf(ignore),Integer.valueOf(limit),token);
    }

    public ResponseEntity<VideoContentResponseToUser> findAllContentUploadedByUserId(String ignore,String limit,String uid){

        if (ignore.isEmpty() || !ignore.matches(".*\\d.*")){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new VideoContentResponseToUser("Ignore Dose not hold any number."));
        }

        if (limit.isEmpty() || !limit.matches(".*\\d.*")){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new VideoContentResponseToUser("Limit Dose not hold any number."));
        }

        if (uid.isEmpty() || !uid.matches(".*\\d.*")){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new VideoContentResponseToUser("uid Dose not hold any number."));
        }

        return videoContentService.findAllContentUploadedByUserId(Integer.valueOf(ignore),Integer.valueOf(limit),Integer.valueOf(uid));
    }
}
