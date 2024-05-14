package com.ss.tst1.orders;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderResponse {
    private String message;
    private String status;
    private String razorpayOrderId;
    private String key;
    private String currency = "INR";
    private String amount;
    private String notes;
    private String description;
    private String name;
    private String email;

    public OrderResponse(String message, String status) {
        this.message = message;
        this.status = status;
    }
}
