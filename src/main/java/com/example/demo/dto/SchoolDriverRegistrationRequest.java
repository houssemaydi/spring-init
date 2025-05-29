package com.example.demo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour compléter l'enregistrement d'un propriétaire d'auto-école
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SchoolDriverRegistrationRequest {

    @NotBlank(message = "Le nom de l'auto-école ne peut pas être vide")
    @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
    private String schoolName;

    @NotBlank(message = "L'adresse ne peut pas être vide")
    @Size(min = 5, max = 200, message = "L'adresse doit contenir entre 5 et 200 caractères")
    private String schoolAddress;

    @NotNull(message = "Le prix de l'heure de code doit être spécifié")
    @Min(value = 0, message = "Le prix de l'heure de code ne peut pas être négatif")
    private Double codeHourPrice;

    @NotNull(message = "Le prix de l'heure de conduite doit être spécifié")
    @Min(value = 0, message = "Le prix de l'heure de conduite ne peut pas être négatif")
    private Double drivingHourPrice;
} 