package com.ss.tst1.comment;

import com.ss.tst1.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Comment {

    @SequenceGenerator(
            name = "comment_squ",
            sequenceName = "comment_squ",
            initialValue = 100,
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.AUTO,
            generator = "comment_squ"
    )
    @Id
    private Integer id;

    private Integer parentId;

    @ManyToOne
    @JoinColumn(name = "authorId")
    private User author;

    private String text;

    private Date createdAt;

    @Enumerated(EnumType.STRING)
    private CommentParentType parentType;

    private Boolean isPrivate;

    public Comment(Integer parentId, String text,User author, CommentParentType parentType) {
        this.parentId = parentId;
        this.text = text;
        this.author = author;
        this.createdAt = new Date();
        this.parentType = parentType;
        this.isPrivate = false;
    }
}
