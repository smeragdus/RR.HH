package com.rrhh.service;

import com.rrhh.dto.request.CreateEmployeeRequest;
import com.rrhh.dto.response.AttendanceResponse;
import com.rrhh.dto.response.EmployeeResponse;
import com.rrhh.dto.response.RequestResponse;
import com.rrhh.dto.response.ContractResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EmployeeService {
    Page<EmployeeResponse> getAllEmployees(Pageable pageable);
    Page<EmployeeResponse> searchEmployees(String search, Pageable pageable);
    EmployeeResponse getEmployeeById(Long id);
    EmployeeResponse getEmployeeByUsuarioId(Long usuarioId);
    EmployeeResponse createEmployee(CreateEmployeeRequest request);
    EmployeeResponse updateEmployee(Long id, CreateEmployeeRequest request);
    void deactivateEmployee(Long id);
    List<AttendanceResponse> getEmployeeAttendance(Long id);
    List<RequestResponse> getEmployeeRequests(Long id);
    List<ContractResponse> getEmployeeContracts(Long id);
}
