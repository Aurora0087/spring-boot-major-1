package com.ss.tst1.comment;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepo extends JpaRepository<Comment,Integer> {

    @Query("SELECT c FROM Comment c WHERE c.parentId = :parentId AND c.parentType = :parentType")
    List<Comment> commentsWithSameParent(Integer parentId,CommentParentType parentType);

    @Query("SELECT c FROM Comment c WHERE c.author.id = :uid")
    List<Comment> commentsPostBySameUser(Integer uid);

    @Transactional
    @Modifying
    @Query("UPDATE Comment c SET c.text = :text WHERE c.id = :commentId")
    Comment updateComment(Integer commentId,String text);

    @Transactional
    @Modifying
    @Query("UPDATE Comment c SET c.isPrivate = :isPrivateValue WHERE c.id = :commentId")
    Comment privateComment(Integer commentId,Boolean isPrivateValue);
}
