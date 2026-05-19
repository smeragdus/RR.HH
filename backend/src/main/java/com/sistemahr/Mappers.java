package com.sistemahr;

final class Mappers {
    private Mappers() {}

    static UserDto user(UserAccount user) {
        Employee employee = user.getEmployee();
        return new UserDto(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                user.getStatus(),
                employee == null ? null : employee.getId(),
                employee == null ? null : employeeName(employee)
        );
    }

    static EmployeeDto employee(Employee employee) {
        return new EmployeeDto(
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getDni(),
                employee.getPhone(),
                employee.getEmail(),
                employee.getPosition(),
                employee.getArea(),
                employee.getLocation(),
                employee.getEmploymentStatus(),
                employee.getCreatedAt(),
                employee.getUpdatedAt(),
                employee.getUpdatedBy()
        );
    }

    static DocumentDto document(EmployeeDocument document) {
        return new DocumentDto(
                document.getId(),
                document.getOriginalName(),
                document.getContentType(),
                document.getSize(),
                document.getUploadedBy(),
                document.getUploadedAt()
        );
    }

    static AttendanceDto attendance(Attendance attendance) {
        Employee employee = attendance.getEmployee();
        return new AttendanceDto(
                attendance.getId(),
                employee.getId(),
                employeeName(employee),
                employee.getDni(),
                employee.getArea(),
                attendance.getWorkDate(),
                attendance.getCheckIn(),
                attendance.getCheckOut(),
                attendance.getHoursWorked(),
                attendance.getImportedPosition(),
                attendance.getStatus(),
                attendance.isLate(),
                attendance.getJustification()
        );
    }

    static AbsenceRequestDto request(AbsenceRequest request) {
        Employee employee = request.getEmployee();
        return new AbsenceRequestDto(
                request.getId(),
                employee.getId(),
                employeeName(employee),
                employee.getArea(),
                request.getType(),
                request.getStartDate(),
                request.getEndDate(),
                request.getReason(),
                request.getStatus(),
                request.getRejectionReason(),
                request.getRegisteredAt(),
                request.getReviewedAt(),
                request.getReviewedBy()
        );
    }

    static ContractDto contract(LaborContract contract) {
        Employee employee = contract.getEmployee();
        return new ContractDto(
                contract.getId(),
                employee.getId(),
                employeeName(employee),
                contract.getContractType(),
                contract.getStartDate(),
                contract.getEndDate(),
                contract.getStatus()
        );
    }

    static AuditDto audit(AuditLog audit) {
        return new AuditDto(audit.getId(), audit.getUserId(), audit.getActorEmail(), audit.getActorName(), audit.getActorDni(), audit.getActorPosition(), audit.getAction(), audit.getModule(), audit.getOccurredAt(), audit.getAffectedRecord(), audit.getDescription());
    }

    static NotificationDto notification(Notification notification) {
        return new NotificationDto(notification.getId(), notification.getMessage(), notification.isRead(), notification.getCreatedAt());
    }

    static String employeeName(Employee employee) {
        return employee.getFirstName() + " " + employee.getLastName();
    }
}
