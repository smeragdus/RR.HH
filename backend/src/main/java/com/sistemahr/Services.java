package com.sistemahr;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.persistence.EntityNotFoundException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

class BusinessException extends RuntimeException {
    BusinessException(String message) {
        super(message);
    }
}

@Service
@RequiredArgsConstructor
class CurrentUserService {
    private final UserRepository users;

    UserAccount account() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return users.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("Usuario autenticado no encontrado"));
    }

    boolean has(Role role) {
        return account().getRole() == role;
    }
}

@Service
@RequiredArgsConstructor
class AuditService {
    private final AuditLogRepository audits;
    private final UserRepository users;

    void log(String action, String module, String affectedRecord, String description) {
        String email = SecurityContextHolder.getContext().getAuthentication() == null ? "system" : SecurityContextHolder.getContext().getAuthentication().getName();
        UserAccount actor = users.findByEmail(email).orElse(null);
        AuditLog audit = new AuditLog();
        audit.setUserId(actor == null ? null : actor.getId());
        audit.setActorEmail(email);
        if (actor != null && actor.getEmployee() != null) {
            Employee employee = actor.getEmployee();
            audit.setActorName(Mappers.employeeName(employee));
            audit.setActorDni(employee.getDni());
            audit.setActorPosition(employee.getPosition());
        }
        audit.setAction(action);
        audit.setModule(module);
        audit.setAffectedRecord(affectedRecord);
        audit.setDescription(description);
        audits.save(audit);
    }
}

@Service
@RequiredArgsConstructor
class AuthService {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    AuthenticatedLogin login(LoginRequest request) {
        UserAccount user = users.findByEmail(request.email()).orElseThrow(() -> new BusinessException("Credenciales incorrectas"));
        if (user.getStatus() != AccountStatus.ACTIVO || (user.getEmployee() != null && user.getEmployee().getEmploymentStatus() == EmploymentStatus.INACTIVO)) {
            throw new BusinessException("Usuario inactivo");
        }
        if (!encoder.matches(request.password(), user.getPassword())) {
            throw new BusinessException("Credenciales incorrectas");
        }
        return new AuthenticatedLogin(jwt.generate(user), Mappers.user(user));
    }
}

@Service
@RequiredArgsConstructor
class UserService {
    private final UserRepository users;
    private final EmployeeRepository employees;
    private final PasswordEncoder encoder;
    private final AuditService audit;

    List<UserDto> list() {
        return users.findAll().stream().map(Mappers::user).toList();
    }

    @Transactional
    UserDto save(Long id, UserRequest request) {
        UserAccount user = id == null ? new UserAccount() : users.findById(id).orElseThrow();
        if (id == null && users.existsByEmail(request.email())) {
            throw new BusinessException("El correo ya existe");
        }
        user.setEmail(request.email());
        if (request.password() != null && !request.password().isBlank()) {
            user.setPassword(encoder.encode(request.password()));
        } else if (id == null) {
            throw new BusinessException("La contraseña es obligatoria");
        }
        user.setRole(request.role());
        user.setStatus(request.status() == null ? AccountStatus.ACTIVO : request.status());
        user.setEmployee(request.employeeId() == null ? null : employees.findById(request.employeeId()).orElseThrow());
        users.save(user);
        audit.log(id == null ? "CREATE" : "UPDATE", "USUARIOS", String.valueOf(user.getId()), "Usuario actualizado");
        return Mappers.user(user);
    }
}

@Service
@RequiredArgsConstructor
class EmployeeService {
    private final EmployeeRepository employees;
    private final UserRepository users;
    private final AuditService audit;
    private final CurrentUserService current;

    List<EmployeeDto> list(String area, String location) {
        if (area != null && location != null) {
            return employees.findByAreaContainingIgnoreCaseAndLocationContainingIgnoreCase(area, location).stream().map(Mappers::employee).toList();
        }
        return employees.findAll().stream().map(Mappers::employee).toList();
    }

    List<EmployeeDto> active(String area, String location) {
        return employees.findByEmploymentStatus(EmploymentStatus.ACTIVO).stream()
                .filter(e -> area == null || e.getArea() != null && e.getArea().equalsIgnoreCase(area))
                .filter(e -> location == null || e.getLocation() != null && e.getLocation().equalsIgnoreCase(location))
                .map(Mappers::employee)
                .toList();
    }

