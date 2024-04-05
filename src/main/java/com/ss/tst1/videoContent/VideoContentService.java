package com.ss.tst1.videoContent;

import com.ss.tst1.aws.AmazonS3Service;
import com.ss.tst1.likes.LikesService;
import com.ss.tst1.user.User;
import com.ss.tst1.user.UserService;
import com.ss.tst1.videoContentCategory.Category;
import com.ss.tst1.videoContentCategory.CategoryService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Service
@AllArgsConstructor
public class VideoContentService {

    @Autowired
    private VideoContentRepo videoContentRepo;

    @Autowired
    private AmazonS3Service s3Service;

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;


    public ResponseEntity<String> createVideoContent(
            Integer authorId,
            Integer categoryID,
            String title,
            String description,
            Float price,
            String imgUrl,
            String videoUrl
    ){
        try{
            User user = userService.getUserById(authorId)
                    .orElseThrow(()->new IllegalArgumentException("User not found"));

            Category category = categoryService.getCategory(categoryID)
                    .orElseThrow(()->new IllegalArgumentException("Category do not exist."));

            VideoContent content = new VideoContent(user,category,title,description,price,imgUrl,videoUrl);
            VideoContent newContent = videoContentRepo.save(content);

            return ResponseEntity.ok("Content id "+newContent.getId()+" uploaded properly.");
        }
        catch (Error e){
            throw new RuntimeException(e);
        }
    }

    public Optional<VideoContent> getVideoContent(Integer contentId) {
       return videoContentRepo.findById(contentId);
    }

    public Optional<VideoContent> getUnBanedContent(Integer contentId) {
        return videoContentRepo.getUnBanedVideoContent(contentId);
    }

    public VideoContentResponseToUser getUnBanedContents(Integer ignore,Integer limit){
        List<VideoContent> unBanedContents = videoContentRepo.findAllUnBanedVideoContent();

        List<VideoContentForUser> responseContents = new ArrayList<>();

        int endLine = Math.min((ignore+limit),unBanedContents.size());

        for (int i= ignore;i<endLine;i++){

            VideoContentForUser response = new VideoContentForUser();
            VideoContent content = unBanedContents.get(i);

            response.setId(content.getId());
            response.setCategory(content.getCategory().getCategoryName());
            response.setPrice(content.getPrice());
            response.setDescription(content.getDescription());
            response.setCreatedAt(content.getCreatedAt());

            String imageUrl = s3Service.generatePreSignedUrl(content.getImgUrl(),new Date(System.currentTimeMillis()+1000*60*60*6)).toString();

            response.setImgUrl(imageUrl);

            responseContents.addLast(response);
        }

        Boolean isMoreExist = ignore+limit < unBanedContents.size();

        return new VideoContentResponseToUser("Done",responseContents,isMoreExist);
    }
}
