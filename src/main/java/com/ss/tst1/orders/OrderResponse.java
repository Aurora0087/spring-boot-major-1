package com.ss.tst1.orders;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
    private String message;
    private OrderStatus status;
    private String razorpayOrderId;

    public OrderResponse(String message, OrderStatus status) {
        this.message = message;
        this.status = status;
        this.razorpayOrderId = "";
    }
}
