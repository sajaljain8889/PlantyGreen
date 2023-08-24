package com.ecom.plantygreen.config;

import com.ecom.plantygreen.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.hibernate.usertype.UserType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.crypto.SecretKey;
import java.util.Date;

@Configuration
public class JwtConfig {

    @Value("${myapp.jwt.secret-key}")
    private String jwtSecretKey;

    @Value("${myapp.jwt.expiration-time}")
    private Long jwtExpirationTime;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecretKey secretKey() {
        // Generate a secure key for HS512 algorithm if the provided key size is not secure enough
        if (jwtSecretKey.length() * 8 < 512) {
            return Keys.secretKeyFor(SignatureAlgorithm.HS512);
        }
        return Keys.hmacShaKeyFor(jwtSecretKey.getBytes());
    }


    public String generateToken(User user, UserType userType) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("userType", userType.toString())
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + jwtExpirationTime))
                .signWith(SignatureAlgorithm.HS512, jwtSecretKey)
                .compact();

    }

//    public Authentication getAuthentication(String token, User userDetails) {
//        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
//    }

    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }


    public String getUserTypeFromToken(String token) {
        String userTypeStr = Jwts.parser().setSigningKey(jwtSecretKey).parseClaimsJws(token).getBody().get("userType", String.class);

        return userTypeStr;

    }
    public boolean validateToken(String token, User userDetails) {
        final String username = extractUsername(token);
        final UserType userType = getUserTypeFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private Claims extractClaims(String token) {
        return Jwts.parser().setSigningKey(jwtSecretKey).parseClaimsJws(token).getBody();
    }

    private boolean isTokenExpired(String token) {
        final Date expiration = extractClaims(token).getExpiration();
        return expiration.before(new Date());
    }
}