    @Transactional
    EmployeeDto save(Long id, EmployeeRequest request) {
        Employee employee = id == null ? new Employee() : employees.findById(id).orElseThrow();
        validateEmployeeIdentity(request);
        if ((id == null && employees.existsByDni(request.dni())) || (id != null && employees.existsByDniAndIdNot(request.dni(), id))) {
            throw new BusinessException("Ya existe un empleado con el mismo DNI");
        }
        employee.setFirstName(request.firstName());
        employee.setLastName(request.lastName());
        employee.setDni(request.dni());
        employee.setPhone(request.phone());
        employee.setEmail(request.email());
        employee.setPosition(request.position());
        employee.setArea(request.area());
        employee.setLocation(request.location());
        employee.setEmploymentStatus(request.employmentStatus() == null ? EmploymentStatus.ACTIVO : request.employmentStatus());
        employee.setUpdatedAt(LocalDateTime.now());
        employee.setUpdatedBy(current.account().getEmail());
        employees.save(employee);
        audit.log(id == null ? "CREATE" : "UPDATE", "EMPLEADOS", String.valueOf(employee.getId()), "Empleado actualizado");
        return Mappers.employee(employee);
    }

    private void validateEmployeeIdentity(EmployeeRequest request) {
        if (request.dni() == null || !request.dni().matches("\\d{8}")) {
            throw new BusinessException("El DNI debe tener exactamente 8 dígitos");
        }
        if (request.phone() != null && !request.phone().isBlank() && !request.phone().matches("9\\d{8}")) {
            throw new BusinessException("El teléfono debe empezar por 9 y tener 9 dígitos");
        }
    }

    @Transactional
    EmployeeDto deactivate(Long id) {
        Employee employee = employees.findById(id).orElseThrow();
        employee.setEmploymentStatus(EmploymentStatus.INACTIVO);
        employee.setUpdatedAt(LocalDateTime.now());
        employee.setUpdatedBy(current.account().getEmail());
        users.findByEmployeeId(id).forEach(user -> user.setStatus(AccountStatus.INACTIVO));
        audit.log("DEACTIVATE", "EMPLEADOS", String.valueOf(id), "Baja lógica de empleado");
        return Mappers.employee(employee);
    }

    @Transactional
    EmployeeDto reinstate(Long id) {
        Employee employee = employees.findById(id).orElseThrow();
        employee.setEmploymentStatus(EmploymentStatus.ACTIVO);
        employee.setUpdatedAt(LocalDateTime.now());
        employee.setUpdatedBy(current.account().getEmail());
        users.findByEmployeeId(id).forEach(user -> user.setStatus(AccountStatus.ACTIVO));
        audit.log("REINSTATE", "EMPLEADOS", String.valueOf(id), "Reincorporación de empleado");
        return Mappers.employee(employee);
    }
}

@Service
@RequiredArgsConstructor
class DocumentService {
    private final DocumentRepository documents;
    private final EmployeeRepository employees;
    private final RequestRepository requests;
    private final ContractRepository contracts;
    private final CurrentUserService current;
    private final AppProperties props;

    @Transactional
    DocumentDto uploadForEmployee(Long employeeId, MultipartFile file) throws IOException {
        EmployeeDocument doc = store(file);
        doc.setEmployee(employees.findById(employeeId).orElseThrow());
        return Mappers.document(documents.save(doc));
    }

    @Transactional
    EmployeeDocument uploadForRequest(Long requestId, MultipartFile file) throws IOException {
        EmployeeDocument doc = store(file);
        doc.setRequest(requests.findById(requestId).orElseThrow());
        return documents.save(doc);
    }

    @Transactional
    EmployeeDocument uploadForContract(Long contractId, MultipartFile file) throws IOException {
        EmployeeDocument doc = store(file);
        doc.setContract(contracts.findById(contractId).orElseThrow());
        return documents.save(doc);
    }

    List<DocumentDto> byEmployee(Long employeeId) {
        return documents.findByEmployeeId(employeeId).stream().map(Mappers::document).toList();
    }

    List<DocumentDto> byRequest(Long requestId) {
        return documents.findByRequestId(requestId).stream().map(Mappers::document).toList();
    }

    EmployeeDocument find(Long id) {
        return documents.findById(id).orElseThrow();
    }

