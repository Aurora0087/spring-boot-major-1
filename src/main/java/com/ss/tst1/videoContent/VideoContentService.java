package com.ss.tst1.videoContent;

import com.ss.tst1.aws.AmazonS3Service;
import com.ss.tst1.likes.ContentType;
import com.ss.tst1.likes.LikesService;
import com.ss.tst1.post.GetVideoContentDetailsByIdResponse;
import com.ss.tst1.profile.PostProfileResponse;
import com.ss.tst1.user.Role;
import com.ss.tst1.user.User;
import com.ss.tst1.user.UserService;
import com.ss.tst1.videoContentCategory.Category;
import com.ss.tst1.videoContentCategory.CategoryService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;


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

    @Autowired
    private LikesService likesService;


    //                     create
    public ResponseEntity<CreateVideoContentResponse> createVideoContent(
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

            VideoContent content = new VideoContent(user,category,title,description.trim(),price,imgUrl,videoUrl);
            VideoContent newContent = videoContentRepo.save(content);

            return ResponseEntity.ok(new CreateVideoContentResponse("Done",newContent.getId()));
        }
        catch (Error e){
            throw new RuntimeException(e);
        }
    }

    //                                 edit
    public ResponseEntity<CreateVideoContentResponse> editVideoContent(
            Integer contentId,
            Integer userid,
            String title,
            String description,
            Float price
    ){

        User user = userService.getUserById(userid)
                .orElseThrow(()->new UsernameNotFoundException("User with id:"+ userid+" do not exist"));
        VideoContent content = videoContentRepo.findById(contentId)
                .orElseThrow(()->new IllegalArgumentException("Content do not exist."));

        if (Objects.equals(user.getId(), content.getAuthor().getId()) || user.getRole().toString().equalsIgnoreCase(Role.ADMIN.toString())){
            videoContentRepo.updateContent(content.getId(),title,description.trim(),price);

            return ResponseEntity.ok(new CreateVideoContentResponse("Done",content.getId()));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new CreateVideoContentResponse("Your not authotized to edit this content.",contentId));
    }


    //                                      delete
    public ResponseEntity<Boolean> deleteVideoContent(
            Integer contentId,
            Integer userid
    ){
        User user = userService.getUserById(userid)
                .orElseThrow(()->new UsernameNotFoundException("User with id:"+ userid+" do not exist"));
        VideoContent content = videoContentRepo.findById(contentId)
                .orElseThrow(()->new IllegalArgumentException("Content do not exist."));

        if (Objects.equals(user.getId(), content.getAuthor().getId()) || user.getRole().toString().equalsIgnoreCase(Role.ADMIN.toString())){

            s3Service.deleteFile(content.getImgUrl());
            s3Service.deleteFile(content.getVideoUrl());

            videoContentRepo.deleteById(contentId);

            return ResponseEntity.ok(true);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
    }

    public Optional<VideoContent> getVideoContent(Integer contentId) {
       return videoContentRepo.findById(contentId);
    }

    public Optional<VideoContent> getUnBanedContent(Integer contentId) {
        return videoContentRepo.getUnBanedVideoContent(contentId);
    }

    //
    public VideoContentResponseToUser getUnBanedContents(Integer ignore,Integer limit){
        List<VideoContent> unBanedContents = videoContentRepo.findAllUnBanedVideoContent();

        List<VideoContentForUser> responseContents = new ArrayList<>();

        int endLine = Math.min((ignore+limit),unBanedContents.size());

        for (int i= ignore;i<endLine;i++){

            VideoContentForUser response = new VideoContentForUser();
            PostProfileResponse profileResponse = new PostProfileResponse();
            VideoContent content = unBanedContents.get(i);

            response.setId(content.getId());
            response.setCategory(content.getCategory().getCategoryName());
            response.setTitle(content.getTitle());
            response.setPrice(content.getPrice());
            response.setDescription(content.getDescription());
            response.setCreatedAt(content.getCreatedAt());

            String imageUrl = s3Service.generatePreSignedUrl(content.getImgUrl(),new Date(System.currentTimeMillis()+1000*60*60*6)).toString();

            response.setImgUrl(imageUrl);

            List<Integer> likedUser = likesService.getLikedUsersIds(content.getId(), ContentType.Video);

            response.setLikeList(likedUser);

            profileResponse.setProfileId(content.getAuthor().getId());
            profileResponse.setFirstName(content.getAuthor().getFirstName());
            profileResponse.setLastName(content.getAuthor().getLastName());

            String avatarUrl = s3Service.generatePreSignedUrl(content.getAuthor().getImageUrl(),new Date(System.currentTimeMillis()+1000*60*60*6)).toString();

            profileResponse.setAvatarUrl(avatarUrl);
            profileResponse.setUserName(content.getAuthor().getUsername());

            response.setAuthor(profileResponse);

            responseContents.addLast(response);
        }

        Boolean isMoreExist = ignore+limit < unBanedContents.size();

        return new VideoContentResponseToUser("Done",responseContents,isMoreExist);
    }

    //---------------------------------------------- search
    public VideoContentResponseToUser getUnBanedContentsWithSameCategory(Integer ignore,Integer limit,String category){
        List<VideoContent> unBanedContents = videoContentRepo.findAllUnBanedVideoContentWithSameCategory(category);

        List<VideoContentForUser> responseContents = new ArrayList<>();

        int endLine = Math.min((ignore+limit),unBanedContents.size());

        for (int i= ignore;i<endLine;i++){

            VideoContentForUser response = new VideoContentForUser();
            PostProfileResponse profileResponse = new PostProfileResponse();
            VideoContent content = unBanedContents.get(i);

            response.setId(content.getId());
            response.setCategory(content.getCategory().getCategoryName());
            response.setTitle(content.getTitle());
            response.setPrice(content.getPrice());
            response.setDescription(content.getDescription());
            response.setCreatedAt(content.getCreatedAt());

            String imageUrl = s3Service.generatePreSignedUrl(content.getImgUrl(),new Date(System.currentTimeMillis()+1000*60*60*6)).toString();

            response.setImgUrl(imageUrl);

            List<Integer> likedUser = likesService.getLikedUsersIds(content.getId(), ContentType.Video);

            response.setLikeList(likedUser);

            profileResponse.setProfileId(content.getAuthor().getId());
            profileResponse.setFirstName(content.getAuthor().getFirstName());
            profileResponse.setLastName(content.getAuthor().getLastName());

            String avatarUrl = s3Service.generatePreSignedUrl(content.getAuthor().getImageUrl(),new Date(System.currentTimeMillis()+1000*60*60*6)).toString();

            profileResponse.setAvatarUrl(avatarUrl);
            profileResponse.setUserName(content.getAuthor().getUsername());

            response.setAuthor(profileResponse);

            responseContents.addLast(response);
        }

        Boolean isMoreExist = ignore+limit < unBanedContents.size();

        return new VideoContentResponseToUser("Done",responseContents,isMoreExist);
    }

    public VideoContentResponseToUser searchAndGetUnBanedContentsWithTopic(Integer ignore,Integer limit,String topic){

        List<VideoContent> unBanedContents = videoContentRepo.findAllUnBanedVideoContentWithTopic(topic);

        List<VideoContentForUser> responseContents = new ArrayList<>();

        int endLine = Math.min((ignore+limit),unBanedContents.size());

        for (int i= ignore;i<endLine;i++){

            VideoContentForUser response = new VideoContentForUser();
            PostProfileResponse profileResponse = new PostProfileResponse();
            VideoContent content = unBanedContents.get(i);

            response.setId(content.getId());
            response.setCategory(content.getCategory().getCategoryName());
            response.setTitle(content.getTitle());
            response.setPrice(content.getPrice());
            response.setDescription(content.getDescription());
            response.setCreatedAt(content.getCreatedAt());

            String imageUrl = s3Service.generatePreSignedUrl(content.getImgUrl(),new Date(System.currentTimeMillis()+1000*60*60*6)).toString();

            response.setImgUrl(imageUrl);

            List<Integer> likedUser = likesService.getLikedUsersIds(content.getId(), ContentType.Video);

            response.setLikeList(likedUser);

            profileResponse.setProfileId(content.getAuthor().getId());
            profileResponse.setFirstName(content.getAuthor().getFirstName());
            profileResponse.setLastName(content.getAuthor().getLastName());

            String avatarUrl = s3Service.generatePreSignedUrl(content.getAuthor().getImageUrl(),new Date(System.currentTimeMillis()+1000*60*60*6)).toString();

            profileResponse.setAvatarUrl(avatarUrl);
            profileResponse.setUserName(content.getAuthor().getUsername());

            response.setAuthor(profileResponse);

            responseContents.addLast(response);
        }

        Boolean isMoreExist = ignore+limit < unBanedContents.size();

        return new VideoContentResponseToUser("Done",responseContents,isMoreExist);
    }

    //
    public ResponseEntity<GetVideoContentDetailsByIdResponse> getVideoByIdForUser(String vId){

        if (!vId.matches(".*\\d.*")){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new GetVideoContentDetailsByIdResponse("Video content id did not given properly."));
        }

        Optional<VideoContent> videoContent = getVideoContent(Integer.valueOf(vId));

        if (videoContent.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new GetVideoContentDetailsByIdResponse("Video content do not exist."));
        }
        if (videoContent.get().getIsBlocked()){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new GetVideoContentDetailsByIdResponse("Video content is blocked."));
        }

        VideoContentForUser videoContentResponse = new VideoContentForUser();
        PostProfileResponse profileResponse = new PostProfileResponse();

        videoContentResponse.setId(videoContent.get().getId());
        videoContentResponse.setImgUrl(videoContent.get().getImgUrl());
        videoContentResponse.setTitle(videoContent.get().getTitle());
        videoContentResponse.setDescription(videoContent.get().getDescription());
        videoContentResponse.setCreatedAt(videoContent.get().getCreatedAt());
        videoContentResponse.setCategory(videoContent.get().getCategory().getCategoryName());

        List<Integer> likedUser = likesService.getLikedUsersIds(videoContent.get().getId(), ContentType.Video);

        videoContentResponse.setLikeList(likedUser);

        profileResponse.setProfileId(videoContent.get().getAuthor().getId());
        profileResponse.setFirstName(videoContent.get().getAuthor().getFirstName());
        profileResponse.setLastName(videoContent.get().getAuthor().getLastName());

        String avatarUrl = s3Service.generatePreSignedUrl(videoContent.get().getAuthor().getImageUrl(),new Date(System.currentTimeMillis()+1000*60*60*6)).toString();

        profileResponse.setAvatarUrl(avatarUrl);
        profileResponse.setUserName(videoContent.get().getAuthor().getUsername());

        videoContentResponse.setAuthor(profileResponse);

        return ResponseEntity.ok(new GetVideoContentDetailsByIdResponse("Done",videoContentResponse));
    }

    //
    public List<Integer> likeVideoContent(Integer uId,Integer vId){
        return likesService.toggleLike(uId,vId,ContentType.Video);
    }

    //
    public List<Integer> getLikedUserIdByContentId(Integer cId){
        Optional<VideoContent> content = getVideoContent(cId);

        if (content.isEmpty()){
            return new ArrayList<>();
        }
        return likesService.getLikedUsersIds(cId,ContentType.Video);
    }


}
