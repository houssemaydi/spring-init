package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.Set;

@Entity
@Table(name = "candidates")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Candidate extends User {

    @Min(value = 0, message = "Le nombre d'heures de code ne peut pas être négatif")
    private int heuresCode;

    @Min(value = 0, message = "Le nombre d'heures de conduite ne peut pas être négatif")
    private int heuresConduite;

    @NotNull(message = "Le solde doit être spécifié")
    private double solde;

    @NotNull(message = "L'accès GL doit être spécifié")
    private boolean hasGlAccess;

    @NotNull(message = "utilisateur a un prix special")
    private boolean hasSpecialPrice;

    @NotNull(message = "Le prix de l'heure de code doit être spécifié")
    @Min(value = 0, message = "Le prix de l'heure de code ne peut pas être négatif")
    private double prixCodeHeure;

    @NotNull(message = "Le prix de l'heure de conduite doit être spécifié")
    @Min(value = 0, message = "Le prix de l'heure de conduite ne peut pas être négatif")
    private double prixConduiteHeure;

    @NotBlank(message = "Le numéro de téléphone ne peut pas être vide")
    @Pattern(regexp = "^[0-9]{8}$", message = "Le numéro de téléphone doit contenir exactement 8 chiffres")
    private String phoneNumber;

    @NotBlank(message = "Le numéro CIN ne peut pas être vide")
    @Pattern(regexp = "^[0-9]{8}$", message = "Le numéro CIN doit contenir exactement 8 chiffres")
    private String cinNumber;

    @OneToMany(mappedBy = "candidate")
    private Set<Seance> seances;

    @OneToMany(mappedBy = "candidate")
    private Set<Payment> payments;
}
