package com.example.micro_a.repositories;

import com.example.micro_a.entities.TipoClienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoClienteRepository extends JpaRepository<TipoClienteEntity, Long> {
}
