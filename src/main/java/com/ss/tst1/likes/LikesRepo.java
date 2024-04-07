package com.ss.tst1.likes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikesRepo extends JpaRepository<Likes,Integer> {

    @Query("SELECT l FROM Likes l " +
            "WHERE l.user.id = :userId AND l.contentId = :contentId AND l.contentType = :contentType")
    Optional<Likes> getLikeByUidCidType(Integer userId, Integer contentId, ContentType contentType);



    @Query("SELECT l.user.id FROM Likes l WHERE l.contentType = :contentType AND l.contentId = :cId")
    List<Integer> getLikesOnContent(Integer cId,ContentType contentType);
}
