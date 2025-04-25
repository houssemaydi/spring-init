package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * DTO pour les demandes d'enregistrement d'un nouvel utilisateur
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    /**
     * Nom d'utilisateur souhaité
     */
    @NotBlank(message = "Le nom d'utilisateur ne peut pas être vide")
    @Size(min = 3, max = 50, message = "Le nom d'utilisateur doit contenir entre 3 et 50 caractères")
    private String username;

    /**
     * Adresse email
     */
    @NotBlank(message = "L'email ne peut pas être vide")
    @Email(message = "L'email doit être valide")
    @Size(max = 100, message = "L'email ne peut pas dépasser 100 caractères")
    private String email;

    /**
     * Mot de passe souhaité
     */
    @NotBlank(message = "Le mot de passe ne peut pas être vide")
    @Size(min = 6, max = 100, message = "Le mot de passe doit contenir entre 6 et 100 caractères")
    private String password;

    /**
     * Rôles demandés (optionnel)
     * Si non fourni, un rôle par défaut sera attribué
     */
    private Set<String> roles;
}
