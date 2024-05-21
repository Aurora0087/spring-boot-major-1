package com.ss.tst1.cart;

import com.razorpay.RazorpayException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/cart/add/{contentId}")
    public ResponseEntity<?> addToCart(
            @PathVariable String contentId,
            @CookieValue(name = "token")String token
    ){
        return cartService.addToCart(token,contentId);
    }

    @GetMapping("/cart/get")
    public ResponseEntity<List<CartResponseUser>> getUsersCartItem(
            @CookieValue(name = "token")String token
    ){
        return cartService.getUsersCartItem(token);
    }

    @DeleteMapping("/cart/delete/{itemId}")
    public ResponseEntity<?> deleteItem(
            @PathVariable String itemId,
            @CookieValue(name = "token")String token
    ){
        return cartService.deleteCartItem(token,itemId);
    }

    @PostMapping("/cart/order")
    public ResponseEntity<?> makeOrderFromCart(
            @CookieValue(name = "token")String token
    ) throws RazorpayException {
        return cartService.makeOrderFromCart(token);
    }
}
