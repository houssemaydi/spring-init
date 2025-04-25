package com.example.demo.repository;

import com.example.demo.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository pour l'entité Role
 * Étend JpaRepository pour hériter des méthodes CRUD standard
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Trouve un rôle par son nom
     * @param name Le nom du rôle à rechercher
     * @return Un Optional contenant le rôle s'il existe
     */
    Optional<Role> findByName(String name);

    /**
     * Vérifie si un nom de rôle existe déjà
     * @param name Le nom du rôle à vérifier
     * @return true si le nom existe, false sinon
     */
    boolean existsByName(String name);
}
