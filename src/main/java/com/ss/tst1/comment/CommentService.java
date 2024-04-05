package com.ss.tst1.comment;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class CommentService {

    @Autowired
    private CommentRepo commentRepo;
    public Optional<Comment> getComment(Integer commentId) {
        return commentRepo.findById(commentId);
    }
}
