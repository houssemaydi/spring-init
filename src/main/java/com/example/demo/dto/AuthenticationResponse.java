package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour la réponse d'authentification
 * Contient le token JWT et les informations de base de l'utilisateur
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {

    /**
     * Token JWT généré lors de l'authentification
     */
    private String token;

    /**
     * Identifiant de l'utilisateur
     */
    private Long id;

    /**
     * Nom d'utilisateur
     */
    private String username;

    /**
     * Adresse email de l'utilisateur
     */
    private String email;
}
