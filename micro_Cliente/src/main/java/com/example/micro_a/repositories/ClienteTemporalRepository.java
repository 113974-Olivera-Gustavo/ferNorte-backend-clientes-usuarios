package com.example.micro_a.repositories;

import com.example.micro_a.entities.ClienteTemporalEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteTemporalRepository extends JpaRepository<ClienteTemporalEntity, Long> {
}
