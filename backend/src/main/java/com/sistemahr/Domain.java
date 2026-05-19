package com.sistemahr;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

enum Role {
    ADMIN, RRHH, JEFE, EMPLEADO
}

enum AccountStatus {
    ACTIVO, INACTIVO
}

enum EmploymentStatus {
    ACTIVO, INACTIVO
}

enum AttendanceStatus {
    PRESENTE, TARDANZA, INASISTENCIA, JUSTIFICADA
}

enum RequestType {
    PERMISO, LICENCIA, VACACIONES, JUSTIFICACION
}

enum RequestStatus {
    PENDIENTE, APROBADA, RECHAZADA
}

enum ContractStatus {
    VIGENTE, VENCIDO, FINALIZADO
}

@Getter
@Setter
@Entity
@Table(name = "users")
class UserAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status = AccountStatus.ACTIVO;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_id")
    private Employee employee;
}

@Getter
@Setter
@Entity
@Table(name = "employees", uniqueConstraints = @UniqueConstraint(name = "uk_employee_dni", columnNames = "dni"))
class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String dni;

    private String phone;

    @Column(nullable = false)
    private String email;

    private String position;
    private String area;
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmploymentStatus employmentStatus = EmploymentStatus.ACTIVO;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;
    private String updatedBy;
}

@Getter
@Setter
@Entity
@Table(name = "documents")
class EmployeeDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    private AbsenceRequest request;

    @ManyToOne(fetch = FetchType.LAZY)
    private LaborContract contract;

    @Column(nullable = false)
    private String originalName;

    @Column(nullable = false)
    private String storedName;

    @Column(nullable = false)
    private String path;

    private String contentType;
    private Long size;
    private String uploadedBy;
    private LocalDateTime uploadedAt = LocalDateTime.now();
}

@Getter
@Setter
@Entity
@Table(name = "attendance", uniqueConstraints = @UniqueConstraint(name = "uk_attendance_employee_date", columnNames = {"employee_id", "work_date"}))
class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "work_date", nullable = false)
    private LocalDate workDate;

    private LocalTime checkIn;
    private LocalTime checkOut;
    private BigDecimal hoursWorked;
    private String importedPosition;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttendanceStatus status;

    @Column(nullable = false)
    private boolean late;

    private String justification;

    @ManyToOne(fetch = FetchType.LAZY)
    private EmployeeDocument justificationDocument;
}

@Getter
@Setter
@Entity
@Table(name = "absence_requests")
class AbsenceRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestType type;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false, length = 1000)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status = RequestStatus.PENDIENTE;

    private String rejectionReason;
    private LocalDateTime registeredAt = LocalDateTime.now();
    private LocalDateTime reviewedAt;
    private String reviewedBy;
}

@Getter
@Setter
@Entity
@Table(name = "contracts")
class LaborContract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(nullable = false)
    private String contractType;

    @Column(nullable = false)
    private LocalDate startDate;

    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContractStatus status = ContractStatus.VIGENTE;
}

@Getter
@Setter
@Entity
@Table(name = "audit_logs")
class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @Column(nullable = false)
    private String actorEmail;

    @Column(nullable = false)
    private String action;

    @Column(nullable = false)
    private String module;

    @Column(nullable = false)
    private LocalDateTime occurredAt = LocalDateTime.now();

    private String affectedRecord;
    private String actorName;
    private String actorDni;
    private String actorPosition;

    @Column(length = 1000)
    private String description;
}

@Getter
@Setter
@Entity
@Table(name = "notifications")
class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserAccount recipient;

    @Column(nullable = false)
    private String message;

    private boolean read;
    private LocalDateTime createdAt = LocalDateTime.now();
}

@Getter
@Setter
@Entity
@Table(name = "system_config")
class SystemConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String configKey;

    @Column(nullable = false)
    private String configValue;
}
