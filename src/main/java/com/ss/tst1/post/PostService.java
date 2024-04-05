package com.ss.tst1.post;

import com.ss.tst1.aws.AmazonS3Service;
import com.ss.tst1.user.UserService;
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

    public ResponseEntity<String> postVideoContent(
            MultipartFile thumbnail,
            MultipartFile video,
            String title,
            String categoryID,
            String description,
            String price,
            String uid
    ){
        if (thumbnail.isEmpty()){
            return ResponseEntity.badRequest().body("Thumbnail is not given");
        }
        if (video.isEmpty()){
            return ResponseEntity.badRequest().body("Video is not given");
        }
        if (!categoryService.isCategoryExist(Integer.valueOf(categoryID))){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Category do not exist.");
        }
        if (!userService.isUserExistWithId(Integer.valueOf(uid))){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User do not exist.");
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
}
