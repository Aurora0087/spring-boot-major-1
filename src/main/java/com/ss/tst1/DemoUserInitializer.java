package com.ss.tst1;

import com.ss.tst1.likes.LikesService;
import com.ss.tst1.user.Role;
import com.ss.tst1.user.User;
import com.ss.tst1.user.UserService;
import com.ss.tst1.videoContent.VideoContentService;
import com.ss.tst1.videoContentCategory.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DemoUserInitializer implements ApplicationRunner {

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private VideoContentService videoContentService;

    @Autowired
    private LikesService likesService;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        // demo admin
        User demoUser = new User("deb","r","debrajbanshi1@gmail.com","123", Role.ADMIN);
        userService.signUpUser(demoUser);

        demoUser = new User("arpan","m.","arpan@gmail.com","1234",Role.ADMIN);
        userService.signUpUser(demoUser);

        //demo users
        demoUser = new User("demo","user","demo@mail.com","456",Role.USER);
        userService.signUpUser(demoUser);

        // demo category
        categoryService.createCategory("music");
        categoryService.createCategory("animation");

        videoContentService.createVideoContent(1000,1,"Demo Content","Instead of testing SQL injection attacks on your live application, you should focus on implementing security best practices to prevent SQL injection vulnerabilities. This includes using parameterized queries or prepared statements, input validation and sanitization, least privilege principle, and secure coding practices.",53.1F,"images/09499d40-478f-4019-8847-298c5a7910c3pexels-wendy-wei-1864637.jpg","videos/0a878ea0-023c-4254-9702-5f6d64c9c9e7pexels-tom-fisk-20317587 (1080p).mp4");

        videoContentService.likeVideoContent(1002,1000);
    }
}
