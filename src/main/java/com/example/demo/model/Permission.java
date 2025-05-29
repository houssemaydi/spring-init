package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entité représentant une permission spécifique dans le système
 * Les permissions définissent les actions qu'un utilisateur peut effectuer
 */
@Entity
@Table(name = "permissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Permission {

    /**
     * Identifiant unique de la permission, généré automatiquement
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nom de la permission, doit être unique
     * Généralement au format RESSOURCE_ACTION (ex: USER_READ, USER_WRITE)
     */
    @NotBlank(message = "Le nom de la permission ne peut pas être vide")
    @Size(min = 3, max = 50, message = "Le nom de la permission doit contenir entre 3 et 50 caractères")
    @Column(unique = true, nullable = false)
    private String name;

    /**
     * Description de la permission
     */
    @Size(max = 255, message = "La description ne peut pas dépasser 255 caractères")
    private String description;

    /**
     * Enumération de toutes les permissions disponibles dans le système
     */
    public enum PermissionType {
        // Permissions globales
        CREER_MODULE_FONCTIONNEL("Créer des modules fonctionnels"),
        ACTIVER_DESACTIVER_ECRAN("Activer/désactiver des écrans"),
        GERER_ROLES_ET_PERMISSIONS_GLOBAL("Gérer les rôles et permissions globalement"),
        GERER_MULTI_AUTO_ECOLES("Gérer plusieurs auto-écoles"),
        VOIR_TOUTES_LES_ECOLES("Voir toutes les écoles"),
        SUPPRIMER_UTILISATEURS_GLOBAUX("Supprimer des utilisateurs globaux"),
        GESTION_LANGUES_ET_TEMPLATES("Gérer les langues et templates"),
        VOIR_JOURNAUX_SYSTEME("Voir les journaux système"),

        // Permissions véhicules
        VOIR_VEHICULES("Voir les véhicules"),
        AJOUTER_VEHICULE("Ajouter un véhicule"),
        MODIFIER_VEHICULE("Modifier un véhicule"),
        SUPPRIMER_VEHICULE("Supprimer un véhicule"),
        VOIR_DOCUMENTS_VEHICULE("Voir les documents du véhicule"),
        GERER_DOCUMENTS_VEHICULE("Gérer les documents du véhicule"),
        VOIR_MAINTENANCE("Voir la maintenance"),
        GERER_MAINTENANCE("Gérer la maintenance"),

        // Permissions séances
        VOIR_SEANCES("Voir les séances"),
        CREER_SEANCE("Créer une séance"),
        MODIFIER_SEANCE("Modifier une séance"),
        SUPPRIMER_SEANCE("Supprimer une séance"),
        APPROUVER_SEANCE("Approuver une séance"),
        REJETER_SEANCE("Rejeter une séance"),
        VOIR_SEANCES_MONITEUR("Voir les séances du moniteur"),
        VOIR_SEANCES_PERSO("Voir ses séances personnelles"),
        RESERVER_SEANCE("Réserver une séance"),

        // Permissions utilisateurs
        VOIR_UTILISATEURS("Voir les utilisateurs"),
        CREER_UTILISATEUR("Créer un utilisateur"),
        MODIFIER_UTILISATEUR("Modifier un utilisateur"),
        SUPPRIMER_UTILISATEUR("Supprimer un utilisateur"),
        ATTRIBUER_ROLE("Attribuer un rôle"),
        VOIR_PROFIL_PERSO("Voir son profil personnel"),
        MODIFIER_PROFIL_PERSO("Modifier son profil personnel"),

        // Permissions candidats
        VOIR_CANDIDATS("Voir les candidats"),
        MODIFIER_CANDIDAT("Modifier un candidat"),
        GERER_ACCÈS_GL("Gérer l'accès GL"),
        DEFINIR_PRIX_PERSONNALISÉ("Définir un prix personnalisé"),

        // Permissions paiements
        VOIR_PAIEMENTS("Voir les paiements"),
        AJOUTER_PAIEMENT("Ajouter un paiement"),
        SUPPRIMER_PAIEMENT("Supprimer un paiement"),
        EXPORTER_FACTURES("Exporter des factures"),

        // Permissions auto-école
        VOIR_ECOLE("Voir l'auto-école"),
        MODIFIER_INFOS_ECOLE("Modifier les informations de l'auto-école"),
        DEFINIR_PRIX_HORAIRE_GLOBAUX("Définir les prix horaires globaux"),
        GERER_LANGUES("Gérer les langues"),
        VOIR_LIEUX_RENDEZVOUS("Voir les lieux de rendez-vous"),
        GERER_LIEUX_RENDEZVOUS("Gérer les lieux de rendez-vous"),

        // Permissions rôles
        VOIR_ROLES("Voir les rôles"),
        CREER_ROLE("Créer un rôle"),
        MODIFIER_ROLE("Modifier un rôle"),
        SUPPRIMER_ROLE("Supprimer un rôle"),
        ASSIGNER_PERMISSIONS("Assigner des permissions");

        private final String description;

        PermissionType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
