package com.ss.tst1.orders;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrdersRepo extends JpaRepository<Orders,Integer> {

    @Query("SELECT o FROM Orders o " +
            "WHERE o.boughtBy.id = :userId AND o.content.id = :contentId AND o.isPayed = true")
    Optional<Orders> findByUidAndContentId(Integer userId, Integer contentId);

    @Query("SELECT o FROM Orders o WHERE o.razorpayOrderId = :id")
    List<Orders> findByRazorpayOrderId(String id);

    @Query("SELECT o FROM Orders o WHERE o.boughtBy.id = :userId AND o.isPayed = true")
    List<Orders> findAllBoughtByUser(Integer userId);
}
