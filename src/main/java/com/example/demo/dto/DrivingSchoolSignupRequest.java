package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DrivingSchoolSignupRequest {
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

    @NotBlank(message = "Le nom de l'auto-école ne peut pas être vide")
    @Size(min = 3, max = 100, message = "Le nom de l'auto-école doit contenir entre 3 et 100 caractères")
    private String schoolName;

    @NotBlank(message = "L'adresse de l'auto-école ne peut pas être vide")
    @Size(min = 5, max = 200, message = "L'adresse doit contenir entre 5 et 200 caractères")
    private String schoolAddress;

    @NotNull(message = "Le prix de l'heure de code ne peut pas être vide")
    private Double codeHourPrice;

    @NotNull(message = "Le prix de l'heure de conduite ne peut pas être vide")
    private Double drivingHourPrice;
} 