package com.example.fintech.service;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Jwt Service
 *
 * Responsible for handling all JWT-related operations
 *
 * 1. Generate JWT tokens during authentication
 * 2. Extract information (claims) from tokens
 * 3. Validate tokens for incoming requests that require them
 *
 * Note:
 * JWT Tokens are used to authenticate users in a stateless way
 * Instead of storing sessions on the server, client sends JWT token
 * with every request that requires one
 */
@Service
public class JwtService {


    @Value("${jwt.secret}")
    private String secret;                                      // Used to sign and verify JWT Tokens

    @Value("${jwt.expiration}")
    private Long expiration;                                    // Token expiration time

    /**
     * Method taht generates JWT token for a given user
     * @param userDetails
     * @return String
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * Method responsible to builds the actual token
     * @param claims
     * @param subject
     * @return String
     */
    public String createToken(Map<String, Object> claims, String subject ) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Method responsible to validate the JWT token
     *
     * 1. Check username inside the token mathes the user
     * 2. Token is not expired yet
     * @param token
     * @param userDetails
     * @return Boolean
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUserName(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Extracts the username (subject) from the token
     * @param token
     * @return String
     */
    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }


    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extraAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Generic method to extract any claim from the token
     * @param token
     * @return Claims
     */
    private Claims extraAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSignInKey()).
                build().
                parseClaimsJws(token).
                getBody();
    }

    /**
     * Check if token is expired
     * @param token
     * @return Boolean
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Gets the expiration date for the provided token
     * @param token
     * @return Date
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Generates the signing key used to sign and verify tokens
     * @return Key
     */
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return  Keys.hmacShaKeyFor(keyBytes);
    }

}

