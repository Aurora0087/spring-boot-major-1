package com.ss.tst1.orders;

import lombok.Data;

import java.util.Date;

@Data
public class SuccessPaymentResponse {
    private Integer contentId;
    private Date createdAt;
    private String paymentId;
    private Integer orderId;
}
