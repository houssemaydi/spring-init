package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class User {

    /**
     * Identifiant unique de l'utilisateur, généré automatiquement
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nom d'utilisateur unique
     */
    @Column(unique = true, nullable = false)
    private String username;

    /**
     * Adresse email unique
     */
    @Column(unique = true, nullable = false)
    private String email;

    /**
     * Mot de passe de l'utilisateur (sera stocké crypté)
     */
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
    @Column(nullable = false)
    private boolean enabled = true;

    /**
     * Indique si le compte n'est pas verrouillé
     */
    @Column(nullable = false)
    private boolean accountNonLocked = true;

    /**
     * Indique si les identifiants ne sont pas expirés
     */
    @Column(nullable = false)
    private boolean credentialsNonExpired = true;

    /**
     * Indique si le compte n'est pas expiré
     */
    @Column(nullable = false)
    private boolean accountNonExpired = true;
}
