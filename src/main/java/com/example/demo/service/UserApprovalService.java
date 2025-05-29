package com.example.demo.service;

import com.example.demo.model.ApprovalStatus;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserApprovalService {

    private final UserRepository userRepository;

    /**
     * Approuve un utilisateur
     * @param approver L'utilisateur qui effectue l'approbation
     * @param userId L'ID de l'utilisateur à approuver
     * @throws AccessDeniedException Si l'approbateur n'a pas les droits nécessaires
     * @throws EntityNotFoundException Si l'utilisateur n'existe pas
     */
    @Transactional
    public void approveUser(User approver, Long userId) {
        User userToApprove = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));

        validateApproval(approver, userToApprove);

        userToApprove.setApprovalStatus(ApprovalStatus.APPROVED);
        userToApprove.setApprovedBy(approver);
        userToApprove.setApprovedAt(LocalDateTime.now());
        userRepository.save(userToApprove);
    }

    /**
     * Rejette un utilisateur
     * @param approver L'utilisateur qui effectue le rejet
     * @param userId L'ID de l'utilisateur à rejeter
     * @throws AccessDeniedException Si l'approbateur n'a pas les droits nécessaires
     * @throws EntityNotFoundException Si l'utilisateur n'existe pas
     */
    @Transactional
    public void rejectUser(User approver, Long userId) {
        User userToReject = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));

        validateApproval(approver, userToReject);

        userToReject.setApprovalStatus(ApprovalStatus.REJECTED);
        userToReject.setApprovedBy(approver);
        userToReject.setApprovedAt(LocalDateTime.now());
        userRepository.save(userToReject);
    }

    /**
     * Valide si l'approbateur peut approuver l'utilisateur
     * @param approver L'utilisateur qui effectue l'approbation
     * @param userToApprove L'utilisateur à approuver
     * @throws AccessDeniedException Si l'approbateur n'a pas les droits nécessaires
     */
    private void validateApproval(User approver, User userToApprove) {
        // Vérifier si l'approbateur a le rôle nécessaire
        boolean isSuperAdmin = approver.getRoles().stream()
            .anyMatch(role -> role.getName().equals("SUPERADMIN"));
        boolean isAdmin = approver.getRoles().stream()
            .anyMatch(role -> role.getName().equals("ADMIN"));

        // Vérifier si l'utilisateur à approuver a le rôle ADMIN
        boolean isUserAdmin = userToApprove.getRoles().stream()
            .anyMatch(role -> role.getName().equals("ADMIN"));

        // Seul le SUPERADMIN peut approuver un ADMIN
        if (isUserAdmin && !isSuperAdmin) {
            throw new AccessDeniedException("Seul le SUPERADMIN peut approuver un ADMIN");
        }

        // Un ADMIN ne peut approuver que les utilisateurs de sa propre auto-école
        if (isAdmin && !isSuperAdmin) {
            if (userToApprove.getDrivingSchool() == null || 
                approver.getDrivingSchool() == null ||
                !userToApprove.getDrivingSchool().getId().equals(approver.getDrivingSchool().getId())) {
                throw new AccessDeniedException("Un ADMIN ne peut approuver que les utilisateurs de sa propre auto-école");
            }
        }

        // Vérifier si l'utilisateur n'est pas déjà approuvé ou rejeté
        if (userToApprove.getApprovalStatus() != ApprovalStatus.PENDING) {
            throw new IllegalStateException("L'utilisateur n'est pas en attente d'approbation");
        }
    }
} 