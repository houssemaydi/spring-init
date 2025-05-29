package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;

@Entity
@Table(name = "vehicles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "La marque ne peut pas être vide")
    @Size(min = 2, max = 50, message = "La marque doit contenir entre 2 et 50 caractères")
    private String marque;

    @NotBlank(message = "Le modèle ne peut pas être vide")
    @Size(min = 2, max = 50, message = "Le modèle doit contenir entre 2 et 50 caractères")
    private String modele;

    @NotBlank(message = "L'immatriculation ne peut pas être vide")
    @Pattern(regexp = "^[A-Z0-9]{2,10}$", message = "L'immatriculation doit contenir entre 2 et 10 caractères alphanumériques en majuscules")
    private String immatriculation;

    @Min(value = 0, message = "Le kilométrage ne peut pas être négatif")
    private int kilometrage;

    @NotNull(message = "La disponibilité doit être spécifiée")
    private boolean disponible;

    @NotNull(message = "L'auto-école doit être spécifiée")
    @ManyToOne
    @JoinColumn(name = "driving_school_id")
    private DrivingSchool drivingSchool;

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL)
    private Set<VehicleDocument> documents;

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL)
    private Set<Maintenance> maintenances;
} 