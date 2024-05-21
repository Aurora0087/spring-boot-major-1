package com.ss.tst1.orders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.razorpay.RazorpayException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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

    @PostMapping("/webhook/paymentSuccess")
    public ResponseEntity<?> afterSuccessPayment(
            @RequestBody Map<String, ?> data,
            @RequestHeader("X-Razorpay-Signature") String signature
            ) throws RazorpayException, JsonProcessingException {

        /*{
        entity=event,
         account_id=acc_Nh2x7XYtuA353S,
          event=payment.captured,
           contains=[payment],
            payload={
            payment={
            entity={
            id=pay_O9tbDpMvsmMQSz,
             entity=payment,
              amount=5310,
               currency=INR,
                status=captured,
                 order_id=order_O9taXnI8iEgCZw,
                  invoice_id=null,
                   international=false,
                    method=card,
                     amount_refunded=0,
                      refund_status=null,
                       captured=true,
                        description=Test Transaction,
                         card_id=card_O9tbE2qM0rWorB,
                          card={id=card_O9tbE2qM0rWorB,
                           entity=card,
                            name=,
                             last4=1111,
                              network=Visa,
                               type=debit,
                                issuer=null,
                                 international=false,
                                  emi=false,
                                   sub_type=consumer,
                                    token_iin=null},
                                     bank=null,
                                      wallet=null,
                                       vpa=null,
                                        email=debrajbanshi1@gmail.com, contact=+919330062616,
                                         notes={notes_key_1=Demo Content, address=Demo Content},
                                          fee=47, tax=0, error_code=null,
                                           error_description=null,
                                            error_source=null,
                                             error_step=null,
                                              error_reason=null,
                                               acquirer_data={auth_code=834352},
                                                created_at=1715585341,
                                                 reward=null,
                                                  base_amount=5310
                                                  }
                                                  }
                                                  },
             created_at=1715585350}

* */
        return ordersService.handleWebhookAfterPayment(data,signature);
    }

    @GetMapping("/orders/success/{uid}")
    public ResponseEntity<?> findAllBoughtByUser(
            @PathVariable String uid,
            @CookieValue(name = "token")String token
    ){
        return ordersService.findAllSuccessPayment(uid,token);
    }
}
