package com.rrhh.repository;

import com.rrhh.model.entity.Attendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findByEmpleadoIdAndFecha(Long empleadoId, LocalDate fecha);

    List<Attendance> findByEmpleadoIdAndFechaBetween(Long empleadoId, LocalDate startDate, LocalDate endDate);

    Page<Attendance> findByFecha(LocalDate fecha, Pageable pageable);

    Page<Attendance> findByEmpleadoId(Long empleadoId, Pageable pageable);

    @Query("SELECT a FROM Attendance a WHERE a.fecha BETWEEN :startDate AND :endDate")
    Page<Attendance> findByDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.empleado.id = :empleadoId AND a.fecha = :fecha AND a.horaEntrada IS NOT NULL")
    long countCheckInsForDate(@Param("empleadoId") Long empleadoId, @Param("fecha") LocalDate fecha);

    boolean existsByEmpleadoIdAndFecha(Long empleadoId, LocalDate fecha);
}
