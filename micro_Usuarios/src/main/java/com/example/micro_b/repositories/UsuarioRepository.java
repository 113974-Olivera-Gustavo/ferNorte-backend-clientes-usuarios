package com.example.micro_b.repositories;

import com.example.micro_b.entities.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {
    Optional<UsuarioEntity> findByUsername(String username);
    Optional<UsuarioEntity> findByEmail(String email);
    List<UsuarioEntity> findByCargo_DescripcionIgnoreCase(String descripcion);
    Optional<UsuarioEntity> findByNumeroDocumento(String numeroDocumento);

}

