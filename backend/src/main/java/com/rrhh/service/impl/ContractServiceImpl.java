package com.rrhh.service.impl;

import com.rrhh.dto.request.CreateContractRequest;
import com.rrhh.dto.response.ContractResponse;
import com.rrhh.exception.BadRequestException;
import com.rrhh.exception.ResourceNotFoundException;
import com.rrhh.model.entity.Contract;
import com.rrhh.model.entity.Employee;
import com.rrhh.repository.ContractRepository;
import com.rrhh.repository.EmployeeRepository;
import com.rrhh.service.ContractService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class ContractServiceImpl implements ContractService {

    private final ContractRepository contractRepository;
    private final EmployeeRepository employeeRepository;

    public ContractServiceImpl(ContractRepository contractRepository, EmployeeRepository employeeRepository) {
        this.contractRepository = contractRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ContractResponse> getAllContracts(Pageable pageable) {
        return contractRepository.findByActivoTrue(pageable).map(ContractResponse::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ContractResponse> getContractsByEmpleadoId(Long empleadoId, Pageable pageable) {
        return contractRepository.findByEmpleadoId(empleadoId, pageable).map(ContractResponse::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public ContractResponse getContractById(Long id) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contrato", "id", id));
        return ContractResponse.fromEntity(contract);
    }

    @Override
    @Transactional
    public ContractResponse createContract(CreateContractRequest request) {
        Employee employee = employeeRepository.findById(request.getEmpleadoId())
                .orElseThrow(() -> new ResourceNotFoundException("Empleado", "id", request.getEmpleadoId()));

        if (request.getNumeroContrato() != null && contractRepository.existsByNumeroContrato(request.getNumeroContrato())) {
            throw new BadRequestException("El número de contrato ya existe");
        }

        Contract contract = Contract.builder()
                .empleado(employee)
                .numeroContrato(generateNumeroContrato())
                .tipo(request.getTipo())
                .fechaInicio(request.getFechaInicio())
                .fechaFin(request.getFechaFin())
                .salario(request.getSalario())
                .puesto(request.getPuesto())
                .departamento(request.getDepartamento())
                .jornada(request.getJornada())
                .prestaciones(request.getPrestaciones())
                .observaciones(request.getObservaciones())
                .activo(true)
                .build();

        // Calcular salario diario si no se proporcionó
        if (request.getSalarioDiario() == null && request.getSalario() != null) {
            contract.setSalarioDiario(request.getSalario().divide(BigDecimal.valueOf(30), 2, java.math.RoundingMode.HALF_UP));
        } else {
            contract.setSalarioDiario(request.getSalarioDiario());
        }

        // Calcular SBC IMSS (Base de cotización)
        if (contract.getSbcImss() == null && contract.getSalarioDiario() != null) {
            contract.setSbcImss(contract.getSalarioDiario().multiply(BigDecimal.valueOf(1.10)));
        }

        return ContractResponse.fromEntity(contractRepository.save(contract));
    }

    @Override
    @Transactional
    public ContractResponse updateContract(Long id, CreateContractRequest request) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contrato", "id", id));

        contract.setTipo(request.getTipo());
        contract.setFechaInicio(request.getFechaInicio());
        contract.setFechaFin(request.getFechaFin());
        contract.setSalario(request.getSalario());
        contract.setPuesto(request.getPuesto());
        contract.setDepartamento(request.getDepartamento());
        contract.setJornada(request.getJornada());
        contract.setPrestaciones(request.getPrestaciones());
        contract.setObservaciones(request.getObservaciones());

        if (request.getSalarioDiario() != null) {
            contract.setSalarioDiario(request.getSalarioDiario());
        }

        if (request.getSbcImss() != null) {
            contract.setSbcImss(request.getSbcImss());
        }

        return ContractResponse.fromEntity(contractRepository.save(contract));
    }

    @Override
    @Transactional
    public void deactivateContract(Long id) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contrato", "id", id));
        contract.setActivo(false);
        contractRepository.save(contract);
    }

    private String generateNumeroContrato() {
        return "CTR-" + System.currentTimeMillis() % 100000;
    }
}
