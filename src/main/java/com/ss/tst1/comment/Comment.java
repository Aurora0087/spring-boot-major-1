package com.ss.tst1.comment;

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

    private String text;
}
