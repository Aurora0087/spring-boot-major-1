package com.ss.tst1.post;

import com.ss.tst1.aws.AmazonS3Service;
import com.ss.tst1.likes.LikeResponse;
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

    public ResponseEntity<String> createNewCategory(String name){
        return categoryService.createCategory(name);
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
}
