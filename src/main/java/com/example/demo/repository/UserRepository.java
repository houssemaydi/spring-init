package com.example.demo.repository;

import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
     * @return Un Optional contenant l'utilisateur s'il existe
     */
    Optional<User> findByUsername(String username);

    /**
     * Vérifie si un nom d'utilisateur existe déjà
     * @param username Le nom d'utilisateur à vérifier
     * @return true si le nom d'utilisateur existe, false sinon
     */
    boolean existsByUsername(String username);

    /**
     * Vérifie si une adresse email existe déjà
     * @param email L'adresse email à vérifier
     * @return true si l'email existe, false sinon
     */
    boolean existsByEmail(String email);
}
