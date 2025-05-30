package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entité représentant un utilisateur dans le système
 * Utilise JPA pour la persistance en base de données
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public class User {

    /**
     * Identifiant unique de l'utilisateur, généré automatiquement
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le prénom ne peut pas être vide")
    @Size(min = 2, max = 50, message = "Le prénom doit contenir entre 2 et 50 caractères")
    @Column(nullable = false)
    private String firstName;

    @NotBlank(message = "Le nom ne peut pas être vide")
    @Size(min = 2, max = 50, message = "Le nom doit contenir entre 2 et 50 caractères")
    @Column(nullable = false)
    private String lastName;


    /**
     * Nom d'utilisateur unique
     */
    @Column(unique = true, nullable = true)
    private String username;

    /**
     * Adresse email unique
     */
    @NotBlank(message = "L'email ne peut pas être vide")
    @Email(message = "L'email doit être valide")
    @Size(max = 100, message = "L'email ne peut pas dépasser 100 caractères")
    @Column(unique = true, nullable = false)
    private String email;

    /**
     * Mot de passe de l'utilisateur (sera stocké crypté)
     */
    @NotBlank(message = "Le mot de passe ne peut pas être vide")
    @Size(min = 6, max = 100, message = "Le mot de passe doit contenir entre 6 et 100 caractères")
    @Column(nullable = false)
    private String password;

    /**
     * Les rôles attribués à l'utilisateur
     * Relation many-to-many avec l'entité Role
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    /**
     * Indique si le compte utilisateur est activé
     */
    @Column(nullable = true)
    private Boolean enabled;

    /**
     * Indique si le compte n'est pas verrouillé
     */
    @Column(nullable = true)
    private Boolean accountNonLocked;

    /**
     * Indique si les identifiants ne sont pas expirés
     */
    @Column(nullable = true)
    private Boolean credentialsNonExpired;

    /**
     * Indique si le compte n'est pas expiré
     */
    @Column(nullable = true)
    private Boolean accountNonExpired;

    @OneToMany(mappedBy = "approuveePar")
    private Set<Seance> seancesApprouvees = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "driving_school_id")
    private DrivingSchool drivingSchool;

    /**
     * Status d'approbation de l'utilisateur
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private ApprovalStatus approvalStatus;

    /**
     * Utilisateur qui a approuvé ce compte
     */
    @ManyToOne
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    /**
     * Date d'approbation du compte
     */
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    /**
     * Initialise les valeurs par défaut pour les champs de sécurité
     */
    @PrePersist
    public void prePersist() {
        if (enabled == null) enabled = true;
        if (accountNonLocked == null) accountNonLocked = true;
        if (credentialsNonExpired == null) credentialsNonExpired = true;
        if (accountNonExpired == null) accountNonExpired = true;
        if (approvalStatus == null) approvalStatus = ApprovalStatus.PENDING;
    }
}
