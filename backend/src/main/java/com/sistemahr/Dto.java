package com.sistemahr;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.math.BigDecimal;

record LoginRequest(@Email @NotBlank String email, @NotBlank @Size(max = 128) String password) {}
record AuthenticatedLogin(String token, UserDto user) {}
record LoginResponse(UserDto user) {}
record CsrfResponse(String token) {}
record UserDto(Long id, String email, Role role, AccountStatus status, Long employeeId, String employeeName) {}
record UserRequest(@Email @NotBlank String email, @Size(min = 12, max = 128) String password, @NotNull Role role, AccountStatus status, Long employeeId) {}

record EmployeeDto(
        Long id,
        String firstName,
        String lastName,
        String dni,
        String phone,
        String email,
        String position,
        String area,
        String location,
        EmploymentStatus employmentStatus,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String updatedBy
) {}

record EmployeeRequest(
        @NotBlank @Size(max = 80) String firstName,
        @NotBlank @Size(max = 80) String lastName,
        @NotBlank @Size(min = 8, max = 8) String dni,
        @Size(max = 20) String phone,
        @Email @NotBlank String email,
        @Size(max = 120) String position,
        @Size(max = 120) String area,
        @Size(max = 120) String location,
        EmploymentStatus employmentStatus
) {}

record DocumentDto(Long id, String originalName, String contentType, Long size, String uploadedBy, LocalDateTime uploadedAt) {}
record AttendanceDto(Long id, Long employeeId, String employeeName, String dni, String area, LocalDate workDate, LocalTime checkIn, LocalTime checkOut, BigDecimal hoursWorked, String importedPosition, AttendanceStatus status, boolean late, String justification) {}
record AttendanceJustificationRequest(@NotBlank String justification) {}
record AbsenceRequestDto(Long id, Long employeeId, String employeeName, String area, RequestType type, LocalDate startDate, LocalDate endDate, String reason, RequestStatus status, String rejectionReason, LocalDateTime registeredAt, LocalDateTime reviewedAt, String reviewedBy) {}
record AbsenceRequestCreate(@NotNull RequestType type, @NotNull LocalDate startDate, @NotNull LocalDate endDate, @NotBlank @Size(max = 1000) String reason, Long employeeId) {}
record RejectRequest(@NotBlank @Size(max = 1000) String reason) {}
record ContractDto(Long id, Long employeeId, String employeeName, String contractType, LocalDate startDate, LocalDate endDate, ContractStatus status) {}
record ContractRequest(@NotNull Long employeeId, @NotBlank @Size(max = 120) String contractType, @NotNull LocalDate startDate, LocalDate endDate, ContractStatus status) {}
record AuditDto(Long id, Long userId, String actorEmail, String actorName, String actorDni, String actorPosition, String action, String module, LocalDateTime occurredAt, String affectedRecord, String description) {}
record NotificationDto(Long id, String message, boolean read, LocalDateTime createdAt) {}
record ErrorResponse(String message, LocalDateTime timestamp) {}
