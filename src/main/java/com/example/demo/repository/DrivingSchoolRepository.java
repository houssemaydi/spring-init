package com.example.demo.repository;

import com.example.demo.model.DrivingSchool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository pour l'entité DrivingSchool
 * Étend JpaRepository pour hériter des méthodes CRUD standard
 */
@Repository
public interface DrivingSchoolRepository extends JpaRepository<DrivingSchool, Long> {
} 