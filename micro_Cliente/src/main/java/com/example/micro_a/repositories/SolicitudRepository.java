package com.example.micro_a.repositories;

import com.example.micro_a.entities.ClienteEntity;
import com.example.micro_a.entities.SolicitudEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolicitudRepository extends JpaRepository<SolicitudEntity, Long> {
    @Query("select s from SolicitudEntity s where s.atendido = true")
    List<SolicitudEntity> findByAtendido();
    @Query("select s from SolicitudEntity s where s.atendido = false")
    List<SolicitudEntity> findByRegistrado();

}
