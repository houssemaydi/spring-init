package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.model.*;
import com.example.demo.repository.DrivingSchoolRepository;
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
import java.util.UUID;

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
    private final DrivingSchoolRepository drivingSchoolRepository;
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
    @Operation(summary = "Enregistrer un nouvel utilisateur", description = "Crée un nouveau compte utilisateur avec les informations de base")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Utilisateur créé avec succès"),
        @ApiResponse(responseCode = "400", description = "Données invalides ou email déjà utilisé")
    })
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        // Vérification de l'existence de l'email
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity
                .badRequest()
                .body(Map.of("message", "Cette adresse email est déjà utilisée"));
        }

        // Création du nouvel utilisateur
        User user = new User();
        user.setUsername(UUID.randomUUID().toString());
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);

        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(Map.of(
                "message", "Utilisateur enregistré avec succès",
                "userId", user.getId()
            ));
    }

    @PostMapping("/complete-candidate-registration/{userId}")
    @Operation(summary = "Compléter l'enregistrement d'un candidat", description = "Complète l'enregistrement d'un utilisateur en tant que candidat")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Enregistrement complété avec succès"),
        @ApiResponse(responseCode = "400", description = "Données invalides"),
        @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    public ResponseEntity<?> completeCandidateRegistration(
            @PathVariable Long userId,
            @Valid @RequestBody CandidateRegistrationRequest request) {
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Vérifier si l'utilisateur est déjà un candidat
        if (user instanceof Candidate) {
            return ResponseEntity
                .badRequest()
                .body(Map.of("message", "L'utilisateur est déjà un candidat"));
        }

        // Récupérer l'auto-école
        DrivingSchool drivingSchool = drivingSchoolRepository.findById(request.getDrivingSchoolId())
            .orElseThrow(() -> new RuntimeException("Auto-école non trouvée"));

        // Créer un nouveau candidat
        Candidate candidate = new Candidate();
        candidate.setId(user.getId());
        candidate.setUsername(user.getUsername());
        candidate.setFirstName(user.getFirstName());
        candidate.setLastName(user.getLastName());
        candidate.setEmail(user.getEmail());
        candidate.setPassword(user.getPassword());
        candidate.setPhoneNumber(request.getPhoneNumber());
        candidate.setCinNumber(request.getCinNumber());
        candidate.setHasSpecialPrice(false);
        candidate.setHeuresCode(0);
        candidate.setHeuresConduite(0);
        candidate.setSolde(0.0);
        candidate.setHasGlAccess(false);
        candidate.setPrixCodeHeure(drivingSchool.getPrixCodeHeure());
        candidate.setPrixConduiteHeure(drivingSchool.getPrixConduiteHeure());
        candidate.setDrivingSchool(drivingSchool);

        // Attribution du rôle CANDIDAT
        Role candidateRole = roleRepository.findByName("CANDIDAT")
            .orElseThrow(() -> new RuntimeException("Rôle CANDIDAT non trouvé"));
        Set<Role> roles = new HashSet<>();
        roles.add(candidateRole);
        candidate.setRoles(roles);

        userRepository.save(candidate);

        return ResponseEntity.ok(Map.of("message", "Enregistrement du candidat complété avec succès"));
    }

    @PostMapping("/complete-school-driver-registration/{userId}")
    @Operation(summary = "Compléter l'enregistrement d'un propriétaire d'auto-école", description = "Complète l'enregistrement d'un utilisateur en tant que propriétaire d'auto-école")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Enregistrement complété avec succès"),
        @ApiResponse(responseCode = "400", description = "Données invalides"),
        @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    public ResponseEntity<?> completeSchoolDriverRegistration(
            @PathVariable Long userId,
            @Valid @RequestBody SchoolDriverRegistrationRequest request) {

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Vérifier si l'utilisateur est déjà associé à une auto-école
        if (user.getDrivingSchool() != null) {
            return ResponseEntity
                .badRequest()
                .body(Map.of("message", "L'utilisateur est déjà associé à une auto-école"));
        }

        // Création de l'auto-école
        DrivingSchool drivingSchool = new DrivingSchool();
        drivingSchool.setNom(request.getSchoolName());
        drivingSchool.setAdresse(request.getSchoolAddress());
        drivingSchool.setPrixCodeHeure(request.getCodeHourPrice());
        drivingSchool.setPrixConduiteHeure(request.getDrivingHourPrice());
        drivingSchool = drivingSchoolRepository.save(drivingSchool);

        // Association de l'utilisateur à l'auto-école
        user.setDrivingSchool(drivingSchool);

        // Attribution du rôle GESTIONNAIRE_AUTO_ECOLE
        Role ownerRole = roleRepository.findByName("GESTIONNAIRE_AUTO_ECOLE")
            .orElseThrow(() -> new RuntimeException("Rôle GESTIONNAIRE_AUTO_ECOLE non trouvé"));
        Set<Role> roles = new HashSet<>();
        roles.add(ownerRole);
        user.setRoles(roles);

        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "Enregistrement du propriétaire d'auto-école complété avec succès"));
    }
}
