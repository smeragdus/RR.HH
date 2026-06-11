package com.sistemahr;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class ServicesUnitTests {

    @TempDir
    java.nio.file.Path tempDir;

    @Test
    void authRejectsInactiveEmployeeLogin() {
        UserRepository users = mock(UserRepository.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        JwtService jwt = mock(JwtService.class);
        AuthService service = new AuthService(users, encoder, jwt);
        UserAccount account = user("empleado@empresa.com", Role.EMPLEADO, employee(1L, "12345678", EmploymentStatus.INACTIVO));

        when(users.findByEmail("empleado@empresa.com")).thenReturn(Optional.of(account));

        assertThatThrownBy(() -> service.login(new LoginRequest("empleado@empresa.com", "clave")))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Usuario inactivo");
    }

    @Test
    void authValidCredentialsReturnTokenAndUser() {
        UserRepository users = mock(UserRepository.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        JwtService jwt = mock(JwtService.class);
        AuthService service = new AuthService(users, encoder, jwt);
        UserAccount account = user("admin@empresa.com", Role.ADMIN, null);

        when(users.findByEmail("admin@empresa.com")).thenReturn(Optional.of(account));
        when(encoder.matches("clave", "hash")).thenReturn(true);
        when(jwt.generate(account)).thenReturn("jwt-token");

        AuthenticatedLogin response = service.login(new LoginRequest("admin@empresa.com", "clave"));

        assertThat(response.token()).isEqualTo("jwt-token");
        assertThat(response.user().email()).isEqualTo("admin@empresa.com");
        assertThat(response.user().role()).isEqualTo(Role.ADMIN);
    }

    @Test
    void authRejectsUnknownEmail() {
        UserRepository users = mock(UserRepository.class);
        AuthService service = new AuthService(users, mock(PasswordEncoder.class), mock(JwtService.class));

        when(users.findByEmail("nadie@empresa.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.login(new LoginRequest("nadie@empresa.com", "clave")))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Credenciales incorrectas");
    }

    @Test
    void authRejectsWrongPassword() {
        UserRepository users = mock(UserRepository.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        AuthService service = new AuthService(users, encoder, mock(JwtService.class));
        UserAccount account = user("admin@empresa.com", Role.ADMIN, null);

        when(users.findByEmail("admin@empresa.com")).thenReturn(Optional.of(account));
        when(encoder.matches("mala", "hash")).thenReturn(false);

        assertThatThrownBy(() -> service.login(new LoginRequest("admin@empresa.com", "mala")))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Credenciales incorrectas");
    }

    @Test
    void userCreateRequiresPassword() {
        UserService service = new UserService(mock(UserRepository.class), mock(EmployeeRepository.class), mock(PasswordEncoder.class), mock(AuditService.class));

        assertThatThrownBy(() -> service.save(null, new UserRequest("nuevo@empresa.com", " ", Role.RRHH, AccountStatus.ACTIVO, null)))
                .isInstanceOf(BusinessException.class)
                .hasMessage("La contraseña es obligatoria");
    }

    @Test
    void userCreateRejectsDuplicateEmail() {
        UserRepository users = mock(UserRepository.class);
        UserService service = new UserService(users, mock(EmployeeRepository.class), mock(PasswordEncoder.class), mock(AuditService.class));

        when(users.existsByEmail("duplicado@empresa.com")).thenReturn(true);

        assertThatThrownBy(() -> service.save(null, new UserRequest("duplicado@empresa.com", "clave", Role.RRHH, AccountStatus.ACTIVO, null)))
                .isInstanceOf(BusinessException.class)
                .hasMessage("El correo ya existe");
    }

    @Test
    void userCreateEncodesPasswordAndAudits() {
        UserRepository users = mock(UserRepository.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        AuditService audit = mock(AuditService.class);
        UserService service = new UserService(users, mock(EmployeeRepository.class), encoder, audit);
        ArgumentCaptor<UserAccount> captor = ArgumentCaptor.forClass(UserAccount.class);

        when(encoder.encode("clave")).thenReturn("encoded");
        when(users.save(any(UserAccount.class))).thenAnswer(invocation -> {
            UserAccount account = invocation.getArgument(0);
            account.setId(30L);
            return account;
        });

        UserDto dto = service.save(null, new UserRequest("rrhh@empresa.com", "clave", Role.RRHH, null, null));

        verify(users).save(captor.capture());
        assertThat(captor.getValue().getPassword()).isEqualTo("encoded");
        assertThat(dto.status()).isEqualTo(AccountStatus.ACTIVO);
        verify(audit).log("CREATE", "USUARIOS", "30", "Usuario actualizado");
    }

    @Test
    void employeeSaveRejectsDuplicatedDni() {
        EmployeeRepository employees = mock(EmployeeRepository.class);
        UserRepository users = mock(UserRepository.class);
        AuditService audit = mock(AuditService.class);
        CurrentUserService current = mock(CurrentUserService.class);
        EmployeeService service = new EmployeeService(employees, users, audit, current);

        when(employees.existsByDni("12345678")).thenReturn(true);

        assertThatThrownBy(() -> service.save(null, employeeRequest("12345678")))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Ya existe un empleado con el mismo DNI");
    }

    @Test
    void employeeSaveCreatesActiveEmployeeAndAudits() {
        EmployeeRepository employees = mock(EmployeeRepository.class);
        CurrentUserService current = mock(CurrentUserService.class);
        AuditService audit = mock(AuditService.class);
        EmployeeService service = new EmployeeService(employees, mock(UserRepository.class), audit, current);
        UserAccount rrhh = user("rrhh@empresa.com", Role.RRHH, null);

        when(current.account()).thenReturn(rrhh);
        when(employees.save(any(Employee.class))).thenAnswer(invocation -> {
            Employee employee = invocation.getArgument(0);
            employee.setId(50L);
            return employee;
        });

        EmployeeDto dto = service.save(null, employeeRequest("70000001"));

        assertThat(dto.employmentStatus()).isEqualTo(EmploymentStatus.ACTIVO);
        assertThat(dto.updatedBy()).isEqualTo("rrhh@empresa.com");
        verify(audit).log("CREATE", "EMPLEADOS", "50", "Empleado actualizado");
    }

    @Test
    void employeeUpdateRejectsDniUsedByAnotherEmployee() {
        EmployeeRepository employees = mock(EmployeeRepository.class);
        EmployeeService service = new EmployeeService(employees, mock(UserRepository.class), mock(AuditService.class), mock(CurrentUserService.class));

        when(employees.findById(5L)).thenReturn(Optional.of(employee(5L, "11111111", EmploymentStatus.ACTIVO)));
        when(employees.existsByDniAndIdNot("22222222", 5L)).thenReturn(true);

        assertThatThrownBy(() -> service.save(5L, employeeRequest("22222222")))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Ya existe un empleado con el mismo DNI");
    }

    @Test
    void employeeActiveFiltersByAreaAndLocation() {
        EmployeeRepository employees = mock(EmployeeRepository.class);
        EmployeeService service = new EmployeeService(employees, mock(UserRepository.class), mock(AuditService.class), mock(CurrentUserService.class));
        Employee rrhhLima = employee(1L, "10000001", EmploymentStatus.ACTIVO);
        rrhhLima.setArea("RRHH");
        rrhhLima.setLocation("Lima");
        Employee ventasLima = employee(2L, "10000002", EmploymentStatus.ACTIVO);
        ventasLima.setArea("Ventas");
        ventasLima.setLocation("Lima");

        when(employees.findByEmploymentStatus(EmploymentStatus.ACTIVO)).thenReturn(List.of(rrhhLima, ventasLima));

        List<EmployeeDto> active = service.active("rrhh", "lima");

        assertThat(active).hasSize(1);
        assertThat(active.get(0).dni()).isEqualTo("10000001");
    }

    @Test
    void employeeDeactivateAndReinstateTogglesEmployeeAndLinkedUsers() {
        EmployeeRepository employees = mock(EmployeeRepository.class);
        UserRepository users = mock(UserRepository.class);
        AuditService audit = mock(AuditService.class);
        CurrentUserService current = mock(CurrentUserService.class);
        EmployeeService service = new EmployeeService(employees, users, audit, current);
        Employee employee = employee(9L, "87654321", EmploymentStatus.ACTIVO);
        UserAccount linkedUser = user("persona@empresa.com", Role.EMPLEADO, employee);
        UserAccount rrhh = user("rrhh@empresa.com", Role.RRHH, null);

        when(current.account()).thenReturn(rrhh);
        when(employees.findById(9L)).thenReturn(Optional.of(employee));
        when(users.findByEmployeeId(9L)).thenReturn(List.of(linkedUser));

        EmployeeDto deactivated = service.deactivate(9L);
        assertThat(deactivated.employmentStatus()).isEqualTo(EmploymentStatus.INACTIVO);
        assertThat(linkedUser.getStatus()).isEqualTo(AccountStatus.INACTIVO);

        EmployeeDto reinstated = service.reinstate(9L);
        assertThat(reinstated.employmentStatus()).isEqualTo(EmploymentStatus.ACTIVO);
        assertThat(linkedUser.getStatus()).isEqualTo(AccountStatus.ACTIVO);
        verify(audit).log("DEACTIVATE", "EMPLEADOS", "9", "Baja lógica de empleado");
        verify(audit).log("REINSTATE", "EMPLEADOS", "9", "Reincorporación de empleado");
    }

    @Test
    void requestRejectRequiresReason() {
        RequestRepository requests = mock(RequestRepository.class);
        EmployeeRepository employees = mock(EmployeeRepository.class);
        UserRepository users = mock(UserRepository.class);
        CurrentUserService current = mock(CurrentUserService.class);
        AuditService audit = mock(AuditService.class);
        NotificationRepository notifications = mock(NotificationRepository.class);
        RequestService service = new RequestService(requests, employees, users, current, audit, notifications);

        assertThatThrownBy(() -> service.reject(1L, new RejectRequest(" ")))
                .isInstanceOf(BusinessException.class)
                .hasMessage("El motivo de rechazo es obligatorio");
    }

    @Test
    void requestCreateUsesCurrentEmployeeForEmployeeRole() {
        RequestRepository requests = mock(RequestRepository.class);
        EmployeeRepository employees = mock(EmployeeRepository.class);
        CurrentUserService current = mock(CurrentUserService.class);
        AuditService audit = mock(AuditService.class);
        RequestService service = new RequestService(requests, employees, mock(UserRepository.class), current, audit, mock(NotificationRepository.class));
        Employee employee = employee(44L, "80808080", EmploymentStatus.ACTIVO);
        UserAccount empleado = user("empleado@empresa.com", Role.EMPLEADO, employee);

        when(current.account()).thenReturn(empleado);
        when(employees.findById(44L)).thenReturn(Optional.of(employee));
        when(requests.save(any(AbsenceRequest.class))).thenAnswer(invocation -> {
            AbsenceRequest request = invocation.getArgument(0);
            request.setId(70L);
            return request;
        });

        AbsenceRequestDto dto = service.create(new AbsenceRequestCreate(RequestType.VACACIONES, LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 5), "Descanso", 99L));

        assertThat(dto.employeeId()).isEqualTo(44L);
        assertThat(dto.status()).isEqualTo(RequestStatus.PENDIENTE);
        verify(audit).log("CREATE", "SOLICITUDES", "70", "VACACIONES");
    }

    @Test
    void requestCreateRejectsEndDateBeforeStartDate() {
        CurrentUserService current = mock(CurrentUserService.class);
        RequestService service = new RequestService(mock(RequestRepository.class), mock(EmployeeRepository.class), mock(UserRepository.class), current, mock(AuditService.class), mock(NotificationRepository.class));

        when(current.account()).thenReturn(user("rrhh@empresa.com", Role.RRHH, null));

        assertThatThrownBy(() -> service.create(new AbsenceRequestCreate(RequestType.PERMISO, LocalDate.of(2026, 7, 10), LocalDate.of(2026, 7, 9), "Error", 1L)))
                .isInstanceOf(BusinessException.class)
                .hasMessage("La fecha fin no puede ser anterior a la fecha inicio");
    }

    @Test
    void requestListForEmployeeShowsOnlyOwnRequests() {
        RequestRepository requests = mock(RequestRepository.class);
        CurrentUserService current = mock(CurrentUserService.class);
        RequestService service = new RequestService(requests, mock(EmployeeRepository.class), mock(UserRepository.class), current, mock(AuditService.class), mock(NotificationRepository.class));
        Employee employee = employee(77L, "30303030", EmploymentStatus.ACTIVO);

        when(current.account()).thenReturn(user("empleado@empresa.com", Role.EMPLEADO, employee));
        when(requests.findByEmployeeId(77L)).thenReturn(List.of(absenceRequest(1L, employee)));

        List<AbsenceRequestDto> result = service.list();

        assertThat(result).hasSize(1);
        verify(requests).findByEmployeeId(77L);
    }

    @Test
    void requestListForBossShowsPendingRequestsFromOwnArea() {
        RequestRepository requests = mock(RequestRepository.class);
        CurrentUserService current = mock(CurrentUserService.class);
        RequestService service = new RequestService(requests, mock(EmployeeRepository.class), mock(UserRepository.class), current, mock(AuditService.class), mock(NotificationRepository.class));
        Employee jefeEmployee = employee(78L, "40404040", EmploymentStatus.ACTIVO);
        jefeEmployee.setArea("Operaciones");

        when(current.account()).thenReturn(user("jefe@empresa.com", Role.JEFE, jefeEmployee));
        when(requests.findByEmployeeAreaContainingIgnoreCaseAndStatus("Operaciones", RequestStatus.PENDIENTE)).thenReturn(List.of());

        List<AbsenceRequestDto> result = service.list();

        assertThat(result).isEmpty();
        verify(requests).findByEmployeeAreaContainingIgnoreCaseAndStatus("Operaciones", RequestStatus.PENDIENTE);
    }

    @Test
    void requestApproveUpdatesStatusAndNotifiesEmployee() {
        RequestRepository requests = mock(RequestRepository.class);
        UserRepository users = mock(UserRepository.class);
        CurrentUserService current = mock(CurrentUserService.class);
        AuditService audit = mock(AuditService.class);
        NotificationRepository notifications = mock(NotificationRepository.class);
        RequestService service = new RequestService(requests, mock(EmployeeRepository.class), users, current, audit, notifications);
        Employee employee = employee(80L, "50505050", EmploymentStatus.ACTIVO);
        AbsenceRequest request = absenceRequest(81L, employee);
        UserAccount jefe = user("jefe@empresa.com", Role.JEFE, employee(82L, "60606060", EmploymentStatus.ACTIVO));

        when(requests.findById(81L)).thenReturn(Optional.of(request));
        when(current.account()).thenReturn(jefe);
        when(users.findByEmployeeId(80L)).thenReturn(List.of(user("empleado@empresa.com", Role.EMPLEADO, employee)));

        AbsenceRequestDto dto = service.approve(81L);

        assertThat(dto.status()).isEqualTo(RequestStatus.APROBADA);
        assertThat(dto.reviewedBy()).isEqualTo("jefe@empresa.com");
        verify(notifications).save(any(Notification.class));
        verify(audit).log("APPROVE", "SOLICITUDES", "81", "Solicitud aprobada");
    }

    @Test
    void requestRejectUpdatesStatusAndNotifiesEmployee() {
        RequestRepository requests = mock(RequestRepository.class);
        EmployeeRepository employees = mock(EmployeeRepository.class);
        UserRepository users = mock(UserRepository.class);
        CurrentUserService current = mock(CurrentUserService.class);
        AuditService audit = mock(AuditService.class);
        NotificationRepository notifications = mock(NotificationRepository.class);
        RequestService service = new RequestService(requests, employees, users, current, audit, notifications);
        Employee employee = employee(4L, "11112222", EmploymentStatus.ACTIVO);
        AbsenceRequest request = absenceRequest(12L, employee);
        UserAccount jefe = user("jefe@empresa.com", Role.JEFE, employee(5L, "22223333", EmploymentStatus.ACTIVO));
        UserAccount requester = user("empleado@empresa.com", Role.EMPLEADO, employee);

        when(requests.findById(12L)).thenReturn(Optional.of(request));
        when(current.account()).thenReturn(jefe);
        when(users.findByEmployeeId(4L)).thenReturn(List.of(requester));

        AbsenceRequestDto dto = service.reject(12L, new RejectRequest("No corresponde"));

        assertThat(dto.status()).isEqualTo(RequestStatus.RECHAZADA);
        assertThat(dto.rejectionReason()).isEqualTo("No corresponde");
        assertThat(dto.reviewedBy()).isEqualTo("jefe@empresa.com");
        verify(notifications).save(any(Notification.class));
        verify(audit).log("REJECT", "SOLICITUDES", "12", "Solicitud rechazada");
    }

    @Test
    void contractCreateDefaultsToVigenteAndAudits() {
        ContractRepository contracts = mock(ContractRepository.class);
        EmployeeRepository employees = mock(EmployeeRepository.class);
        AuditService audit = mock(AuditService.class);
        ContractService service = new ContractService(contracts, employees, audit);
        Employee employee = employee(91L, "70707070", EmploymentStatus.ACTIVO);

        when(employees.findById(91L)).thenReturn(Optional.of(employee));
        when(contracts.save(any(LaborContract.class))).thenAnswer(invocation -> {
            LaborContract contract = invocation.getArgument(0);
            contract.setId(92L);
            return contract;
        });

        ContractDto dto = service.create(new ContractRequest(91L, "Indeterminado", LocalDate.of(2026, 1, 1), null, null));

        assertThat(dto.status()).isEqualTo(ContractStatus.VIGENTE);
        assertThat(dto.employeeId()).isEqualTo(91L);
        verify(audit).log("CREATE", "CONTRATOS", "92", "Indeterminado");
    }

    @Test
    void contractListFiltersByEmployee() {
        ContractRepository contracts = mock(ContractRepository.class);
        ContractService service = new ContractService(contracts, mock(EmployeeRepository.class), mock(AuditService.class));
        Employee employee = employee(93L, "71717171", EmploymentStatus.ACTIVO);
        LaborContract contract = contract(94L, employee);

        when(contracts.findByEmployeeId(93L)).thenReturn(List.of(contract));

        List<ContractDto> result = service.list(93L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(94L);
        verify(contracts).findByEmployeeId(93L);
    }

    @Test
    void documentUploadAllowsPdfAndRejectsUnsupportedFormats() throws Exception {
        DocumentRepository documents = mock(DocumentRepository.class);
        EmployeeRepository employees = mock(EmployeeRepository.class);
        RequestRepository requests = mock(RequestRepository.class);
        ContractRepository contracts = mock(ContractRepository.class);
        CurrentUserService current = mock(CurrentUserService.class);
        AppProperties props = appProperties();
        DocumentService service = new DocumentService(documents, employees, requests, contracts, current, props);
        Employee employee = employee(7L, "45678912", EmploymentStatus.ACTIVO);
        UserAccount rrhh = user("rrhh@empresa.com", Role.RRHH, employee);

        when(employees.findById(7L)).thenReturn(Optional.of(employee));
        when(current.account()).thenReturn(rrhh);
        when(documents.save(any(EmployeeDocument.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MockMultipartFile pdf = new MockMultipartFile("file", "contrato.pdf", "application/pdf", "PDF".getBytes());
        DocumentDto uploaded = service.uploadForEmployee(7L, pdf);

        assertThat(uploaded.originalName()).isEqualTo("contrato.pdf");
        assertThat(uploaded.uploadedBy()).isEqualTo("rrhh@empresa.com");
        assertThat(Files.list(tempDir).findAny()).isPresent();

        MockMultipartFile exe = new MockMultipartFile("file", "programa.exe", "application/octet-stream", "x".getBytes());
        assertThatThrownBy(() -> service.uploadForEmployee(7L, exe))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Formato no permitido. Usa PDF, Word o imagen.");
    }

    @Test
    void documentUploadAllowsWordContractFiles() throws Exception {
        DocumentRepository documents = mock(DocumentRepository.class);
        EmployeeRepository employees = mock(EmployeeRepository.class);
        RequestRepository requests = mock(RequestRepository.class);
        ContractRepository contracts = mock(ContractRepository.class);
        CurrentUserService current = mock(CurrentUserService.class);
        DocumentService service = new DocumentService(documents, employees, requests, contracts, current, appProperties());
        LaborContract contract = contract(101L, employee(102L, "81818181", EmploymentStatus.ACTIVO));

        when(contracts.findById(101L)).thenReturn(Optional.of(contract));
        when(current.account()).thenReturn(user("rrhh@empresa.com", Role.RRHH, null));
        when(documents.save(any(EmployeeDocument.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MockMultipartFile docx = new MockMultipartFile(
                "file",
                "contrato.docx",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "WORD".getBytes()
        );

        EmployeeDocument saved = service.uploadForContract(101L, docx);

        assertThat(saved.getOriginalName()).isEqualTo("contrato.docx");
        assertThat(saved.getContract()).isEqualTo(contract);
    }

    @Test
    void documentUploadRejectsEmptyFile() {
        DocumentService service = new DocumentService(mock(DocumentRepository.class), mock(EmployeeRepository.class), mock(RequestRepository.class), mock(ContractRepository.class), mock(CurrentUserService.class), appProperties());
        MockMultipartFile empty = new MockMultipartFile("file", "vacio.pdf", "application/pdf", new byte[0]);

        assertThatThrownBy(() -> service.uploadForEmployee(1L, empty))
                .isInstanceOf(BusinessException.class)
                .hasMessage("El archivo es obligatorio");
    }

    @Test
    void auditLogStoresActorIdentityDetails() {
        AuditLogRepository audits = mock(AuditLogRepository.class);
        UserRepository users = mock(UserRepository.class);
        AuditService service = new AuditService(audits, users);
        Employee actorEmployee = employee(3L, "99998888", EmploymentStatus.ACTIVO);
        actorEmployee.setFirstName("Ana");
        actorEmployee.setLastName("Mendoza");
        actorEmployee.setPosition("Recursos Humanos");
        UserAccount actor = user("ana@empresa.com", Role.RRHH, actorEmployee);
        SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken("ana@empresa.com", null));
        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);

        when(users.findByEmail("ana@empresa.com")).thenReturn(Optional.of(actor));

        service.log("UPDATE", "EMPLEADOS", "3", "Actualizo datos");

        verify(audits).save(captor.capture());
        AuditLog audit = captor.getValue();
        assertThat(audit.getActorEmail()).isEqualTo("ana@empresa.com");
        assertThat(audit.getActorName()).isEqualTo("Ana Mendoza");
        assertThat(audit.getActorDni()).isEqualTo("99998888");
        assertThat(audit.getActorPosition()).isEqualTo("Recursos Humanos");
        SecurityContextHolder.clearContext();
    }

    @Test
    void attendanceExcelImportCreatesAttendanceWithHoursAndTardiness() throws Exception {
        AttendanceRepository attendance = mock(AttendanceRepository.class);
        EmployeeRepository employees = mock(EmployeeRepository.class);
        CurrentUserService current = mock(CurrentUserService.class);
        AuditService audit = mock(AuditService.class);
        AttendanceService service = new AttendanceService(attendance, employees, current, audit, appProperties());
        LocalDate workDate = LocalDate.of(2026, 5, 20);
        Employee importedEmployee = employee(20L, "44556677", EmploymentStatus.ACTIVO);
        importedEmployee.setFirstName("Luis");
        importedEmployee.setLastName("Ramos");
        importedEmployee.setPosition("Operario");
        UserAccount rrhh = user("rrhh@empresa.com", Role.RRHH, null);
        List<Attendance> saved = new ArrayList<>();

        when(employees.findByDni("44556677")).thenReturn(Optional.empty());
        when(employees.save(any(Employee.class))).thenReturn(importedEmployee);
        when(attendance.findByEmployeeIdAndWorkDate(20L, workDate)).thenReturn(Optional.empty());
        when(attendance.save(any(Attendance.class))).thenAnswer(invocation -> {
            Attendance record = invocation.getArgument(0);
            saved.add(record);
            return record;
        });
        when(current.account()).thenReturn(rrhh);
        when(attendance.findByWorkDateBetween(workDate, workDate)).thenAnswer(invocation -> saved);

        List<AttendanceDto> result = service.importExcel(attendanceFile(), workDate);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).dni()).isEqualTo("44556677");
        assertThat(result.get(0).hoursWorked()).isEqualByComparingTo(new BigDecimal("8.5"));
        assertThat(result.get(0).late()).isTrue();
        assertThat(result.get(0).status()).isEqualTo(AttendanceStatus.TARDANZA);
        verify(audit).log("IMPORT", "ASISTENCIA", "2026-05-20", "Importación Excel de asistencia: 1 filas");
    }

    @Test
    void attendanceCheckInRejectsDuplicateEntry() {
        AttendanceRepository attendance = mock(AttendanceRepository.class);
        CurrentUserService current = mock(CurrentUserService.class);
        AttendanceService service = new AttendanceService(attendance, mock(EmployeeRepository.class), current, mock(AuditService.class), appProperties());
        Employee employee = employee(110L, "90909090", EmploymentStatus.ACTIVO);
        Attendance existing = attendanceRecord(111L, employee, LocalDate.now(), AttendanceStatus.PRESENTE);
        existing.setCheckIn(LocalTime.of(8, 1));

        when(current.account()).thenReturn(user("empleado@empresa.com", Role.EMPLEADO, employee));
        when(attendance.findByEmployeeIdAndWorkDate(110L, LocalDate.now())).thenReturn(Optional.of(existing));

        assertThatThrownBy(service::checkIn)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Ya registraste entrada para este turno");
    }

    @Test
    void attendanceCheckOutRequiresPreviousCheckIn() {
        AttendanceRepository attendance = mock(AttendanceRepository.class);
        CurrentUserService current = mock(CurrentUserService.class);
        AttendanceService service = new AttendanceService(attendance, mock(EmployeeRepository.class), current, mock(AuditService.class), appProperties());
        Employee employee = employee(112L, "91919191", EmploymentStatus.ACTIVO);

        when(current.account()).thenReturn(user("empleado@empresa.com", Role.EMPLEADO, employee));
        when(attendance.findByEmployeeIdAndWorkDate(112L, LocalDate.now())).thenReturn(Optional.empty());

        assertThatThrownBy(service::checkOut)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Primero debes registrar entrada");
    }

    @Test
    void attendanceCheckOutRejectsDuplicateExit() {
        AttendanceRepository attendance = mock(AttendanceRepository.class);
        CurrentUserService current = mock(CurrentUserService.class);
        AttendanceService service = new AttendanceService(attendance, mock(EmployeeRepository.class), current, mock(AuditService.class), appProperties());
        Employee employee = employee(113L, "92929292", EmploymentStatus.ACTIVO);
        Attendance existing = attendanceRecord(114L, employee, LocalDate.now(), AttendanceStatus.PRESENTE);
        existing.setCheckIn(LocalTime.of(8, 0));
        existing.setCheckOut(LocalTime.of(17, 0));

        when(current.account()).thenReturn(user("empleado@empresa.com", Role.EMPLEADO, employee));
        when(attendance.findByEmployeeIdAndWorkDate(113L, LocalDate.now())).thenReturn(Optional.of(existing));

        assertThatThrownBy(service::checkOut)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Ya registraste salida para este turno");
    }

    @Test
    void attendanceListForEmployeeIgnoresRequestedEmployeeAndUsesOwnId() {
        AttendanceRepository attendance = mock(AttendanceRepository.class);
        CurrentUserService current = mock(CurrentUserService.class);
        AttendanceService service = new AttendanceService(attendance, mock(EmployeeRepository.class), current, mock(AuditService.class), appProperties());
        Employee employee = employee(115L, "93939393", EmploymentStatus.ACTIVO);
        LocalDate from = LocalDate.of(2026, 5, 1);
        LocalDate to = LocalDate.of(2026, 5, 20);

        when(current.account()).thenReturn(user("empleado@empresa.com", Role.EMPLEADO, employee));
        when(attendance.findByEmployeeIdAndWorkDateBetween(115L, from, to)).thenReturn(List.of(attendanceRecord(116L, employee, from, AttendanceStatus.PRESENTE)));

        List<AttendanceDto> result = service.list(999L, null, from, to);

        assertThat(result).hasSize(1);
        verify(attendance).findByEmployeeIdAndWorkDateBetween(115L, from, to);
    }

    @Test
    void attendanceListForAdminCanFilterByArea() {
        AttendanceRepository attendance = mock(AttendanceRepository.class);
        CurrentUserService current = mock(CurrentUserService.class);
        AttendanceService service = new AttendanceService(attendance, mock(EmployeeRepository.class), current, mock(AuditService.class), appProperties());
        LocalDate from = LocalDate.of(2026, 5, 1);
        LocalDate to = LocalDate.of(2026, 5, 20);

        when(current.account()).thenReturn(user("admin@empresa.com", Role.ADMIN, null));
        when(attendance.findByEmployeeAreaContainingIgnoreCaseAndWorkDateBetween("RRHH", from, to)).thenReturn(List.of());

        List<AttendanceDto> result = service.list(null, "RRHH", from, to);

        assertThat(result).isEmpty();
        verify(attendance).findByEmployeeAreaContainingIgnoreCaseAndWorkDateBetween("RRHH", from, to);
    }

    @Test
    void attendanceImportRejectsEmptyExcel() {
        AttendanceService service = new AttendanceService(mock(AttendanceRepository.class), mock(EmployeeRepository.class), mock(CurrentUserService.class), mock(AuditService.class), appProperties());
        MockMultipartFile empty = new MockMultipartFile("file", "asistencia.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", new byte[0]);

        assertThatThrownBy(() -> service.importExcel(empty, LocalDate.of(2026, 5, 20)))
                .isInstanceOf(BusinessException.class)
                .hasMessage("El Excel es obligatorio");
    }

    @Test
    void reportExcelExportCreatesWorkbookBytes() {
        EmployeeService employeeService = mock(EmployeeService.class);
        AttendanceService attendanceService = mock(AttendanceService.class);
        RequestRepository requests = mock(RequestRepository.class);
        ReportService service = new ReportService(employeeService, attendanceService, requests);

        when(employeeService.active(null, null)).thenReturn(List.of(new EmployeeDto(1L, "Ana", "Perez", "123", null, "ana@empresa.com", "RRHH", "RRHH", "Lima", EmploymentStatus.ACTIVO, LocalDateTime.now(), null, null)));

        byte[] bytes = service.exportExcel("active-employees");

        assertThat(bytes).isNotEmpty();
        assertThat(bytes[0]).isEqualTo((byte) 'P');
        assertThat(bytes[1]).isEqualTo((byte) 'K');
    }

    @Test
    void notificationServiceReturnsCurrentUserNotifications() {
        NotificationRepository notifications = mock(NotificationRepository.class);
        CurrentUserService current = mock(CurrentUserService.class);
        NotificationService service = new NotificationService(notifications, current);
        Notification notification = new Notification();
        notification.setId(120L);
        notification.setMessage("Solicitud aprobada");

        when(current.account()).thenReturn(user("empleado@empresa.com", Role.EMPLEADO, employee(121L, "94949494", EmploymentStatus.ACTIVO)));
        when(notifications.findByRecipientIdOrderByCreatedAtDesc(100L)).thenReturn(List.of(notification));

        List<NotificationDto> result = service.mine();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).message()).isEqualTo("Solicitud aprobada");
    }

    @Test
    void mapperBuildsAttendanceDtoWithEmployeeIdentity() {
        Employee employee = employee(130L, "95959595", EmploymentStatus.ACTIVO);
        employee.setFirstName("Carla");
        employee.setLastName("Diaz");
        Attendance attendance = attendanceRecord(131L, employee, LocalDate.of(2026, 5, 20), AttendanceStatus.PRESENTE);
        attendance.setHoursWorked(new BigDecimal("8"));
        attendance.setImportedPosition("Supervisora");

        AttendanceDto dto = Mappers.attendance(attendance);

        assertThat(dto.employeeName()).isEqualTo("Carla Diaz");
        assertThat(dto.dni()).isEqualTo("95959595");
        assertThat(dto.importedPosition()).isEqualTo("Supervisora");
    }

    private AppProperties appProperties() {
        return new AppProperties(
                "12345678901234567890123456789012",
                60,
                tempDir.toString(),
                "http://localhost:5173,http://127.0.0.1:5173",
                "",
                false,
                new AppProperties.AttendanceProperties(LocalTime.of(8, 0), 10)
        );
    }

    private static EmployeeRequest employeeRequest(String dni) {
        return new EmployeeRequest("Maria", "Lopez", dni, "999999999", "maria@empresa.com", "Analista", "RRHH", "Lima", EmploymentStatus.ACTIVO);
    }

    private static Employee employee(Long id, String dni, EmploymentStatus status) {
        Employee employee = new Employee();
        employee.setId(id);
        employee.setFirstName("Nombre");
        employee.setLastName("Apellido");
        employee.setDni(dni);
        employee.setEmail("empleado" + dni + "@empresa.com");
        employee.setArea("RRHH");
        employee.setLocation("Lima");
        employee.setPosition("Asistente");
        employee.setEmploymentStatus(status);
        employee.setCreatedAt(LocalDateTime.now());
        return employee;
    }

    private static UserAccount user(String email, Role role, Employee employee) {
        UserAccount account = new UserAccount();
        account.setId(100L);
        account.setEmail(email);
        account.setPassword("hash");
        account.setRole(role);
        account.setStatus(AccountStatus.ACTIVO);
        account.setEmployee(employee);
        return account;
    }

    private static AbsenceRequest absenceRequest(Long id, Employee employee) {
        AbsenceRequest request = new AbsenceRequest();
        request.setId(id);
        request.setEmployee(employee);
        request.setType(RequestType.PERMISO);
        request.setStartDate(LocalDate.of(2026, 5, 20));
        request.setEndDate(LocalDate.of(2026, 5, 20));
        request.setReason("Cita medica");
        request.setStatus(RequestStatus.PENDIENTE);
        return request;
    }

    private static LaborContract contract(Long id, Employee employee) {
        LaborContract contract = new LaborContract();
        contract.setId(id);
        contract.setEmployee(employee);
        contract.setContractType("Plazo fijo");
        contract.setStartDate(LocalDate.of(2026, 1, 1));
        contract.setEndDate(LocalDate.of(2026, 12, 31));
        contract.setStatus(ContractStatus.VIGENTE);
        return contract;
    }

    private static Attendance attendanceRecord(Long id, Employee employee, LocalDate workDate, AttendanceStatus status) {
        Attendance attendance = new Attendance();
        attendance.setId(id);
        attendance.setEmployee(employee);
        attendance.setWorkDate(workDate);
        attendance.setStatus(status);
        attendance.setLate(status == AttendanceStatus.TARDANZA);
        return attendance;
    }

    private static MockMultipartFile attendanceFile() throws Exception {
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            var sheet = workbook.createSheet("Asistencia");
            var header = sheet.createRow(0);
            header.createCell(0).setCellValue("Nombre");
            header.createCell(1).setCellValue("DNI");
            header.createCell(2).setCellValue("Cargo");
            header.createCell(3).setCellValue("Horas trabajadas");
            header.createCell(4).setCellValue("Tardanza");
            var row = sheet.createRow(1);
            row.createCell(0).setCellValue("Luis Ramos");
            row.createCell(1).setCellValue("44556677");
            row.createCell(2).setCellValue("Operario");
            row.createCell(3).setCellValue("8.5");
            row.createCell(4).setCellValue("si");
            workbook.write(out);
            return new MockMultipartFile(
                    "file",
                    "asistencia.xlsx",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    out.toByteArray()
            );
        }
    }
}
