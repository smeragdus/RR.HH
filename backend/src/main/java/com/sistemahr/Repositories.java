package com.sistemahr;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

interface UserRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByEmail(String email);
    boolean existsByEmail(String email);
    List<UserAccount> findByEmployeeId(Long employeeId);
}

interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByDni(String dni);
    boolean existsByDni(String dni);
    boolean existsByDniAndIdNot(String dni, Long id);
    List<Employee> findByEmploymentStatus(EmploymentStatus status);
    List<Employee> findByAreaContainingIgnoreCaseAndLocationContainingIgnoreCase(String area, String location);
}

interface DocumentRepository extends JpaRepository<EmployeeDocument, Long> {
    List<EmployeeDocument> findByEmployeeId(Long employeeId);
    List<EmployeeDocument> findByRequestId(Long requestId);
    List<EmployeeDocument> findByContractId(Long contractId);
}

interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    Optional<Attendance> findByEmployeeIdAndWorkDate(Long employeeId, LocalDate workDate);
    List<Attendance> findByEmployeeIdAndWorkDateBetween(Long employeeId, LocalDate from, LocalDate to);
    List<Attendance> findByWorkDateBetween(LocalDate from, LocalDate to);
    List<Attendance> findByEmployeeAreaContainingIgnoreCaseAndWorkDateBetween(String area, LocalDate from, LocalDate to);
}

interface RequestRepository extends JpaRepository<AbsenceRequest, Long> {
    List<AbsenceRequest> findByEmployeeId(Long employeeId);
    List<AbsenceRequest> findByEmployeeAreaContainingIgnoreCaseAndStatus(String area, RequestStatus status);
    List<AbsenceRequest> findByStatus(RequestStatus status);
    List<AbsenceRequest> findByTypeAndStatus(RequestType type, RequestStatus status);
}

interface ContractRepository extends JpaRepository<LaborContract, Long> {
    List<LaborContract> findByEmployeeId(Long employeeId);
}

interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findTop200ByOrderByOccurredAtDesc();
    List<AuditLog> findTop200ByActorEmailContainingIgnoreCaseOrActorNameContainingIgnoreCaseOrActorDniContainingIgnoreCaseOrActorPositionContainingIgnoreCaseOrderByOccurredAtDesc(String email, String name, String dni, String position);
}

interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientIdOrderByCreatedAtDesc(Long recipientId);
}

interface SystemConfigRepository extends JpaRepository<SystemConfig, Long> {
    Optional<SystemConfig> findByConfigKey(String configKey);
}
