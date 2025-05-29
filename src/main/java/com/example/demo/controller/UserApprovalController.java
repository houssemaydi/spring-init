package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserApprovalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/approval")
@RequiredArgsConstructor
@Tag(name = "User Approval", description = "API pour la gestion des approbations d'utilisateurs")
@SecurityRequirement(name = "bearerAuth")
public class UserApprovalController {

    private final UserApprovalService userApprovalService;

    @Operation(
        summary = "Approuver un utilisateur",
        description = "Approuve un utilisateur en attente. Seul un SUPERADMIN peut approuver un ADMIN, " +
                     "et un ADMIN ne peut approuver que les utilisateurs de sa propre auto-école."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Utilisateur approuvé avec succès"),
        @ApiResponse(responseCode = "403", description = "Accès non autorisé - l'utilisateur n'a pas les droits nécessaires"),
        @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé"),
        @ApiResponse(responseCode = "400", description = "L'utilisateur n'est pas en attente d'approbation")
    })
    @PostMapping("/{userId}/approve")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<Void> approveUser(
            @Parameter(description = "L'utilisateur actuellement connecté", hidden = true)
            @AuthenticationPrincipal User currentUser,
            @Parameter(description = "ID de l'utilisateur à approuver")
            @PathVariable Long userId) {
        userApprovalService.approveUser(currentUser, userId);
        return ResponseEntity.ok().build();
    }

    @Operation(
        summary = "Rejeter un utilisateur",
        description = "Rejette un utilisateur en attente. Seul un SUPERADMIN peut rejeter un ADMIN, " +
                     "et un ADMIN ne peut rejeter que les utilisateurs de sa propre auto-école."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Utilisateur rejeté avec succès"),
        @ApiResponse(responseCode = "403", description = "Accès non autorisé - l'utilisateur n'a pas les droits nécessaires"),
        @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé"),
        @ApiResponse(responseCode = "400", description = "L'utilisateur n'est pas en attente d'approbation")
    })
    @PostMapping("/{userId}/reject")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<Void> rejectUser(
            @Parameter(description = "L'utilisateur actuellement connecté", hidden = true)
            @AuthenticationPrincipal User currentUser,
            @Parameter(description = "ID de l'utilisateur à rejeter")
            @PathVariable Long userId) {
        userApprovalService.rejectUser(currentUser, userId);
        return ResponseEntity.ok().build();
    }
} 