package com.example.wasla.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Service
@Getter
@RequiredArgsConstructor
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private Integer expiration;


    // ============ generate secret key ===============

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }


    // ============ generate Token ============

    public String generateToken(UserDetails userDetails) {

        Map<String, Object> claims = new HashMap<>();

        claims.put("ROLE", userDetails.getAuthorities());

        return doGenerateToken(claims, userDetails.getUsername());
    }

    private String doGenerateToken(Map<String, Object> claims, String username) {

        return Jwts.builder()
                .signWith(getSigningKey())
                .subject(username)
                .claims(claims)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .compact();

    }

//    public String refreshToken(String username) {
//
//        return Jwts.builder()
//                .signWith(getSigningKey())
//                .subject(username)
//                .issuedAt(new Date(System.currentTimeMillis()))
//                .expiration(new Date(System.currentTimeMillis() + refresh))
//                .compact();
//    }


    // ========== extract token ===================

    public Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

    }


    public <T> T extractClaim(String token, Function<Claims, T> claimsTFunction) {

        Claims claims = extractAllClaims(token);
        return claimsTFunction.apply(claims);
    }


    public String extractUsername(String token) {

        var username = extractClaim(token, Claims::getSubject);

        return username;
    }


    // ============== validate token ===========

    public boolean validateToken(String token) {

        log.info("received token is : {}", token);

        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);

            return true;
        } catch (JwtException e) {
            log.error("exception occurred while verify token {}", e);
            return false;
        }
    }

    public boolean validateToken(String token, UserDetails userDetails) {

        var username = extractUsername(token);

        return username.equals(userDetails.getUsername()) &&!isTokenExpire(token);
    }

    private boolean isTokenExpire(String token) {

        return extractClaim(token, Claims::getExpiration).before(new Date(System.currentTimeMillis()));
    }


}