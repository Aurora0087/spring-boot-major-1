package com.ss.tst1.orders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.razorpay.Order;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import com.ss.tst1.jwt.JwtService;
import com.ss.tst1.razeorPay.RazorPayment;
import com.ss.tst1.user.Role;
import com.ss.tst1.user.User;
import com.ss.tst1.user.UserService;
import com.ss.tst1.videoContent.VideoContent;
import com.ss.tst1.videoContent.VideoContentService;
import lombok.AllArgsConstructor;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class OrdersService {

    @Value("${razorpay.key.id}")
    private String keyId;

    @Autowired
    private OrdersRepo ordersRepo;

    @Autowired
    private UserService userService;

    @Autowired
    private VideoContentService videoContentService;

    @Autowired
    private JwtService jwtService;

    private static final String WEBHOOK_SECRET = "aurora";

    @Autowired
    private RazorPayment razorPayment;

    public Boolean isBought(Integer uid,Integer contentId){
        return ordersRepo.findByUidAndContentId(uid,contentId).isPresent();
    }

    public ResponseEntity<OrderResponse> makeOrder(String uid, String contentId) throws RazorpayException {

        if (!uid.matches(".*\\d.*") || !contentId.matches(".*\\d.*")){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new OrderResponse("Give proper properties.", "failed"));
        }

        Optional<User> user =userService.getUserById(Integer.valueOf(uid));
        Optional<VideoContent> content = videoContentService.getVideoContent(Integer.valueOf(contentId));

        if (user.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new OrderResponse("User do not exist.", "failed"));
        }
        if (content.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body( new OrderResponse("Content do not exist.", "failed"));
        }

        Order order =  razorPayment.makeOrder(content.get().getPrice(),content.get().getTitle());
        /*
        * {"amount":5310,"amount_paid":0,"notes":{"notes_key_1":"Demo Content"},"created_at":1715412858,"amount_due":5310,"currency":"INR","receipt":"receipt#1","id":"order_O96cYqcMqZZvnb","entity":"order","offer_id":null,"status":"created","attempts":0}
        *
        * */
        ordersRepo.save(new Orders(user.get(),content.get(),order.get("id")));

        OrderResponse response = new OrderResponse();

        response.setKey(keyId);
        response.setName(user.get().getFirstName() + " "+ user.get().getLastName());
        response.setEmail(user.get().getEmail());
        response.setMessage("success");
        response.setStatus(order.get("status"));
        response.setRazorpayOrderId(order.get("id"));
        response.setAmount(order.get("amount").toString());
        response.setNotes(content.get().getTitle());

        return ResponseEntity.ok(response);
    }



    public ResponseEntity<?> handleWebhookAfterPayment(Map<String, ?> payLoad,String signature) throws RazorpayException, JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        String paymentId = null;
        String orderId = null;

        String payLoadJson = objectMapper.writeValueAsString(payLoad);

        if (Utils.verifyWebhookSignature(payLoadJson,signature,WEBHOOK_SECRET)){

            Map<String, ?> payload = (Map<String, ?>) payLoad.get("payload");
            if (payload != null) {
                Map<String, ?> payment = (Map<String, ?>) payload.get("payment");
                if (payment != null) {
                    Map<String, ?> entity = (Map<String, ?>) payment.get("entity");
                    if (entity != null) {
                       paymentId = (String) entity.get("id");
                       orderId= (String) entity.get("order_id");
                    }
                }
            }

            Optional<Orders> order = ordersRepo.findByRazorpayOrderId(orderId);

            if (order.isPresent()){
                order.get().setIsPayed(true);
                order.get().setRazorpayPaymentId(paymentId);
                ordersRepo.save(order.get());
            }
            System.out.println("working");
            return ResponseEntity.ok("ok");
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not allowed.");
    }

    public ResponseEntity<?> findAllSuccessPayment(String userId,String token){
        if (!userId.matches(".*\\d.*")){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Parameter not given properly.");
        }

        String userEmail = jwtService.extractUsername(token);
        Optional<User> currentUser = userService.getUserByEmailId(userEmail);

        if (currentUser.get().getRole().toString().equalsIgnoreCase(Role.ADMIN.toString()) || Integer.valueOf(userId).equals(currentUser.get().getId())){
            return ResponseEntity.ok(ordersRepo.findAllBoughtByUser(Integer.valueOf(userId)));
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You don't have Authority.");
    }
}
