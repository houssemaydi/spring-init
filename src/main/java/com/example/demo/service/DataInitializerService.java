package com.example.demo.service;

import com.example.demo.model.ApprovalStatus;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Service pour initialiser les données par défaut dans la base de données
 * Exécuté automatiquement au démarrage de l'application en environnement de développement
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Profile({"dev", "default"}) // N'exécute pas en production
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
        Permission approveUser = createPermission("USER_APPROVE", "Approuver des utilisateurs");
        Permission manageDrivingSchool = createPermission("MANAGE_DRIVING_SCHOOL", "Gérer une auto-école");

        // Création des rôles avec leurs permissions
        Role superAdminRole = createRole("SUPERADMIN", "Administrateur de la plateforme avec accès à toutes les auto-écoles",
            Set.of(readUser, writeUser, deleteUser, readRole, writeRole, deleteRole,
                readPermission, writePermission, deletePermission, approveUser, manageDrivingSchool));

        Role adminRole = createRole("ADMIN", "Propriétaire d'une auto-école",
            Set.of(readUser, writeUser, readRole, readPermission, approveUser, manageDrivingSchool));

        Role gestionnaireRole = createRole("GESTIONNAIRE_AUTO_ECOLE", "Responsable d'un établissement",
            Set.of(readUser, writeUser, readRole, readPermission));

        Role secretaireRole = createRole("SECRETAIRE", "Personnel administratif",
            Set.of(readUser, readRole));

        Role moniteurRole = createRole("MONITEUR", "Formateur de conduite",
            Set.of(readUser, readRole));

        Role candidatRole = createRole("CANDIDAT", "Élève inscrit à l'auto-école",
            Set.of(readUser));

        Role supportRole = createRole("SUPPORT", "Support technique",
            Set.of(readUser, readRole));

        // Création d'utilisateurs de test
        // 1. Superadmin (approuvé automatiquement, pas d'approbateur)
        User superAdmin = createUser("superadmin", "admin", "superadmin@example.com", "admin123", Set.of(superAdminRole), null, ApprovalStatus.APPROVED);

        // 2. Admin d'une auto-école (approuvé par le superadmin)
        User admin = createUser("admin", "admin", "admin@example.com", "admin123", Set.of(adminRole), superAdmin, ApprovalStatus.APPROVED);

        // 3. Utilisateurs de l'auto-école (en attente d'approbation par l'admin)
        createUser("gestionnaire", "manager", "gestionnaire@example.com", "manager123", Set.of(gestionnaireRole), admin, ApprovalStatus.PENDING);
        createUser("secretaire", "user", "secretaire@example.com", "user123", Set.of(secretaireRole), admin, ApprovalStatus.PENDING);
        createUser("moniteur", "user", "moniteur@example.com", "user123", Set.of(moniteurRole), admin, ApprovalStatus.PENDING);
        createUser("candidat", "user", "candidat@example.com", "user123", Set.of(candidatRole), admin, ApprovalStatus.PENDING);
        createUser("support", "user", "support@example.com", "user123", Set.of(supportRole), admin, ApprovalStatus.PENDING);

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
     * @param firstName Nom d'utilisateur
     * @param lastName Nom d'utilisateur
     * @param email Adresse email
     * @param password Mot de passe en clair (sera encodé)
     * @param roles Ensemble des rôles de l'utilisateur
     * @param approvedBy Utilisateur qui approuve ce compte
     * @param approvalStatus Status d'approbation initial
     * @return L'utilisateur créé
     */
    private User createUser(String firstName, String lastName, String email, String password, Set<Role> roles, User approvedBy, ApprovalStatus approvalStatus) {
        User user = new User();
        user.setUsername(UUID.randomUUID().toString());
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(roles);
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setApprovalStatus(approvalStatus);
        user.setApprovedBy(approvedBy);
        if (approvalStatus == ApprovalStatus.APPROVED) {
            user.setApprovedAt(LocalDateTime.now());
        }
        return userRepository.save(user);
    }
}
