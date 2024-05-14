package com.ss.tst1.orders;

import com.ss.tst1.user.User;
import com.ss.tst1.videoContent.VideoContent;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Orders {

    @SequenceGenerator(
            name = "bought_content_squ",
            sequenceName = "bought_content_squ",
            initialValue = 10000,
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.AUTO,
            generator = "bought_content_squ"
    )
    @Id
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "boughtByuId")
    private User boughtBy;

    @ManyToOne
    @JoinColumn(name = "contentId")
    private VideoContent content;

    private String razorpayOrderId;
    private String razorpayPaymentId;
    private Boolean isPayed;
    private Date orderedAt;

    public Orders(User boughtBy, VideoContent content, String razorpayOrderId) {
        this.boughtBy = boughtBy;
        this.content = content;
        this.razorpayOrderId = razorpayOrderId;
        this.isPayed = false;
        this.orderedAt = new Date();
    }
}
