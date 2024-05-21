package com.ss.tst1.cart;

import com.ss.tst1.orders.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepo extends JpaRepository<Cart,Integer> {

    @Query("SELECT c FROM Cart c WHERE c.user.id = :userId")
    List<Cart> findAllByUserId(Integer userId);

    @Query("SELECT c FROM Cart c WHERE c.user.id = :userId AND c.content.id = :contentId")
    Optional<Cart> findAllByUserIdAndContentId(Integer userId, Integer contentId);
}
