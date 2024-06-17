package fpt.aptech.project4_server.service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Service
@SuppressWarnings("deprecation")
@Slf4j
public class JwtServiceImpl implements JwtService {
    @Value("${application.token.secret-key}")
    private String secretKey;

    @Value("${application.token.access-token.expiration}")
    private long accessTokenExpiration;

    @Value("${application.token.refresh-token.expiration}")
    private long refreshTokenExpiration;

    @Override
    public String generateAccessToken(UserDetails userDetails) {
        return generateToken(userDetails, accessTokenExpiration);
    }

    @Override
    public String generateRefreshToken(UserDetails userDetails) {
        return generateToken(userDetails, refreshTokenExpiration);
    }

    @Override
    public boolean inValidToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !extractExpiration(token).before(new Date()));
    }

    // parse token
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException | MalformedJwtException | UnsupportedJwtException | SecurityException
                | IllegalArgumentException e) {
            log.info(e.getMessage());
            return null;
        }
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        try {
            final Claims claims = extractAllClaims(token);
            return claimsResolver.apply(claims);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // build token
    private String generateToken(UserDetails userDetails, long expiration) {
        return buildTokenApp(new HashMap<>(), userDetails, expiration);
    }

    private String buildTokenApp(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
