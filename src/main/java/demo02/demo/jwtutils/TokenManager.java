package demo02.demo.jwtutils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import io.jsonwebtoken.Jwts;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;



@Component
public class TokenManager {
    private static final long serialVersionUID = 123456L;
    public static final long TOKEN_VALIDITY = 10*60; // 10 minutes

    @Value("${secret}")
    private String jwtSecret; // get value from application.property

    // generate Jwt token containing username from userDetails, stored in payload
    // combining with header and signature hashed using HS256 algo
    public String generateJwtToken(UserDetails userDetails){
        Map<String, Object> claims = new HashMap<>();
        return Jwts
                .builder()
                .setClaims(claims) // reset claims to an empty hashmap
                .setSubject(userDetails.getUsername()) // manually set each claim
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_VALIDITY * 1000))
                .signWith(getKey(), SignatureAlgorithm.HS256) // specify the algorithm to sign the jwt using specific key
                .compact();
    }

    public Boolean validateJwtToken(String token, UserDetails userDetails){
        final String username = getUsernameFromToken(token);
        final Claims claims = Jwts
                .parser() // initiate parser
                .setSigningKey(getKey()) // set the key for the parser
                .build() // build the will-be-immutable parser
                .parseClaimsJws(token).getBody(); // use parser to decrypt token to get claim in the payload
        // check the expiration state of token (redundant? because parser already check and throw exceptions if token expired)
        Boolean isTokenExpired = claims.getExpiration().before(new Date());
        // TRUE if token got matching username
        return (username.equals(userDetails.getUsername())) && !isTokenExpired;
    }

    public String getUsernameFromToken(String token) {
        final Claims claims = Jwts
                .parser()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    private Key getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        Key key = Keys.hmacShaKeyFor(keyBytes); // generate symmetrical key
        return key;
    }
}
