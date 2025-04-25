package com.example.demo.repository;

import com.example.demo.model.User;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité User
 * Étend JpaRepository pour hériter des méthodes CRUD standard
 * Utilise le cache pour améliorer les performances
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Trouve un utilisateur par son nom d'utilisateur
     * @param username Le nom d'utilisateur à rechercher
     * @return Un Optional contenant l'utilisateur s'il existe
     */
    @Cacheable(value = "userByUsername", key = "#username", unless = "#result == null")
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

    /**
     * Recherche un utilisateur par email
     * @param email L'email de l'utilisateur à rechercher
     * @return Un Optional contenant l'utilisateur s'il existe
     */
    @Cacheable(value = "userByEmail", key = "#email", unless = "#result == null")
    Optional<User> findByEmail(String email);

    /**
     * Récupère tous les utilisateurs
     * Met en cache le résultat pour améliorer les performances
     * @return La liste des utilisateurs
     */
    @Override
    @Cacheable(value = "users")
    List<User> findAll();

    /**
     * Supprime un utilisateur et invalide les caches associés
     * @param id L'ID de l'utilisateur à supprimer
     */
    @Override
    @Caching(evict = {
        @CacheEvict(value = "users", allEntries = true),
        @CacheEvict(value = "userByUsername", allEntries = true),
        @CacheEvict(value = "userByEmail", allEntries = true)
    })
    void deleteById(Long id);

    /**
     * Enregistre un utilisateur et invalide les caches associés
     * @param user L'utilisateur à enregistrer
     * @return L'utilisateur enregistré
     */
    @Override
    @Caching(evict = {
        @CacheEvict(value = "users", allEntries = true),
        @CacheEvict(value = "userByUsername", allEntries = true),
        @CacheEvict(value = "userByEmail", allEntries = true)
    })
    <S extends User> S save(S user);
}
