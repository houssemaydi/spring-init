package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "seances")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Seance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La date doit être spécifiée")
    @Future(message = "La date doit être dans le futur")
    private LocalDate date;

    @NotNull(message = "L'heure de début doit être spécifiée")
    private LocalTime heureDebut;

    @NotNull(message = "L'heure de fin doit être spécifiée")
    private LocalTime heureFin;

    @NotNull(message = "Le type de séance doit être spécifié")
    @Enumerated(EnumType.STRING)
    private SeanceType type;

    @NotNull(message = "Le statut de la séance doit être spécifié")
    @Enumerated(EnumType.STRING)
    private StatutSeance statut;

    @NotBlank(message = "Le lieu ne peut pas être vide")
    @Size(min = 3, max = 200, message = "Le lieu doit contenir entre 3 et 200 caractères")
    private String lieu;

    @NotNull(message = "Le candidat doit être spécifié")
    @ManyToOne
    @JoinColumn(name = "candidate_id")
    private Candidate candidate;

    @ManyToOne
    @JoinColumn(name = "approuvee_par_id")
    private User approuveePar;
} 