package com.ss.tst1.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepo extends JpaRepository<Order,Integer> {

    @Query("SELECT b FROM BoughtContent b " +
            "WHERE b.user.id = :userId AND b.content.id = :contentId")
    Optional<Order> findByUidAndContentId(Integer userId, Integer contentId);
}
