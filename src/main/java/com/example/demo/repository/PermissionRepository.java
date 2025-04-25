package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.demo.model.Permission;

import java.util.Optional;

/**
 * Repository pour l'entité Permission
 * Étend JpaRepository pour hériter des méthodes CRUD standard
 */
@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    /**
     * Trouve une permission par son nom
     * @param name Le nom de la permission à rechercher
     * @return Un Optional contenant la permission si elle existe
     */
    Optional<Permission> findByName(String name);

    /**
     * Vérifie si un nom de permission existe déjà
     * @param name Le nom de la permission à vérifier
     * @return true si le nom existe, false sinon
     */
    boolean existsByName(String name);
}
