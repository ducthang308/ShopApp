package com.example.ShopApp.Components;

import com.example.ShopApp.Exception.InvalidParamException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.security.SecureRandom;
import java.util.*;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtTokenUtils {
    @Value("${jwt.expiration}")
    private int expiration; //Lưu vào biến môi trường trong yml

    @Value("${jwt.secretKey}")
    private String secretKey;

    //HS256 (HMAC with SHA-256) là một thuật toán
    // ký số thường được sử dụng để tạo chữ ký số cho JSON Web Tokens (JWTs).
    // Là một thuật toán nhanh hiệu quả, xử lí nhiều token ngắn hạn
    //Private claims
    public String generateToken(com.example.ShopApp.Models.User user) throws Exception {
        Map<String, Object> claims = new HashMap<>();
        claims.put("phone", user.getPhone());
        try {
            String token = Jwts.builder()
                    .setClaims(claims)
                    .setSubject(user.getPhone())
                    .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000L))
                    .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                    .compact();
            return token;
        } catch (Exception e) {
            throw new InvalidParamException("Cannot create jwt token, error: " + e.getMessage());
        }
    }

    private Key getSignInKey(){
        byte[] bytes = Decoders.BASE64.decode(secretKey); //Decoders.BASE64.decode("wcxF3OYZq/lkFj7zEKyhpJbMwf1BaVGDTninJkxIpjU=")
        return Keys.hmacShaKeyFor(bytes);
    }

    private String generateSecretKey(){
        SecureRandom random = new SecureRandom();
        byte[] keyBytes = new byte[32];
        random.nextBytes(keyBytes);
        String secretKey = Encoders.BASE64.encode(keyBytes);
        return secretKey;
    }

    private Claims extractAllClaims(String token){
        return Jwts.parser()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    //trích xuất một giá trị cụ thể từ các claims trong JWT
    public <T> T extractClaim(String token, Function<Claims, T> claimResolver){
        final Claims claims = this.extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    //check expiration
    public boolean isTokenExpired(String token){
        Date expirationDate = this.extractClaim(token, Claims::getExpiration);
        return expirationDate.before(new Date());
    }

    public String extractPhone(String token){
        return extractClaim(token, Claims::getSubject);
    }

    public boolean validateToken(String token, UserDetails userDetails){
        String phone = extractPhone(token);
        return (phone.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }
}
