package com.example.demo.controller;


import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.dto.AuthenticationResponse;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Contrôleur pour gérer l'authentification et l'enregistrement des utilisateurs
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentification", description = "API d'authentification")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    /**
     * Endpoint pour l'authentification des utilisateurs existants
     * @param loginRequest Les identifiants de connexion
     * @return La réponse contenant le token JWT
     */
    @PostMapping("/login")
    @Operation(summary = "Authentifier un utilisateur", description = "Authentifie un utilisateur et retourne un token JWT")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Authentification réussie",
            content = @Content(schema = @Schema(implementation = AuthenticationResponse.class))),
        @ApiResponse(responseCode = "401", description = "Identifiants invalides")
    })
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        // Authentification de l'utilisateur
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        // Mise à jour du contexte de sécurité
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Génération du token JWT
        String jwt = jwtUtils.generateJwtToken(authentication);

        // Récupération des détails de l'utilisateur
        User user = userRepository.findByUsername(loginRequest.getUsername()).orElseThrow();

        // Création de la réponse
        AuthenticationResponse response = new AuthenticationResponse(
            jwt,
            user.getId(),
            user.getUsername(),
            user.getEmail()
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint pour l'enregistrement de nouveaux utilisateurs
     * @param registerRequest Les informations du nouvel utilisateur
     * @return La réponse indiquant le succès de l'opération
     */
    @PostMapping("/register")
    @Operation(summary = "Enregistrer un nouvel utilisateur", description = "Crée un nouveau compte utilisateur")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Utilisateur créé avec succès"),
        @ApiResponse(responseCode = "400", description = "Données invalides ou nom d'utilisateur/email déjà utilisé")
    })
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        // Vérification de l'existence du nom d'utilisateur
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            return ResponseEntity
                .badRequest()
                .body(Map.of("message", "Ce nom d'utilisateur est déjà pris"));
        }

        // Vérification de l'existence de l'email
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity
                .badRequest()
                .body(Map.of("message", "Cette adresse email est déjà utilisée"));
        }

        // Création du nouvel utilisateur
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        // Attribution des rôles
        Set<Role> roles = new HashSet<>();

        if (registerRequest.getRoles() == null || registerRequest.getRoles().isEmpty()) {
            // Attribution du rôle USER par défaut
            Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Rôle USER non trouvé"));
            roles.add(userRole);
        } else {
            // Attribution des rôles demandés
            registerRequest.getRoles().forEach(roleName -> {
                Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new RuntimeException("Rôle " + roleName + " non trouvé"));
                roles.add(role);
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(Map.of("message", "Utilisateur enregistré avec succès"));
    }
}
