package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "maintenances")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Maintenance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le type de maintenance ne peut pas être vide")
    @Size(min = 2, max = 50, message = "Le type de maintenance doit contenir entre 2 et 50 caractères")
    private String type;

    @NotNull(message = "La fréquence en kilomètres doit être spécifiée")
    @Min(value = 0, message = "La fréquence en kilomètres ne peut pas être négative")
    private int frequenceKm;

    @NotNull(message = "La fréquence en mois doit être spécifiée")
    @Min(value = 0, message = "La fréquence en mois ne peut pas être négative")
    private int frequenceMois;

    @NotNull(message = "La dernière date doit être spécifiée")
    @PastOrPresent(message = "La dernière date ne peut pas être dans le futur")
    private LocalDate derniereDate;

    @NotNull(message = "Le dernier kilométrage doit être spécifié")
    @Min(value = 0, message = "Le dernier kilométrage ne peut pas être négatif")
    private int dernierKilometrage;

    @NotNull(message = "Le véhicule doit être spécifié")
    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;
} 