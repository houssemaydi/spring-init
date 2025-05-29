package com.example.demo.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CandidateSignupRequest {
    @NotBlank(message = "Le prénom ne peut pas être vide")
    @Size(min = 2, max = 50, message = "Le prénom doit contenir entre 2 et 50 caractères")
    private String firstName;

    @NotBlank(message = "Le nom ne peut pas être vide")
    @Size(min = 2, max = 50, message = "Le nom doit contenir entre 2 et 50 caractères")
    private String lastName;

    @NotBlank(message = "L'email ne peut pas être vide")
    @Email(message = "L'email doit être valide")
    @Size(max = 100, message = "L'email ne peut pas dépasser 100 caractères")
    private String email;

    @NotBlank(message = "Le mot de passe ne peut pas être vide")
    @Size(min = 6, max = 100, message = "Le mot de passe doit contenir entre 6 et 100 caractères")
    private String password;

    @NotBlank(message = "Le numéro de téléphone ne peut pas être vide")
    @Pattern(regexp = "^[0-9]{8}$", message = "Le numéro de téléphone doit contenir exactement 8 chiffres")
    private String phoneNumber;

    @NotBlank(message = "Le numéro CIN ne peut pas être vide")
    @Size(min = 8, max = 20, message = "Le numéro CIN doit contenir entre 8 et 20 caractères")
    private String cinNumber;

    @NotNull(message = "L'ID de l'auto-école ne peut pas être vide")
    private Long drivingSchoolId;
}
