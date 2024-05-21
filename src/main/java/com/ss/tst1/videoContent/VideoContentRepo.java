package com.ss.tst1.videoContent;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VideoContentRepo extends JpaRepository<VideoContent,Integer> {

    @Query("SELECT vc FROM VideoContent vc WHERE vc.category.id = :cid AND vc.isBlocked = false")
    Optional<VideoContent> getUnBanedVideoContent(@Param("cid") Integer cid);

    @Query("SELECT vc FROM VideoContent vc WHERE vc.isBlocked = false")
    List<VideoContent> findAllUnBanedVideoContent();

    @Query("SELECT vc FROM VideoContent vc WHERE vc.isBlocked = false AND vc.category.categoryName = :category")
    List<VideoContent> findAllUnBanedVideoContentWithSameCategory(String category);

    @Query("SELECT vc FROM VideoContent vc WHERE vc.author.id = :userId")
    List<VideoContent> findAllByUserId(Integer userId);

    @Query("SELECT vc FROM VideoContent vc WHERE vc.isBlocked = false AND vc.author.id = :userId")
    List<VideoContent> findAllUnBanedContentByUserId(Integer userId);

    @Query("SELECT vc FROM VideoContent vc WHERE vc.isBlocked = false AND (vc.title LIKE %:topic% OR vc.category.categoryName LIKE %:topic%)")
    List<VideoContent> findAllUnBanedVideoContentWithTopic(String topic);

    @Transactional
    @Modifying
    @Query("UPDATE VideoContent vc SET vc.title = :title, vc.description = :description, vc.price = :price WHERE vc.id = :contentId")
    void updateContent(Integer contentId,String title,String description,Float price);
}
