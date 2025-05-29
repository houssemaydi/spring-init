package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Le montant doit être spécifié")
    @Min(value = 0, message = "Le montant ne peut pas être négatif")
    private double montant;

    @NotNull(message = "Le type de paiement doit être spécifié")
    @Enumerated(EnumType.STRING)
    private PaymentType type;

    @NotNull(message = "La date doit être spécifiée")
    @PastOrPresent(message = "La date ne peut pas être dans le futur")
    private LocalDate date;

    @NotNull(message = "Le candidat doit être spécifié")
    @ManyToOne
    @JoinColumn(name = "candidate_id")
    private Candidate candidate;
} 