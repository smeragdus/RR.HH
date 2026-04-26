package com.rrhh.repository;

import com.rrhh.model.entity.Contract;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {

    Optional<Contract> findByNumeroContrato(String numeroContrato);

    Page<Contract> findByEmpleadoId(Long empleadoId, Pageable pageable);

    Page<Contract> findByActivoTrue(Pageable pageable);

    Optional<Contract> findByEmpleadoIdAndActivoTrue(Long empleadoId);

    boolean existsByNumeroContrato(String numeroContrato);
}
