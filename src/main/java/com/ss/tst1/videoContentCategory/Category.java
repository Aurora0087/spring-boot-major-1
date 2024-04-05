package com.ss.tst1.videoContentCategory;

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
public class Category {


    @SequenceGenerator(
            name = "category_seq",
            sequenceName = "category_seq"
    )
    @GeneratedValue(
            strategy = GenerationType.AUTO,
            generator = "category_seq"
    )
    @Id
    private Integer id;
    private String categoryName;

    public Category(String categoryName) {
        this.categoryName = categoryName;
    }
}
