package com.ss.tst1.cart;

import com.razorpay.Order;
import com.razorpay.RazorpayException;
import com.ss.tst1.DefResponse;
import com.ss.tst1.jwt.JwtService;
import com.ss.tst1.orders.OrderResponse;
import com.ss.tst1.orders.Orders;
import com.ss.tst1.orders.OrdersRepo;
import com.ss.tst1.orders.OrdersService;
import com.ss.tst1.razeorPay.RazorPayment;
import com.ss.tst1.user.User;
import com.ss.tst1.user.UserService;
import com.ss.tst1.videoContent.VideoContent;
import com.ss.tst1.videoContent.VideoContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class CartService {


    @Value("${razorpay.key.id}")
    private String keyId;

    @Autowired
    private CartRepo cartRepo;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @Autowired
    private VideoContentService contentService;

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private RazorPayment razorPayment;

    public ResponseEntity<?> addToCart(String token,String contentId){

        String userEmail = jwtService.extractUsername(token);

        Optional<User> user = userService.getUserByEmailId(userEmail);

        if (user.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new DefResponse("User do not exist."));

        if (!contentId.matches(".*\\d.*")){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new DefResponse("Property not given properly."));
        }

        Optional<VideoContent> content = contentService.getVideoContent(Integer.valueOf(contentId));

        if (content.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new DefResponse("Content id " + contentId + " do not exist."));

        Boolean isBought = ordersService.isBought(user.get().getId(),content.get().getId());

        if (isBought) return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new DefResponse("Content already Bought."));

        Optional<Cart> cartItem = cartRepo.findAllByUserIdAndContentId(user.get().getId(),content.get().getId());

        if (cartItem.isPresent()) return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new DefResponse("Content id " + contentId + " already added to user cart."));

        cartRepo.save(new Cart(user.get(),content.get()));

        return ResponseEntity.ok(new DefResponse("Done"));
    }

    public ResponseEntity<List<CartResponseUser>> getUsersCartItem(String token){

        String userEmail = jwtService.extractUsername(token);

        Optional<User> currentUser = userService.getUserByEmailId(userEmail);

        List<Cart> cartItems = cartRepo.findAllByUserId(currentUser.get().getId());
        List<CartResponseUser> responseList = new ArrayList<>();

        for (Cart cartItem:cartItems){

            CartResponseUser response = new CartResponseUser();

            response.setItemId(cartItem.getId());
            response.setContentId(cartItem.getContent().getId());
            response.setContentTitle(cartItem.getContent().getTitle());
            response.setPrice(cartItem.getContent().getPrice().toString());

            responseList.add(response);
        }

        return ResponseEntity.ok(responseList);
    }

    public ResponseEntity<?> deleteCartItem(String token,String itemId){

        String userEmail = jwtService.extractUsername(token);

        Optional<User> currentUser = userService.getUserByEmailId(userEmail);

        if (!itemId.matches(".*\\d.*")){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new DefResponse("Property not given properly."));
        }

        Optional<Cart> cartItem = cartRepo.findById(Integer.valueOf(itemId));

        if (cartItem.isEmpty())  return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new DefResponse("Item id " + itemId+ " do not exist."));

        if (!Objects.equals(cartItem.get().getUser().getId(), currentUser.get().getId())) return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new DefResponse("You don not have authority to delete this."));

        cartRepo.deleteById(Integer.valueOf(itemId));

        return ResponseEntity.ok(new DefResponse("Done"));
    }

    public ResponseEntity<?> makeOrderFromCart(String token) throws RazorpayException {
        String userEmail = jwtService.extractUsername(token);

        Optional<User> user = userService.getUserByEmailId(userEmail);

        if (user.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new DefResponse("User do not exist."));

        List<Cart> cartItems = cartRepo.findAllByUserId(user.get().getId());

        if(cartItems.isEmpty()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new DefResponse("Cart is empty."));

        Float totalPrice = 0F;
        StringBuilder note = new StringBuilder("Multiple bought: ");
        List<Integer> orderIds = new ArrayList<>();

        for (Cart cartItem :cartItems){
            if (ordersService.isBought(user.get().getId(),cartItem.getContent().getId())) continue;

            totalPrice += cartItem.getContent().getPrice();
            note.append(" | ").append(cartItem.getId().toString());

            Orders  order = ordersService.saveOrder(user.get(),cartItem.getContent(),null);
            orderIds.add(order.getId());

            cartRepo.deleteById(cartItem.getId());
        }

        Order razorPayOrder =  razorPayment.makeOrder(totalPrice,note.toString());

        for (Integer orderId:orderIds){
            Optional<Orders> order = ordersService.findOrderById(orderId);

            if (order.isEmpty()) continue;

            order.get().setRazorpayOrderId(razorPayOrder.get("id"));
            ordersService.saveOrder(order.get());
        }

        OrderResponse response = new OrderResponse();

        response.setKey(keyId);
        response.setName(user.get().getFirstName() + " "+ user.get().getLastName());
        response.setEmail(user.get().getEmail());
        response.setMessage("success");
        response.setStatus(razorPayOrder.get("status"));
        response.setRazorpayOrderId(razorPayOrder.get("id"));
        response.setAmount(razorPayOrder.get("amount").toString());
        response.setNotes(note.toString());

        return ResponseEntity.ok(response);
    }
}
