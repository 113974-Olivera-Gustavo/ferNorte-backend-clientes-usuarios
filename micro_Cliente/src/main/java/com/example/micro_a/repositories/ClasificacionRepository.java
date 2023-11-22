package com.example.micro_a.repositories;

import com.example.micro_a.entities.ClasificacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClasificacionRepository extends JpaRepository<ClasificacionEntity, Long> {
    ClasificacionEntity findByDescripcion(String descripcion);
}
