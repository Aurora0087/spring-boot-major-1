package com.ss.tst1.videoContent;

import com.ss.tst1.user.User;
import com.ss.tst1.videoContentCategory.Category;
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
public class VideoContent {

    @SequenceGenerator(
            name = "video_content_squ",
            sequenceName = "video_content_squ",
            initialValue = 1000,
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.AUTO,
            generator = "video_content_squ"
    )
    @Id
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "authorId")
    private User author;

    @ManyToOne
    @JoinColumn(name = "categoryId")
    private Category category;

    @Column(columnDefinition = "TEXT")
    private String title;
    @Column(columnDefinition = "TEXT")
    private String description;
    private Float price;
    private String imgUrl;
    private String videoUrl;
    private Date createdAt;
    private Boolean isBlocked;

    public VideoContent(User user,Category category, String title, String description, Float price, String imgUrl, String videoUrl) {
        this.author=user;
        this.category=category;
        this.title = title;
        this.description = description;
        this.price = price;
        this.imgUrl = imgUrl;
        this.videoUrl = videoUrl;
        this.createdAt = new Date();
        this.isBlocked = false;
    }
}
