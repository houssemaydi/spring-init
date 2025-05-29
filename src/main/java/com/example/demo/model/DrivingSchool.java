package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;

@Entity
@Table(name = "driving_schools")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DrivingSchool {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom ne peut pas être vide")
    @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
    private String nom;

    @NotBlank(message = "L'adresse ne peut pas être vide")
    @Size(min = 5, max = 200, message = "L'adresse doit contenir entre 5 et 200 caractères")
    private String adresse;

    @NotNull(message = "Le prix de l'heure de code doit être spécifié")
    @Min(value = 0, message = "Le prix de l'heure de code ne peut pas être négatif")
    private double prixCodeHeure;

    @NotNull(message = "Le prix de l'heure de conduite doit être spécifié")
    @Min(value = 0, message = "Le prix de l'heure de conduite ne peut pas être négatif")
    private double prixConduiteHeure;

    @OneToMany(mappedBy = "drivingSchool")
    private Set<User> users;

    @OneToMany(mappedBy = "drivingSchool")
    private Set<Vehicle> vehicles;
} 