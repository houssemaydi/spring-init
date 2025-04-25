package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour les demandes de connexion
 * Contient les informations nécessaires pour l'authentification
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

    /**
     * Nom d'utilisateur pour la connexion
     */
    @NotBlank(message = "Le nom d'utilisateur ne peut pas être vide")
    private String username;

    /**
     * Mot de passe pour la connexion
     */
    @NotBlank(message = "Le mot de passe ne peut pas être vide")
    private String password;
}
