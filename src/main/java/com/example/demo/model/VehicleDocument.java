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

@Entity
@Table(name = "vehicle_documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le type de document ne peut pas être vide")
    @Size(min = 2, max = 50, message = "Le type de document doit contenir entre 2 et 50 caractères")
    private String type;

    @NotNull(message = "La date d'expiration doit être spécifiée")
    @Future(message = "La date d'expiration doit être dans le futur")
    private LocalDate dateExpiration;

    @NotNull(message = "La validité du document doit être spécifiée")
    private boolean valide;

    @NotNull(message = "Le véhicule doit être spécifié")
    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;
} 