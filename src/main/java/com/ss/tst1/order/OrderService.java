package com.ss.tst1.order;

import com.ss.tst1.user.User;
import com.ss.tst1.user.UserService;
import com.ss.tst1.videoContent.VideoContent;
import com.ss.tst1.videoContent.VideoContentService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class OrderService {

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private UserService userService;

    @Autowired
    private VideoContentService videoContentService;

    public Boolean isBought(Integer uid,Integer contentId){
        return orderRepo.findByUidAndContentId(uid,contentId).isPresent();
    }

    public OrderResponse makeOrder(Integer uid, Integer contentId, String razorOrderId){

        Optional<User> user =userService.getUserById(uid);
        Optional<VideoContent> content = videoContentService.getVideoContent(contentId);

        if (user.isEmpty()){
            return new OrderResponse("User do not exist.", OrderStatus.failed);
        }
        if (content.isEmpty()){
            return new OrderResponse("Content do not exist.", OrderStatus.failed);
        }

        orderRepo.save(new Order(user.get(),content.get(),razorOrderId));


        return new OrderResponse("Done", OrderStatus.success);
    }
}
