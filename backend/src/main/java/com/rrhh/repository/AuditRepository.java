package com.rrhh.repository;

import com.rrhh.model.entity.Audit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface AuditRepository extends JpaRepository<Audit, Long> {

    Page<Audit> findByUsuarioId(Long usuarioId, Pageable pageable);

    Page<Audit> findByEntidadAndEntidadId(String entidad, Long entidadId, Pageable pageable);

    Page<Audit> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    Page<Audit> findByAccion(String accion, Pageable pageable);
}
