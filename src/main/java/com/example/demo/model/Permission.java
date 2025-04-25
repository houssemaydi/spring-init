package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entité représentant une permission spécifique dans le système
 * Les permissions définissent les actions qu'un utilisateur peut effectuer
 */
@Entity
@Table(name = "permissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Permission {

    /**
     * Identifiant unique de la permission, généré automatiquement
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nom de la permission, doit être unique
     * Généralement au format RESSOURCE_ACTION (ex: USER_READ, USER_WRITE)
     */
    @Column(unique = true, nullable = false)
    private String name;

    /**
     * Description de la permission
     */
    private String description;
}
