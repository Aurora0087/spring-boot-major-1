package com.ss.tst1;

import com.ss.tst1.user.Role;
import com.ss.tst1.user.User;
import com.ss.tst1.user.UserService;
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

    @Override
    public void run(ApplicationArguments args) throws Exception {

        // demo admin
        User demoUser = new User("deb","r","debrajbanshi1@gmail.com","auroradev","123", Role.ADMIN);
        userService.signUpUser(demoUser);

        // demo category
        categoryService.createCategory("music");

    }
}