    private EmployeeDocument store(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("El archivo es obligatorio");
        }
        String contentType = file.getContentType() == null ? "" : file.getContentType();
        String name = file.getOriginalFilename() == null ? "" : file.getOriginalFilename().toLowerCase();
        boolean allowed = contentType.startsWith("image/")
                || contentType.equals("application/pdf")
                || contentType.equals("application/msword")
                || contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
                || name.endsWith(".pdf") || name.endsWith(".doc") || name.endsWith(".docx")
                || name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".webp");
        if (!allowed) {
            throw new BusinessException("Formato no permitido. Usa PDF, Word o imagen.");
        }
        Path dir = Path.of(props.uploadDir()).toAbsolutePath();
        Files.createDirectories(dir);
        String originalName = Objects.requireNonNull(file.getOriginalFilename()).replaceAll("[^a-zA-Z0-9._-]", "_");
        String storedName = UUID.randomUUID() + "-" + originalName;
        Path target = dir.resolve(storedName).normalize();
        if (!target.startsWith(dir)) {
            throw new BusinessException("Nombre de archivo no permitido");
        }
        file.transferTo(target);
        EmployeeDocument doc = new EmployeeDocument();
        doc.setOriginalName(file.getOriginalFilename());
        doc.setStoredName(storedName);
        doc.setPath(target.toString());
        doc.setContentType(file.getContentType());
        doc.setSize(file.getSize());
        doc.setUploadedBy(current.account().getEmail());
        return doc;
    }
}

@Service
@RequiredArgsConstructor
class AttendanceService {
    private final AttendanceRepository attendance;
    private final EmployeeRepository employees;
    private final CurrentUserService current;
    private final AuditService audit;
    private final AppProperties props;

    @Transactional
    AttendanceDto checkIn() {
        Employee employee = currentEmployee();
        LocalDate today = LocalDate.now();
        Attendance record = attendance.findByEmployeeIdAndWorkDate(employee.getId(), today).orElseGet(Attendance::new);
        if (record.getCheckIn() != null) {
            throw new BusinessException("Ya registraste entrada para este turno");
        }
        record.setEmployee(employee);
        record.setWorkDate(today);
        LocalTime now = LocalTime.now();
        record.setCheckIn(now);
        boolean late = now.isAfter(props.attendance().startTime().plusMinutes(props.attendance().lateToleranceMinutes()));
        record.setLate(late);
        record.setStatus(late ? AttendanceStatus.TARDANZA : AttendanceStatus.PRESENTE);
        attendance.save(record);
        audit.log("CHECK_IN", "ASISTENCIA", String.valueOf(record.getId()), "Registro de entrada");
        return Mappers.attendance(record);
    }

    @Transactional
    AttendanceDto checkOut() {
        Employee employee = currentEmployee();
        Attendance record = attendance.findByEmployeeIdAndWorkDate(employee.getId(), LocalDate.now())
                .orElseThrow(() -> new BusinessException("Primero debes registrar entrada"));
        if (record.getCheckOut() != null) {
            throw new BusinessException("Ya registraste salida para este turno");
        }
        record.setCheckOut(LocalTime.now());
        audit.log("CHECK_OUT", "ASISTENCIA", String.valueOf(record.getId()), "Registro de salida");
        return Mappers.attendance(record);
    }

    List<AttendanceDto> list(Long employeeId, String area, LocalDate from, LocalDate to) {
        UserAccount user = current.account();
        LocalDate start = from == null ? LocalDate.now().minusMonths(1) : from;
        LocalDate end = to == null ? LocalDate.now() : to;
        if (user.getRole() == Role.EMPLEADO) {
            return attendance.findByEmployeeIdAndWorkDateBetween(user.getEmployee().getId(), start, end).stream().map(Mappers::attendance).toList();
        }
        if (employeeId != null) {
            return attendance.findByEmployeeIdAndWorkDateBetween(employeeId, start, end).stream().map(Mappers::attendance).toList();
        }
        if (area != null && !area.isBlank()) {
            return attendance.findByEmployeeAreaContainingIgnoreCaseAndWorkDateBetween(area, start, end).stream().map(Mappers::attendance).toList();
        }
        return attendance.findByWorkDateBetween(start, end).stream().map(Mappers::attendance).toList();
    }

