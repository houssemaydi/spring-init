package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @NotBlank(message = "Le nom du rôle ne peut pas être vide")
    @Size(min = 3, max = 50, message = "Le nom du rôle doit contenir entre 3 et 50 caractères")
    @Column(unique = true, nullable = false)
    private String name;

    /**
     * Description du rôle
     */
    @Size(max = 255, message = "La description ne peut pas dépasser 255 caractères")
    private String description;

    /**
     * Les permissions attribuées à ce rôle
     * Relation many-to-many avec l'entité Permission
     */
    @NotNull(message = "Les permissions ne peuvent pas être nulles")
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "role_permissions",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions = new HashSet<>();

    /**
     * Enumération des rôles prédéfinis dans le système
     */
    public enum RoleType {
        SUPERADMIN("Accès complet à toutes les fonctionnalités, y compris la création de modules et gestion globale."),
        ADMIN("Administrateur d'une auto-école spécifique"),
        GESTIONNAIRE_AUTO_ECOLE("Responsable d'un établissement"),
        SECRETAIRE("Personnel administratif"),
        MONITEUR("Formateur de conduite"),
        CANDIDAT("Élève inscrit à l'auto-école"),
        SUPPORT("Support technique");

        private final String description;

        RoleType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
