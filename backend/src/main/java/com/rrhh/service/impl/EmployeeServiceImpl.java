package com.rrhh.service.impl;

import com.rrhh.dto.request.CreateEmployeeRequest;
import com.rrhh.dto.response.AttendanceResponse;
import com.rrhh.dto.response.ContractResponse;
import com.rrhh.dto.response.EmployeeResponse;
import com.rrhh.dto.response.RequestResponse;
import com.rrhh.exception.ResourceNotFoundException;
import com.rrhh.model.entity.Employee;
import com.rrhh.model.entity.User;
import com.rrhh.repository.AttendanceRepository;
import com.rrhh.repository.ContractRepository;
import com.rrhh.repository.EmployeeRepository;
import com.rrhh.repository.RequestRepository;
import com.rrhh.repository.UserRepository;
import com.rrhh.service.EmployeeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final AttendanceRepository attendanceRepository;
    private final RequestRepository requestRepository;
    private final ContractRepository contractRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, UserRepository userRepository,
                              AttendanceRepository attendanceRepository, RequestRepository requestRepository,
                              ContractRepository contractRepository) {
        this.employeeRepository = employeeRepository;
        this.userRepository = userRepository;
        this.attendanceRepository = attendanceRepository;
        this.requestRepository = requestRepository;
        this.contractRepository = contractRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeResponse> getAllEmployees(Pageable pageable) {
        return employeeRepository.findByActivoTrue(pageable)
                .map(EmployeeResponse::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeResponse> searchEmployees(String search, Pageable pageable) {
        return employeeRepository.searchEmployees(search, pageable)
                .map(EmployeeResponse::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empleado", "id", id));
        return EmployeeResponse.fromEntity(employee);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeByUsuarioId(Long usuarioId) {
        Employee employee = employeeRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Empleado", "usuarioId", usuarioId));
        return EmployeeResponse.fromEntity(employee);
    }

    @Override
    @Transactional
    public EmployeeResponse createEmployee(CreateEmployeeRequest request) {
        String numeroEmpleado = generateNumeroEmpleado();

        Employee employee = Employee.builder()
                .numeroEmpleado(numeroEmpleado)
                .nombres(request.getNombres())
                .apellidoPaterno(request.getApellidoPaterno())
                .apellidoMaterno(request.getApellidoMaterno())
                .fechaNacimiento(request.getFechaNacimiento())
                .genero(request.getGenero())
                .direccion(request.getDireccion())
                .telefono(request.getTelefono())
                .emailPersonal(request.getEmailPersonal())
                .departamento(request.getDepartamento())
                .puesto(request.getPuesto())
                .tipoContrato(request.getTipoContrato())
                .salario(request.getSalario())
                .fechaIngreso(request.getFechaIngreso())
                .activo(true)
                .build();

        if (request.getUsuarioId() != null) {
            User user = userRepository.findById(request.getUsuarioId())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", request.getUsuarioId()));
            employee.setUsuario(user);
        }

        if (request.getSupervisorId() != null) {
            Employee supervisor = employeeRepository.findById(request.getSupervisorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Supervisor", "id", request.getSupervisorId()));
            employee.setSupervisor(supervisor);
        }

        return EmployeeResponse.fromEntity(employeeRepository.save(employee));
    }

    @Override
    @Transactional
    public EmployeeResponse updateEmployee(Long id, CreateEmployeeRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empleado", "id", id));

        employee.setNombres(request.getNombres());
        employee.setApellidoPaterno(request.getApellidoPaterno());
        employee.setApellidoMaterno(request.getApellidoMaterno());
        employee.setFechaNacimiento(request.getFechaNacimiento());
        employee.setGenero(request.getGenero());
        employee.setDireccion(request.getDireccion());
        employee.setTelefono(request.getTelefono());
        employee.setEmailPersonal(request.getEmailPersonal());
        employee.setDepartamento(request.getDepartamento());
        employee.setPuesto(request.getPuesto());
        employee.setTipoContrato(request.getTipoContrato());
        employee.setSalario(request.getSalario());
        employee.setFechaIngreso(request.getFechaIngreso());

        if (request.getSupervisorId() != null) {
            Employee supervisor = employeeRepository.findById(request.getSupervisorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Supervisor", "id", request.getSupervisorId()));
            employee.setSupervisor(supervisor);
        }

        return EmployeeResponse.fromEntity(employeeRepository.save(employee));
    }

    @Override
    @Transactional
    public void deactivateEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empleado", "id", id));
        employee.setActivo(false);
        employeeRepository.save(employee);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResponse> getEmployeeAttendance(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Empleado", "id", id);
        }
        return attendanceRepository.findByEmpleadoIdAndFechaBetween(
                        id,
                        LocalDate.now().minusMonths(1),
                        LocalDate.now())
                .stream()
                .map(AttendanceResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestResponse> getEmployeeRequests(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Empleado", "id", id);
        }
        return requestRepository.findByEmpleadoId(id, Pageable.unpaged())
                .stream()
                .map(RequestResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContractResponse> getEmployeeContracts(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Empleado", "id", id);
        }
        return contractRepository.findByEmpleadoIdAndActivoTrue(id)
                .stream()
                .map(ContractResponse::fromEntity)
                .collect(Collectors.toList());
    }

    private String generateNumeroEmpleado() {
        return "EMP" + System.currentTimeMillis() % 100000;
    }
}
