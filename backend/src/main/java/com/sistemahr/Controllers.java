package com.sistemahr;

import jakarta.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseCookie;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
class AuthController {
    private final AuthService auth;
    private final CurrentUserService current;
    private final AppProperties props;

    @PostMapping("/login")
    ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthenticatedLogin login = auth.login(request);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, sessionCookie(login.token(), props.cookieSecure()).toString())
                .body(new LoginResponse(login.user()));
    }

    @PostMapping("/logout")
    ResponseEntity<Void> logout() {
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, sessionCookie("", props.cookieSecure()).maxAge(0).toString())
                .build();
    }

    @GetMapping("/me")
    UserDto me() {
        return Mappers.user(current.account());
    }

    @GetMapping("/csrf")
    CsrfResponse csrf(CsrfToken token) {
        return new CsrfResponse(token.getToken());
    }

    private ResponseCookie.ResponseCookieBuilder sessionCookie(String value, boolean secure) {
        return ResponseCookie.from(JwtAuthenticationFilter.AUTH_COOKIE, value)
                .httpOnly(true)
                .secure(secure)
                .sameSite("Strict")
                .path("/")
                .maxAge(value.isBlank() ? 0 : props.jwtExpirationMinutes() * 60);
    }
}

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
class UserController {
    private final UserService users;

    @GetMapping
    List<UserDto> list() {
        return users.list();
    }

    @PostMapping
    UserDto create(@Valid @RequestBody UserRequest request) {
        return users.save(null, request);
    }

