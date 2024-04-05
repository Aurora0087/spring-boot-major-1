package com.ss.tst1.filter;

import com.ss.tst1.jwt.JwtService;
import com.ss.tst1.user.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

@Component
public class JwtAuthFillter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token =null;
        String userName =null;

        Cookie[] cookies = request.getCookies();

        if (!Objects.equals(request.getServletPath(), "/register") && !Objects.equals(request.getServletPath(), "/login")){
            if (cookies!=null){
                for (Cookie cookie:cookies){
                    if (Objects.equals(cookie.getName(), "token")){
                        token = cookie.getValue();
                        userName = jwtService.extractUsername(token);
                        break;
                    }
                }
            }

            if (userName!=null && SecurityContextHolder.getContext().getAuthentication()==null){

                UserDetails user = userService.loadUserByUsername(userName);
                if (jwtService.validateToken(token,user)){
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user,null,user.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource()
                            .buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }

        filterChain.doFilter(request,response);
    }
}
