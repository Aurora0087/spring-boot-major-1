package com.ss.tst1.orders;

import com.razorpay.RazorpayException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

@RestController
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    @PostMapping("/order/{vcid}")
    public ResponseEntity<OrderResponse> makeOrder(
            @PathVariable String vcid,
            @CookieValue(name = "uuid")String uid

    ) throws RazorpayException {
        return ordersService.makeOrder(uid,vcid);
    }

    public void afterSuccessPayment(
            @RequestBody Map<String, Objects> data
            ){

        System.out.println(data.toString());
        return;
    }
}
