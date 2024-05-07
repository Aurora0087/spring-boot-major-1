package com.ss.tst1.orders;

import com.razorpay.Order;
import com.razorpay.RazorpayException;
import com.ss.tst1.razeorPay.RazorPayment;
import com.ss.tst1.user.User;
import com.ss.tst1.user.UserService;
import com.ss.tst1.videoContent.VideoContent;
import com.ss.tst1.videoContent.VideoContentService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class OrdersService {

    @Autowired
    private OrdersRepo ordersRepo;

    @Autowired
    private UserService userService;

    @Autowired
    private VideoContentService videoContentService;

    @Autowired
    private RazorPayment razorPayment;

    public Boolean isBought(Integer uid,Integer contentId){
        return ordersRepo.findByUidAndContentId(uid,contentId).isPresent();
    }

    public ResponseEntity<OrderResponse> makeOrder(String uid, String contentId) throws RazorpayException {

        if (!uid.matches(".*\\d.*") || !contentId.matches(".*\\d.*")){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new OrderResponse("Give proper properties.", OrderStatus.failed));
        }

        Optional<User> user =userService.getUserById(Integer.valueOf(uid));
        Optional<VideoContent> content = videoContentService.getVideoContent(Integer.valueOf(contentId));

        if (user.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new OrderResponse("User do not exist.", OrderStatus.failed));
        }
        if (content.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body( new OrderResponse("Content do not exist.", OrderStatus.failed));
        }

        Order order =  razorPayment.makeOrder(content.get().getPrice(),content.get().getTitle());

        //System.out.println(order.toString());

        ordersRepo.save(new Orders(user.get(),content.get(),order.get("id")));


        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new OrderResponse("Done", OrderStatus.success,order.get("id")));
    }
}