    @Transactional
    List<AttendanceDto> importExcel(MultipartFile file, LocalDate workDate) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("El Excel es obligatorio");
        }
        LocalDate date = workDate == null ? LocalDate.now() : workDate;
        try (var workbook = WorkbookFactory.create(file.getInputStream())) {
            var sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();
            int imported = 0;
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                String name = text(row.getCell(0), formatter);
                String dni = text(row.getCell(1), formatter);
                String position = text(row.getCell(2), formatter);
                BigDecimal hours = decimal(row.getCell(3), formatter);
                boolean late = tardy(row.getCell(4), formatter);
                if (dni.isBlank()) continue;
                Employee employee = employees.findByDni(dni).orElseGet(() -> createImportedEmployee(name, dni, position));
                Attendance record = attendance.findByEmployeeIdAndWorkDate(employee.getId(), date).orElseGet(Attendance::new);
                record.setEmployee(employee);
                record.setWorkDate(date);
                record.setImportedPosition(position);
                record.setHoursWorked(hours);
                record.setLate(late);
                record.setStatus(late ? AttendanceStatus.TARDANZA : AttendanceStatus.PRESENTE);
                attendance.save(record);
                imported++;
            }
            audit.log("IMPORT", "ASISTENCIA", date.toString(), "Importación Excel de asistencia: " + imported + " filas");
        }
        return list(null, null, date, date);
    }

    private Employee createImportedEmployee(String fullName, String dni, String position) {
        Employee employee = new Employee();
        String cleanName = fullName == null || fullName.isBlank() ? "Empleado " + dni : fullName.trim();
        String[] parts = cleanName.split("\\s+", 2);
        employee.setFirstName(parts[0]);
        employee.setLastName(parts.length > 1 ? parts[1] : "Importado");
        employee.setDni(dni);
        employee.setEmail("empleado." + dni + "@sistemahr.local");
        employee.setPosition(position);
        employee.setArea("Importado");
        employee.setLocation("Importado");
        return employees.save(employee);
    }

    private String text(Cell cell, DataFormatter formatter) {
        return cell == null ? "" : formatter.formatCellValue(cell).trim();
    }

    private BigDecimal decimal(Cell cell, DataFormatter formatter) {
        String value = text(cell, formatter).replace(",", ".");
        if (value.isBlank()) return BigDecimal.ZERO;
        try {
            return new BigDecimal(value.replaceAll("[^0-9.\\-]", ""));
        } catch (NumberFormatException ex) {
            return BigDecimal.ZERO;
        }
    }

    private boolean tardy(Cell cell, DataFormatter formatter) {
        String value = text(cell, formatter).toLowerCase();
        if (value.isBlank()) return false;
        if (value.equals("si") || value.equals("sí") || value.equals("true") || value.equals("x")) return true;
        try {
            return new BigDecimal(value.replace(",", ".").replaceAll("[^0-9.\\-]", "")).compareTo(BigDecimal.ZERO) > 0;
        } catch (NumberFormatException ex) {
            return value.contains("tard");
        }
    }

    private Employee currentEmployee() {
        UserAccount user = current.account();
        if (user.getEmployee() == null) {
            throw new BusinessException("El usuario no está asociado a un empleado");
        }
        return user.getEmployee();
    }
}

@Service
@RequiredArgsConstructor
class RequestService {
    private final RequestRepository requests;
    private final EmployeeRepository employees;
    private final UserRepository users;
    private final CurrentUserService current;
    private final AuditService audit;
    private final NotificationRepository notifications;

    @Transactional
    AbsenceRequestDto create(AbsenceRequestCreate dto) {
        UserAccount user = current.account();
        Long employeeId = user.getRole() == Role.EMPLEADO ? user.getEmployee().getId() : dto.employeeId();
        if (employeeId == null) {
            throw new BusinessException("El empleado es obligatorio");
        }
        if (dto.endDate().isBefore(dto.startDate())) {
            throw new BusinessException("La fecha fin no puede ser anterior a la fecha inicio");
        }
        AbsenceRequest request = new AbsenceRequest();
        request.setEmployee(employees.findById(employeeId).orElseThrow());
        request.setType(dto.type());
        request.setStartDate(dto.startDate());
        request.setEndDate(dto.endDate());
        request.setReason(dto.reason());
        requests.save(request);
        audit.log("CREATE", "SOLICITUDES", String.valueOf(request.getId()), dto.type().name());
        return Mappers.request(request);
    }

    List<AbsenceRequestDto> list() {
        UserAccount user = current.account();
        if (user.getRole() == Role.EMPLEADO) {
            return requests.findByEmployeeId(user.getEmployee().getId()).stream().map(Mappers::request).toList();
        }
        if (user.getRole() == Role.JEFE && user.getEmployee() != null) {
            return requests.findByEmployeeAreaContainingIgnoreCaseAndStatus(user.getEmployee().getArea(), RequestStatus.PENDIENTE).stream().map(Mappers::request).toList();
        }
        return requests.findAll().stream().map(Mappers::request).toList();
    }

    @Transactional
    AbsenceRequestDto approve(Long id) {
        AbsenceRequest request = requests.findById(id).orElseThrow();
        request.setStatus(RequestStatus.APROBADA);
        request.setReviewedAt(LocalDateTime.now());
        request.setReviewedBy(current.account().getEmail());
        notifyEmployee(request, "Tu solicitud " + request.getType() + " fue aprobada");
        audit.log("APPROVE", "SOLICITUDES", String.valueOf(id), "Solicitud aprobada");
        return Mappers.request(request);
    }

