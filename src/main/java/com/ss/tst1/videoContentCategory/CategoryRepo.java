package com.ss.tst1.videoContentCategory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepo extends JpaRepository<Category,Integer> {

   @Query("SELECT c FROM Category c WHERE c.categoryName = :categoryName")
   Optional<Category> findByCategoryName(String categoryName);

}
