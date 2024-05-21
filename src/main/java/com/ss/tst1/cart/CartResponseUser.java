package com.ss.tst1.cart;

import lombok.Data;

@Data
public class CartResponseUser {
    private Integer itemId;
    private Integer contentId;
    private String price;
    private String contentTitle;
}
