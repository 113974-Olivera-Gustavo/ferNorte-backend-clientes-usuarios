package com.example.micro_a.repositories;

import com.example.micro_a.entities.ClienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<ClienteEntity, Long> {

    Optional<ClienteEntity> findByNroDoc(Long nroDoc);

    Optional<ClienteEntity> findByEmail(String email);

    Optional<ClienteEntity> findByTelefono(String telefono);

}
