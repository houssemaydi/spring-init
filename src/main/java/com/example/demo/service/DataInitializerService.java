package com.example.demo.service;


import com.example.demo.model.Permission;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.PermissionRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Service pour initialiser les données par défaut dans la base de données
 * Exécuté automatiquement au démarrage de l'application en environnement de développement
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Profile({"dev", "default"}) // N'exécute pas en production
@ConditionalOnProperty(name = "spring.flyway.enabled", havingValue = "false")
public class DataInitializerService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Initialise les données par défaut (rôles, permissions, utilisateurs)
     * Exécuté après l'initialisation du bean Spring
     */
    @PostConstruct
    @Transactional
    public void init() {
        log.info("Initialisation des données par défaut...");

        // Vérification pour éviter de dupliquer les données
        if (roleRepository.count() > 0) {
            log.info("Les données sont déjà initialisées, passage...");
            return;
        }

        // Création des permissions standard
        Permission readUser = createPermission("USER_READ", "Lire les détails utilisateurs");
        Permission writeUser = createPermission("USER_WRITE", "Modifier des utilisateurs");
        Permission deleteUser = createPermission("USER_DELETE", "Supprimer des utilisateurs");
        Permission readRole = createPermission("ROLE_READ", "Lire les rôles");
        Permission writeRole = createPermission("ROLE_WRITE", "Modifier des rôles");
        Permission deleteRole = createPermission("ROLE_DELETE", "Supprimer des rôles");
        Permission readPermission = createPermission("PERMISSION_READ", "Lire les permissions");
        Permission writePermission = createPermission("PERMISSION_WRITE", "Modifier des permissions");
        Permission deletePermission = createPermission("PERMISSION_DELETE", "Supprimer des permissions");

        // Création des rôles standard avec leurs permissions
        Role adminRole = createRole("ADMIN", "Administrateur du système",
            Set.of(readUser, writeUser, deleteUser, readRole, writeRole, deleteRole,
                readPermission, writePermission, deletePermission));

        Role managerRole = createRole("MANAGER", "Gestionnaire de l'application",
            Set.of(readUser, writeUser, readRole, readPermission));

        Role userRole = createRole("USER", "Utilisateur standard",
            Set.of(readUser));

        // Création d'utilisateurs de test
        createUser("admin", "admin@example.com", "admin123", Set.of(adminRole));
        createUser("manager", "manager@example.com", "manager123", Set.of(managerRole));
        createUser("user", "user@example.com", "user123", Set.of(userRole));

        log.info("Initialisation des données terminée avec succès");
    }

    /**
     * Crée une permission
     * @param name Nom de la permission
     * @param description Description de la permission
     * @return La permission créée
     */
    private Permission createPermission(String name, String description) {
        Permission permission = new Permission();
        permission.setName(name);
        permission.setDescription(description);
        return permissionRepository.save(permission);
    }

    /**
     * Crée un rôle avec ses permissions
     * @param name Nom du rôle
     * @param description Description du rôle
     * @param permissions Ensemble des permissions du rôle
     * @return Le rôle créé
     */
    private Role createRole(String name, String description, Set<Permission> permissions) {
        Role role = new Role();
        role.setName(name);
        role.setDescription(description);
        role.setPermissions(permissions);
        return roleRepository.save(role);
    }

    /**
     * Crée un utilisateur
     * @param username Nom d'utilisateur
     * @param email Adresse email
     * @param password Mot de passe en clair (sera encodé)
     * @param roles Ensemble des rôles de l'utilisateur
     * @return L'utilisateur créé
     */
    private User createUser(String username, String email, String password, Set<Role> roles) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(roles);
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        return userRepository.save(user);
    }
}