    @PutMapping("/{id}")
    UserDto update(@PathVariable Long id, @Valid @RequestBody UserRequest request) {
        return users.save(id, request);
    }
}

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
class EmployeeController {
    private final EmployeeService employees;
    private final DocumentService documents;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','RRHH','JEFE')")
    List<EmployeeDto> list(@RequestParam(required = false) String area, @RequestParam(required = false) String location) {
        return employees.list(area, location);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','RRHH')")
    EmployeeDto create(@Valid @RequestBody EmployeeRequest request) {
        return employees.save(null, request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RRHH')")
    EmployeeDto update(@PathVariable Long id, @Valid @RequestBody EmployeeRequest request) {
        return employees.save(id, request);
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('ADMIN','RRHH')")
    EmployeeDto deactivate(@PathVariable Long id) {
        return employees.deactivate(id);
    }

    @PatchMapping("/{id}/reinstate")
    @PreAuthorize("hasAnyRole('ADMIN','RRHH')")
    EmployeeDto reinstate(@PathVariable Long id) {
        return employees.reinstate(id);
    }

    @PostMapping(value = "/{id}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN','RRHH')")
    DocumentDto upload(@PathVariable Long id, @RequestPart MultipartFile file) throws IOException {
        return documents.uploadForEmployee(id, file);
    }

    @GetMapping("/{id}/documents")
    @PreAuthorize("hasAnyRole('ADMIN','RRHH')")
    List<DocumentDto> docs(@PathVariable Long id) {
        return documents.byEmployee(id);
    }
}

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
class DocumentController {
    private final DocumentService documents;
    private final AppProperties props;

    @GetMapping("/{id}/download")
    @PreAuthorize("hasAnyRole('ADMIN','RRHH','JEFE')")
    ResponseEntity<FileSystemResource> download(@PathVariable Long id) throws IOException {
        EmployeeDocument doc = documents.find(id);
        Path uploadRoot = Path.of(props.uploadDir()).toAbsolutePath().normalize();
        Path filePath = Path.of(doc.getPath()).toAbsolutePath().normalize();
        if (!filePath.startsWith(uploadRoot)) {
            throw new BusinessException("Archivo no disponible");
        }
        FileSystemResource resource = new FileSystemResource(filePath);
        String contentType = doc.getContentType() == null ? Files.probeContentType(resource.getFile().toPath()) : doc.getContentType();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(doc.getOriginalName()).build().toString())
                .contentType(MediaType.parseMediaType(contentType == null ? MediaType.APPLICATION_OCTET_STREAM_VALUE : contentType))
                .body(resource);
    }
}

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
class AttendanceController {
    private final AttendanceService attendance;

    @PostMapping("/check-in")
    @PreAuthorize("hasRole('EMPLEADO')")
    AttendanceDto checkIn() {
        return attendance.checkIn();
    }

    @PostMapping("/check-out")
    @PreAuthorize("hasRole('EMPLEADO')")
    AttendanceDto checkOut() {
        return attendance.checkOut();
    }

    @GetMapping
    List<AttendanceDto> list(@RequestParam(required = false) Long employeeId, @RequestParam(required = false) String area, @RequestParam(required = false) LocalDate from, @RequestParam(required = false) LocalDate to) {
        return attendance.list(employeeId, area, from, to);
    }

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN','RRHH')")
    List<AttendanceDto> importExcel(@RequestPart MultipartFile file, @RequestParam(required = false) LocalDate workDate) throws IOException {
        return attendance.importExcel(file, workDate);
    }
}

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
class RequestController {
    private final RequestService requests;
    private final DocumentService documents;

    @PostMapping
    AbsenceRequestDto create(@Valid @RequestBody AbsenceRequestCreate request) {
        return requests.create(request);
    }

    @GetMapping
    List<AbsenceRequestDto> list() {
        return requests.list();
    }

    @PostMapping(value = "/{id}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    DocumentDto upload(@PathVariable Long id, @RequestPart MultipartFile file) throws IOException {
        return Mappers.document(documents.uploadForRequest(id, file));
    }

    @GetMapping("/{id}/documents")
    List<DocumentDto> documents(@PathVariable Long id) {
        return documents.byRequest(id);
    }

    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN','RRHH','JEFE')")
    AbsenceRequestDto approve(@PathVariable Long id) {
        return requests.approve(id);
    }

    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN','RRHH','JEFE')")
    AbsenceRequestDto reject(@PathVariable Long id, @Valid @RequestBody RejectRequest request) {
        return requests.reject(id, request);
    }
}

@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','RRHH')")
class ContractController {
    private final ContractService contracts;
    private final DocumentService documents;

    @PostMapping
    ContractDto create(@Valid @RequestBody ContractRequest request) {
        return contracts.create(request);
    }

    @GetMapping
    List<ContractDto> list(@RequestParam(required = false) Long employeeId) {
        return contracts.list(employeeId);
    }

    @PostMapping(value = "/{id}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    DocumentDto upload(@PathVariable Long id, @RequestPart MultipartFile file) throws IOException {
        return Mappers.document(documents.uploadForContract(id, file));
    }
}

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','RRHH')")
class ReportController {
    private final EmployeeService employees;
    private final AttendanceService attendance;
    private final RequestRepository requests;
    private final ReportService reports;

    @GetMapping("/active-employees")
    List<EmployeeDto> activeEmployees(@RequestParam(required = false) String area, @RequestParam(required = false) String location) {
        return employees.active(area, location);
    }

    @GetMapping("/attendance")
    List<AttendanceDto> attendance(@RequestParam(required = false) Long employeeId, @RequestParam(required = false) String area, @RequestParam(required = false) LocalDate from, @RequestParam(required = false) LocalDate to) {
        return attendance.list(employeeId, area, from, to);
    }

    @GetMapping("/vacations")
    List<AbsenceRequestDto> vacations() {
        return requests.findByTypeAndStatus(RequestType.VACACIONES, RequestStatus.APROBADA).stream().map(Mappers::request).toList();
    }

    @GetMapping("/{type}/export/pdf")
    ResponseEntity<byte[]> pdf(@PathVariable String type) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte-" + type + ".pdf")
                .body(reports.exportPdf(type));
    }

    @GetMapping("/{type}/export/excel")
    ResponseEntity<byte[]> excel(@PathVariable String type) {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte-" + type + ".xlsx")
                .body(reports.exportExcel(type));
    }
}

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
class AuditController {
    private final AuditLogRepository audits;

    @GetMapping
    List<AuditDto> list(@RequestParam(required = false) String user) {
        List<AuditLog> rows = user == null || user.isBlank()
                ? audits.findTop200ByOrderByOccurredAtDesc()
                : audits.findTop200ByActorEmailContainingIgnoreCaseOrActorNameContainingIgnoreCaseOrActorDniContainingIgnoreCaseOrActorPositionContainingIgnoreCaseOrderByOccurredAtDesc(user, user, user, user);
        return rows.stream().map(Mappers::audit).toList();
    }
}

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
class NotificationController {
    private final NotificationService notifications;

    @GetMapping
    List<NotificationDto> mine() {
        return notifications.mine();
    }
}

@RestControllerAdvice
class ApiExceptionHandler {
    @ExceptionHandler({BusinessException.class, IllegalArgumentException.class, MethodArgumentNotValidException.class})
    ResponseEntity<ErrorResponse> badRequest(Exception ex) {
        String message = ex instanceof MethodArgumentNotValidException ? "Solicitud invalida" : ex.getMessage();
        return ResponseEntity.badRequest().body(new ErrorResponse(message, LocalDateTime.now()));
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorResponse> server(Exception ex) {
        return ResponseEntity.internalServerError().body(new ErrorResponse("Error interno del servidor", LocalDateTime.now()));
    }
}
