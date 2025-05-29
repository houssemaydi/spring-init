package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour compléter l'enregistrement d'un candidat
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CandidateRegistrationRequest {

    @NotBlank(message = "Le numéro de téléphone ne peut pas être vide")
    @Pattern(regexp = "^[0-9]{8}$", message = "Le numéro de téléphone doit contenir exactement 8 chiffres")
    private String phoneNumber;

    @NotBlank(message = "Le numéro CIN ne peut pas être vide")
    @Pattern(regexp = "^[0-9]{8}$", message = "Le numéro CIN doit contenir exactement 8 chiffres")
    private String cinNumber;

    @NotNull(message = "L'auto-école doit être spécifiée")
    private Long drivingSchoolId;
} 