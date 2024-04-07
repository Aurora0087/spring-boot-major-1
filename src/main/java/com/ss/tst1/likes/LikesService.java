package com.ss.tst1.likes;

import com.ss.tst1.user.User;
import com.ss.tst1.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class LikesService {

    @Autowired
    private LikesRepo likesRepo;

    @Autowired
    private UserService userService;

    public List<Integer> toggleLike(Integer userId,Integer contentId,ContentType contentType){
        try{
            User user = userService.getUserById(userId)
                    .orElseThrow(()-> new IllegalArgumentException("User do not exist."));

            Optional<Likes> like = likesRepo.getLikeByUidCidType(user.getId(),contentId,contentType);

            if (like.isPresent()){

                likesRepo.deleteById(like.get().getId());

            }
            else {
                Likes newLike = new Likes(user,contentType,contentId);

                likesRepo.save(newLike);
            }

            return getLikedUsersIds(contentId,contentType);

        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Integer> getLikedUsersIds(Integer cId,ContentType contentType){
        try{
            return likesRepo.getLikesOnContent(cId,contentType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
