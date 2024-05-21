package com.ss.tst1.jwt;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Service
public class JwtService {

    private static final String SECRET_KEY = "d5b97baae28d8658051ba60926b8d1bcc11a3faf7cb8a95666d27854dc03be77";

    private Key getSignKey() {
        byte[] keyBytes= SECRET_KEY.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String sub){
        return createToken(new HashMap<>(),sub);
    }
    private String createToken(Map<String, Objects> claim,String sub){
        return Jwts.builder()
                .setClaims(claim)
                .setSubject(sub)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+1000*60*60*24*2))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    //verification
    public Boolean validateToken(String token, UserDetails userDetails){
        String username= extractUsername(token);
        return (username.equals(userDetails.getUsername())&& !isTokenExpired(token));
    }

    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }
    public Date extractTokenExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }

    public Boolean isTokenExpired(String token){
        return extractTokenExpiration(token).before(new Date());
    }


    public <T> T extractClaim(String token, Function<Claims,T> claimsResolver){
        final Claims claims = extractAllClaim(token);
        return claimsResolver.apply(claims);
    }
    private Claims extractAllClaim(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
