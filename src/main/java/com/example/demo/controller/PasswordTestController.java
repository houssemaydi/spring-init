package com.example.demo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Contrôleur pour tester l'encodage des mots de passe
 * Utile uniquement en développement, à désactiver en production
 */
@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
@Tag(name = "Test", description = "API de test (développement uniquement)")
public class PasswordTestController {

    private final PasswordEncoder passwordEncoder;

    /**
     * Encode un mot de passe fourni et vérifie sa correspondance
     * @param rawPassword Le mot de passe en clair
     * @return Le résultat de l'encodage et de la vérification
     */
    @GetMapping("/password-encode")
    @Operation(summary = "Tester l'encodage de mot de passe",
        description = "Encode un mot de passe et vérifie sa correspondance")
    public ResponseEntity<Map<String, Object>> testPasswordEncoding(
        @RequestParam("password") String rawPassword) {

        // Encodage du mot de passe
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // Vérification de la correspondance
        boolean matches = passwordEncoder.matches(rawPassword, encodedPassword);

        // Création de la réponse
        Map<String, Object> response = new HashMap<>();
        response.put("rawPassword", rawPassword);
        response.put("encodedPassword", encodedPassword);
        response.put("matches", matches);

        return ResponseEntity.ok(response);
    }

    /**
     * Vérifie si un mot de passe en clair correspond à un mot de passe encodé
     * @param rawPassword Le mot de passe en clair
     * @param encodedPassword Le mot de passe encodé
     * @return Le résultat de la vérification
     */
    @GetMapping("/password-match")
    @Operation(summary = "Tester la correspondance de mot de passe",
        description = "Vérifie si un mot de passe correspond à sa version encodée")
    public ResponseEntity<Map<String, Object>> testPasswordMatch(
        @RequestParam("rawPassword") String rawPassword,
        @RequestParam("encodedPassword") String encodedPassword) {

        // Vérification de la correspondance
        boolean matches = passwordEncoder.matches(rawPassword, encodedPassword);

        // Création de la réponse
        Map<String, Object> response = new HashMap<>();
        response.put("rawPassword", rawPassword);
        response.put("encodedPassword", encodedPassword);
        response.put("matches", matches);

        return ResponseEntity.ok(response);
    }
}
