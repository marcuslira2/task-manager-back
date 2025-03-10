package com.task.manager.task.manager.backend.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.task.manager.task.manager.backend.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    public String generateToken(User user){
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("Task Manager API")
                    .withSubject(user.getUsername())
                    .withClaim("userId",user.getId())
                    .withExpiresAt(loginTimeExpiration())
                    .sign(algorithm);
        } catch (JWTCreationException exception){
            throw new JWTCreationException("Error: ",exception);
        }
    }

    public String getSubject(String tokenJWT){
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("Task Manager API")
                    .build()
                    .verify(tokenJWT)
                    .getSubject();
        }catch (JWTCreationException e){
            throw new JWTVerificationException("Token JWT expired");
        }

    }

    public Long getAuthenticatedUserId() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user.getId();
    }

    private Instant loginTimeExpiration(){
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}