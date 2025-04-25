package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.example.demo.model.Permission;
import java.util.HashSet;
import java.util.Set;

/**
 * Entité représentant un rôle dans le système
 * Un rôle est un ensemble de permissions
 */
@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
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
    @Column(unique = true, nullable = false)
    private String name;

    /**
     * Description du rôle
     */
    private String description;

    /**
     * Les permissions attribuées à ce rôle
     * Relation many-to-many avec l'entité Permission
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "role_permissions",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions = new HashSet<>();
}
