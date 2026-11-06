package com.TiendaApp.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.TiendaApp.demo.entity.Roles;

public interface RolesRepository extends JpaRepository<Roles, Long> {
    
}