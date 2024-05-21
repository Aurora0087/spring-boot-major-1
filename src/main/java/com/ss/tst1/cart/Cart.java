package com.ss.tst1.cart;

import com.ss.tst1.user.User;
import com.ss.tst1.videoContent.VideoContent;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Cart {

    @SequenceGenerator(
            name = "cart_squ",
            sequenceName = "cart_squ",
            initialValue = 10000,
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.AUTO,
            generator = "cart_squ"
    )
    @Id
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne
    @JoinColumn(name = "contentId")
    private VideoContent content;
    private Date addedAt;

    public Cart(User user, VideoContent content) {
        this.user = user;
        this.content = content;
        this.addedAt=new Date();
    }
}
