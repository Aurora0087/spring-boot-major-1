package com.ss.tst1.videoContent;

import org.springframework.data.jpa.repository.JpaRepository;
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
}
