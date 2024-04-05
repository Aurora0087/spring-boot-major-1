package com.ss.tst1.likes;

import com.ss.tst1.comment.Comment;
import com.ss.tst1.user.User;
import com.ss.tst1.videoContent.VideoContent;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Likes {


    @SequenceGenerator(
            name = "like_squ",
            sequenceName = "like_squ",
            initialValue = 100,
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.AUTO,
            generator = "like_squ"
    )
    @Id
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @Enumerated(EnumType.STRING)
    private ContentType contentType;

    private Integer contentId;

    public Likes(User user, ContentType contentType, Integer contentId) {
        this.user = user;
        this.contentType = contentType;
        this.contentId = contentId;
    }
}
