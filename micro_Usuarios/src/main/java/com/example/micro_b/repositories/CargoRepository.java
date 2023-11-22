package com.example.micro_b.repositories;

import com.example.micro_b.entities.CargoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CargoRepository extends JpaRepository<CargoEntity, Long> {
    Optional<CargoEntity> findByDescripcion(String descripcion);
}
