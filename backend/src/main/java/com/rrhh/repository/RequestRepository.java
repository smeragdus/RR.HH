package com.rrhh.repository;

import com.rrhh.model.entity.Request;
import com.rrhh.model.enums.RequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    Page<Request> findByEmpleadoId(Long empleadoId, Pageable pageable);

    Page<Request> findByEstado(RequestStatus estado, Pageable pageable);

    Page<Request> findByEmpleadoIdAndEstado(Long empleadoId, RequestStatus estado, Pageable pageable);

    List<Request> findByFechaInicioBetweenAndEmpleadoId(LocalDate startDate, LocalDate endDate, Long empleadoId);

    @Query("SELECT r FROM Request r WHERE r.estado = :status AND r.fechaInicio <= :date")
    Page<Request> findActiveRequests(@Param("status") RequestStatus status, @Param("date") LocalDate date, Pageable pageable);

    @Query("SELECT COUNT(r) FROM Request r WHERE r.empleado.id = :empleadoId AND r.estado = :status")
    long countByEmpleadoIdAndEstado(@Param("empleadoId") Long empleadoId, @Param("status") RequestStatus status);
}
