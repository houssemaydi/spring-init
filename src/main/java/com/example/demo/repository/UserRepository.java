package com.example.demo.repository;

import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité User
 * Étend JpaRepository pour hériter des méthodes CRUD standard
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Trouve un utilisateur par son nom d'utilisateur
     * @param username Le nom d'utilisateur à rechercher
     * @return Un Optional contenant l'utilisateur si trouvé
     */
    Optional<User> findByUsername(String username);

    /**
     * Vérifie si un nom d'utilisateur existe déjà
     * @param username Le nom d'utilisateur à vérifier
     * @return true si le nom d'utilisateur existe déjà
     */
    boolean existsByUsername(String username);

    /**
     * Trouve un utilisateur par son email
     * @param email L'email à rechercher
     * @return Un Optional contenant l'utilisateur si trouvé
     */
    Optional<User> findByEmail(String email);

    /**
     * Vérifie si un email existe déjà
     * @param email L'email à vérifier
     * @return true si l'email existe déjà
     */
    boolean existsByEmail(String email);

    /**
     * Récupère tous les utilisateurs
     * @return La liste des utilisateurs
     */
    @Override
    List<User> findAll();

    /**
     * Supprime un utilisateur
     * @param id L'ID de l'utilisateur à supprimer
     */
    @Override
    void deleteById(Long id);

    /**
     * Enregistre un utilisateur
     * @param user L'utilisateur à enregistrer
     * @return L'utilisateur enregistré
     */
    @Override
    <S extends User> S save(S user);
}
