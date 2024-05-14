package com.ss.tst1.security;


import com.ss.tst1.filter.JwtAuthFillter;
import com.ss.tst1.user.Role;
import com.ss.tst1.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Date;

@Configuration
@AllArgsConstructor
@EnableWebSecurity
public class WebSecurityConfig {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserService userService;
    @Autowired
    private JwtAuthFillter jwtAuthFillter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{

        System.out.println(new Date());
         http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth->auth
                        .requestMatchers(
                                "/",
                                "/error*/**",
                                "/login*/**",
                                "/logout*/**",
                                "/register*/**",
                                "/webhook*/**",
                                "/get/videocontent*/**"
                        )
                        .permitAll()
                        .requestMatchers("/lol*/**")
                        .hasAuthority("USER")
                        .requestMatchers("/admin*/**")
                        .hasAuthority("ADMIN")
                        .anyRequest()
                        .authenticated()
                )
                .sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFillter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider= new DaoAuthenticationProvider();
        provider.setPasswordEncoder(bCryptPasswordEncoder);
        provider.setUserDetailsService(userService);
        return provider;
    }
}
