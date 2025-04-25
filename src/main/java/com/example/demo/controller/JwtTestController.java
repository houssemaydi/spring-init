package com.example.demo.controller;


import com.example.demo.security.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Key;
import java.util.HashMap;
import java.util.Map;

/**
 * Contrôleur pour tester et déboguer les tokens JWT
 * Utile uniquement en développement, à désactiver en production
 */
@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
@Tag(name = "Test", description = "API de test (développement uniquement)")
public class JwtTestController {

    private final JwtUtils jwtUtils;

    /**
     * Valide et décode un token JWT
     * @param token Le token JWT à vérifier
     * @return Les informations extraites du token
     */
    @GetMapping("/jwt-decode")
    @Operation(summary = "Décoder un token JWT",
        description = "Valide et décode un token JWT pour afficher ses informations")
    public ResponseEntity<?> decodeJwt(@RequestParam("token") String token) {
        Map<String, Object> response = new HashMap<>();

        // Vérification de la validité du token
        boolean isValid = jwtUtils.validateJwtToken(token);
        response.put("valid", isValid);

        if (isValid) {
            try {
                // Extraction du nom d'utilisateur
                String username = jwtUtils.getUsernameFromJwtToken(token);
                response.put("username", username);

                // Pour des informations plus détaillées, nous pourrions ajouter des méthodes
                // à JwtUtils pour exposer plus d'informations du token
            } catch (Exception e) {
                response.put("error", "Erreur lors du décodage du token: " + e.getMessage());
            }
        }

        return ResponseEntity.ok(response);
    }
}