    @Transactional
    AbsenceRequestDto reject(Long id, RejectRequest dto) {
        if (dto == null || dto.reason() == null || dto.reason().isBlank()) {
            throw new BusinessException("El motivo de rechazo es obligatorio");
        }
        AbsenceRequest request = requests.findById(id).orElseThrow();
        request.setStatus(RequestStatus.RECHAZADA);
        request.setRejectionReason(dto.reason());
        request.setReviewedAt(LocalDateTime.now());
        request.setReviewedBy(current.account().getEmail());
        notifyEmployee(request, "Tu solicitud " + request.getType() + " fue rechazada: " + dto.reason());
        audit.log("REJECT", "SOLICITUDES", String.valueOf(id), "Solicitud rechazada");
        return Mappers.request(request);
    }

    private void notifyEmployee(AbsenceRequest request, String message) {
        users.findByEmployeeId(request.getEmployee().getId()).forEach(user -> {
            Notification notification = new Notification();
            notification.setRecipient(user);
            notification.setMessage(message);
            notifications.save(notification);
        });
    }
}

@Service
@RequiredArgsConstructor
class ContractService {
    private final ContractRepository contracts;
    private final EmployeeRepository employees;
    private final AuditService audit;

    @Transactional
    ContractDto create(ContractRequest dto) {
        LaborContract contract = new LaborContract();
        contract.setEmployee(employees.findById(dto.employeeId()).orElseThrow());
        contract.setContractType(dto.contractType());
        contract.setStartDate(dto.startDate());
        contract.setEndDate(dto.endDate());
        contract.setStatus(dto.status() == null ? ContractStatus.VIGENTE : dto.status());
        contracts.save(contract);
        audit.log("CREATE", "CONTRATOS", String.valueOf(contract.getId()), dto.contractType());
        return Mappers.contract(contract);
    }

    List<ContractDto> list(Long employeeId) {
        return (employeeId == null ? contracts.findAll() : contracts.findByEmployeeId(employeeId)).stream().map(Mappers::contract).toList();
    }
}

@Service
@RequiredArgsConstructor
class NotificationService {
    private final NotificationRepository notifications;
    private final CurrentUserService current;

    List<NotificationDto> mine() {
        return notifications.findByRecipientIdOrderByCreatedAtDesc(current.account().getId()).stream().map(Mappers::notification).toList();
    }
}

@Service
@RequiredArgsConstructor
class ReportService {
    private final EmployeeService employeeService;
    private final AttendanceService attendanceService;
    private final RequestRepository requests;

    byte[] exportPdf(String type) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();
            document.add(new Paragraph("SistemaHR - Reporte " + type));
            document.add(new Paragraph("Fecha de generacion: " + LocalDateTime.now()));
            PdfPTable table = new PdfPTable(4);
            rows(type).forEach(row -> row.forEach(table::addCell));
            document.add(table);
            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new BusinessException("No se pudo generar PDF");
        }
    }

    byte[] exportExcel(String type) {
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            var sheet = workbook.createSheet("Reporte");
            int index = 0;
            for (List<String> values : rows(type)) {
                Row row = sheet.createRow(index++);
                for (int i = 0; i < values.size(); i++) {
                    row.createCell(i).setCellValue(values.get(i));
                }
            }
            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new BusinessException("No se pudo generar Excel");
        }
    }

    private List<List<String>> rows(String type) {
        if ("attendance".equalsIgnoreCase(type)) {
            return attendanceService.list(null, null, LocalDate.now().minusMonths(1), LocalDate.now()).stream()
                    .map(a -> List.of(a.employeeName(), String.valueOf(a.workDate()), String.valueOf(a.checkIn()), a.status().name()))
                    .toList();
        }
        if ("vacations".equalsIgnoreCase(type)) {
            return requests.findByTypeAndStatus(RequestType.VACACIONES, RequestStatus.APROBADA).stream()
                    .map(r -> List.of(Mappers.employeeName(r.getEmployee()), r.getStartDate().toString(), r.getEndDate().toString(), r.getStatus().name()))
                    .toList();
        }
        return employeeService.active(null, null).stream()
                .map(e -> List.of(e.firstName() + " " + e.lastName(), e.position() == null ? "" : e.position(), e.area() == null ? "" : e.area(), e.location() == null ? "" : e.location()))
                .toList();
    }
}
