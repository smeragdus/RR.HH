package com.rrhh.controller;

import com.rrhh.dto.request.CreateEmployeeRequest;
import com.rrhh.dto.response.ApiResponse;
import com.rrhh.dto.response.AttendanceResponse;
import com.rrhh.dto.response.ContractResponse;
import com.rrhh.dto.response.EmployeeResponse;
import com.rrhh.dto.response.RequestResponse;
import com.rrhh.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RRHH', 'JEFE', 'EMPLEADO')")
    public ResponseEntity<ApiResponse<Page<EmployeeResponse>>> getAllEmployees(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<EmployeeResponse> employees;
        if (search != null && !search.isEmpty()) {
            employees = employeeService.searchEmployees(search, pageable);
        } else {
            employees = employeeService.getAllEmployees(pageable);
        }
        return ResponseEntity.ok(ApiResponse.success(employees));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RRHH', 'JEFE', 'EMPLEADO')")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getEmployeeById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(employeeService.getEmployeeById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RRHH')")
    public ResponseEntity<ApiResponse<EmployeeResponse>> createEmployee(
            @Valid @RequestBody CreateEmployeeRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Empleado creado", employeeService.createEmployee(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RRHH')")
    public ResponseEntity<ApiResponse<EmployeeResponse>> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody CreateEmployeeRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Empleado actualizado", employeeService.updateEmployee(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RRHH')")
    public ResponseEntity<ApiResponse<Void>> deactivateEmployee(@PathVariable Long id) {
        employeeService.deactivateEmployee(id);
        return ResponseEntity.ok(ApiResponse.success("Empleado desactivado", null));
    }

    @GetMapping("/{id}/attendance")
    @PreAuthorize("hasAnyRole('ADMIN', 'RRHH', 'JEFE', 'EMPLEADO')")
    public ResponseEntity<ApiResponse<List<AttendanceResponse>>> getEmployeeAttendance(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(employeeService.getEmployeeAttendance(id)));
    }

    @GetMapping("/{id}/requests")
    @PreAuthorize("hasAnyRole('ADMIN', 'RRHH', 'JEFE', 'EMPLEADO')")
    public ResponseEntity<ApiResponse<List<RequestResponse>>> getEmployeeRequests(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(employeeService.getEmployeeRequests(id)));
    }

    @GetMapping("/{id}/contracts")
    @PreAuthorize("hasAnyRole('ADMIN', 'RRHH', 'JEFE', 'EMPLEADO')")
    public ResponseEntity<ApiResponse<List<ContractResponse>>> getEmployeeContracts(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(employeeService.getEmployeeContracts(id)));
    }
}
