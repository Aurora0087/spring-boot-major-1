package com.ss.tst1.user;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
    //Query for finding 1 user by email
    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findByEmail(@Param("email") String email);

    @Query("SELECT u FROM User u WHERE u.userName = :userName")
    Optional<User> findByUserName(@Param("userName") String email);

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.locked = :locked, u.enabled = :enabled, u.role = :role WHERE u.id = :userId")
    void updateUserByAdmin(@Param("userId") Integer userId,@Param("locked") Boolean locked, @Param("enabled") Boolean enabled,@Param("role") Role role);


}