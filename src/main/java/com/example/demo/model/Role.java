package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/**
 * Entité représentant un rôle dans le système
 * Un rôle définit le niveau d'accès d'un utilisateur
 */
@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
public class Role {

    /**
     * Identifiant unique du rôle, généré automatiquement
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nom du rôle, doit être unique
     */
    @NotBlank(message = "Le nom du rôle ne peut pas être vide")
    @Size(min = 3, max = 50, message = "Le nom du rôle doit contenir entre 3 et 50 caractères")
    @Column(unique = true)
    private String name;

    /**
     * Description du rôle
     */
    @Column(length = 255)
    private String description;

    /**
     * Les utilisateurs ayant ce rôle
     * Relation many-to-many avec l'entité User
     */
    @ManyToMany(mappedBy = "roles")
    private Set<User> users = new HashSet<>();
}
