package com.ss.tst1.likes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LikesRepo extends JpaRepository<Likes,Integer> {

    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM Likes l " +
            "WHERE l.user.id = :userId AND l.contentId = :contentId AND l.contentType = :contentType")
    Boolean isLiked(Integer userId, Integer contentId, ContentType contentType);
}
