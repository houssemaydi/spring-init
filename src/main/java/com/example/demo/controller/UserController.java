package com.example.demo.controller;

import com.example.demo.dto.PasswordChangeRequest;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Contrôleur pour la gestion des utilisateurs
 * Chaque méthode est sécurisée avec des annotations @PreAuthorize
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Utilisateurs", description = "API de gestion des utilisateurs")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Récupère tous les utilisateurs
     * Accessible uniquement aux utilisateurs avec la permission USER_READ
     * @return La liste des utilisateurs
     */
    @GetMapping
    @PreAuthorize("hasAuthority('USER_READ')")
    @Operation(summary = "Récupérer tous les utilisateurs", description = "Liste tous les utilisateurs enregistrés")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des utilisateurs récupérée avec succès"),
        @ApiResponse(responseCode = "403", description = "Accès refusé - Permissions insuffisantes")
    })
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Récupère un utilisateur par son ID
     * Accessible uniquement aux utilisateurs avec la permission USER_READ
     * @param id L'ID de l'utilisateur
     * @return L'utilisateur ou une erreur 404 si non trouvé
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_READ')")
    @Operation(summary = "Récupérer un utilisateur par ID", description = "Renvoie un utilisateur spécifique")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Utilisateur trouvé"),
        @ApiResponse(responseCode = "403", description = "Accès refusé - Permissions insuffisantes"),
        @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    public ResponseEntity<User> getUserById(
        @Parameter(description = "ID de l'utilisateur", required = true)
        @PathVariable Long id) {
        return userRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Supprime un utilisateur par son ID
     * Accessible uniquement aux utilisateurs avec la permission USER_DELETE
     * @param id L'ID de l'utilisateur à supprimer
     * @return Confirmation de suppression ou erreur 404 si non trouvé
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_DELETE')")
    @Operation(summary = "Supprimer un utilisateur", description = "Supprime un utilisateur par son ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Utilisateur supprimé avec succès"),
        @ApiResponse(responseCode = "403", description = "Accès refusé - Permissions insuffisantes"),
        @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    public ResponseEntity<?> deleteUser(
        @Parameter(description = "ID de l'utilisateur", required = true)
        @PathVariable Long id) {
        return userRepository.findById(id)
            .map(user -> {
                userRepository.delete(user);
                return ResponseEntity.ok().body("Utilisateur supprimé avec succès");
            })
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Récupère un utilisateur par son nom d'utilisateur
     * Accessible uniquement aux utilisateurs avec la permission USER_READ
     * @param username Le nom d'utilisateur recherché
     * @return L'utilisateur ou une erreur 404 si non trouvé
     */
    @GetMapping("/by-username/{username}")
    @PreAuthorize("hasAuthority('USER_READ')")
    @Operation(summary = "Rechercher un utilisateur par nom d'utilisateur",
        description = "Renvoie un utilisateur correspondant au nom d'utilisateur")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Utilisateur trouvé"),
        @ApiResponse(responseCode = "403", description = "Accès refusé - Permissions insuffisantes"),
        @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    public ResponseEntity<User> getUserByUsername(
        @Parameter(description = "Nom d'utilisateur", required = true)
        @PathVariable String username) {
        return userRepository.findByUsername(username)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Change le mot de passe de l'utilisateur authentifié
     * Accessible à l'utilisateur connecté
     * @param passwordChangeRequest La requête contenant les anciens et nouveaux mots de passe
     * @return Confirmation du changement ou erreur si les mots de passe sont invalides
     */
    @PostMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Changer de mot de passe", description = "Permet à l'utilisateur de changer son mot de passe")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Mot de passe changé avec succès"),
        @ApiResponse(responseCode = "400", description = "Données invalides ou mot de passe actuel incorrect"),
        @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<?> changePassword(@Valid @RequestBody PasswordChangeRequest passwordChangeRequest) {
        // Récupération de l'utilisateur authentifié
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return userRepository.findByUsername(username)
            .map(user -> {
                // Vérification que le mot de passe actuel est correct
                if (!passwordEncoder.matches(passwordChangeRequest.getCurrentPassword(), user.getPassword())) {
                    return ResponseEntity.badRequest().body(Map.of("message", "Le mot de passe actuel est incorrect"));
                }

                // Vérification que le nouveau mot de passe et sa confirmation correspondent
                if (!passwordChangeRequest.getNewPassword().equals(passwordChangeRequest.getConfirmPassword())) {
                    return ResponseEntity.badRequest().body(Map.of("message", "Les nouveaux mots de passe ne correspondent pas"));
                }

                // Encodage et mise à jour du mot de passe
                user.setPassword(passwordEncoder.encode(passwordChangeRequest.getNewPassword()));
                userRepository.save(user);

                return ResponseEntity.ok(Map.of("message", "Mot de passe modifié avec succès"));
            })
            .orElse(ResponseEntity.badRequest().build());
    }
}
