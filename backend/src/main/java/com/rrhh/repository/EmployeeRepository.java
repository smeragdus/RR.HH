package com.rrhh.repository;

import com.rrhh.model.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByNumeroEmpleado(String numeroEmpleado);

    Optional<Employee> findByCurp(String curp);

    Optional<Employee> findByRfc(String rfc);

    Optional<Employee> findByNss(String nss);

    Optional<Employee> findByUsuarioId(Long usuarioId);

    boolean existsByNumeroEmpleado(String numeroEmpleado);

    boolean existsByCurp(String curp);

    boolean existsByRfc(String rfc);

    boolean existsByNss(String nss);

    Page<Employee> findByActivoTrue(Pageable pageable);

    Page<Employee> findByDepartamentoAndActivoTrue(String departamento, Pageable pageable);

    List<Employee> findBySupervisorId(Long supervisorId);

    @Query("SELECT e FROM Employee e WHERE e.activo = true AND " +
           "(LOWER(e.nombres) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(e.apellidoPaterno) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(e.apellidoMaterno) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "e.numeroEmpleado LIKE CONCAT('%', :search, '%'))")
    Page<Employee> searchEmployees(@Param("search") String search, Pageable pageable);
}
