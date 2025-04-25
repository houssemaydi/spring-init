package com.example.demo.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * Classe utilitaire pour la génération et validation de tokens JWT
 */
@Component
@Slf4j
public class JwtUtils {

    /**
     * Clé secrète pour signer les tokens (à définir dans application.properties)
     */
    @Value("${app.jwtSecret:defaultSecretKeyForDevelopmentEnvironmentOnly}")
    private String jwtSecret;

    /**
     * Durée de validité du token en ms (à définir dans application.properties)
     */
    @Value("${app.jwtExpirationMs:86400000}") // 24 heures par défaut
    private int jwtExpirationMs;

    private Key key;

    /**
     * Initialise la clé de signature à partir du secret
     */
    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /**
     * Génère un token JWT à partir de l'authentification de l'utilisateur
     * @param authentication L'objet Authentication contenant les détails de l'utilisateur
     * @return Le token JWT généré
     */
    public String generateJwtToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        // Récupération des autorités (roles et permissions)
        String authorities = userPrincipal.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","));

        return Jwts.builder()
            .setSubject(userPrincipal.getUsername())
            .claim("authorities", authorities) // Ajoute les autorités au token
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
            .signWith(key, SignatureAlgorithm.HS512)
            .compact();
    }

    /**
     * Extrait le nom d'utilisateur du token JWT
     * @param token Le token JWT
     * @return Le nom d'utilisateur extrait
     */
    public String getUsernameFromJwtToken(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
    }

    /**
     * Valide un token JWT
     * @param token Le token JWT à valider
     * @return true si le token est valide, false sinon
     */
    public boolean validateJwtToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SignatureException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }
}
