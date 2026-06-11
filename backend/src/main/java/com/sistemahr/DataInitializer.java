package com.sistemahr;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
class DataInitializer implements CommandLineRunner {
    private final UserRepository users;
    private final EmployeeRepository employees;
    private final PasswordEncoder encoder;
    private final AppProperties props;

    @Override
    @Transactional
    public void run(String... args) {
        if (users.existsByEmail("admin@sistemahr.local")) {
            return;
        }
        if (props.initialAdminPassword() == null || props.initialAdminPassword().isBlank()) {
            return;
        }
        Employee adminEmployee = employee("Admin", "Sistema", "00000001", "admin@sistemahr.local", "Administrador", "Direccion", "Principal");
        Employee rrhhEmployee = employee("Rosa", "Recursos", "00000002", "rrhh@sistemahr.local", "Analista RRHH", "RRHH", "Principal");
        Employee bossEmployee = employee("Juan", "Jefe", "00000003", "jefe@sistemahr.local", "Jefe de Area", "Operaciones", "Principal");
        Employee workerEmployee = employee("Elena", "Empleado", "00000004", "empleado@sistemahr.local", "Asistente", "Operaciones", "Principal");
        create("admin@sistemahr.local", Role.ADMIN, adminEmployee);
        create("rrhh@sistemahr.local", Role.RRHH, rrhhEmployee);
        create("jefe@sistemahr.local", Role.JEFE, bossEmployee);
        create("empleado@sistemahr.local", Role.EMPLEADO, workerEmployee);
    }

    private Employee employee(String firstName, String lastName, String dni, String email, String position, String area, String location) {
        Employee employee = new Employee();
        employee.setFirstName(firstName);
        employee.setLastName(lastName);
        employee.setDni(dni);
        employee.setEmail(email);
        employee.setPosition(position);
        employee.setArea(area);
        employee.setLocation(location);
        return employees.save(employee);
    }

    private void create(String email, Role role, Employee employee) {
        UserAccount user = new UserAccount();
        user.setEmail(email);
        user.setPassword(encoder.encode(props.initialAdminPassword()));
        user.setRole(role);
        user.setEmployee(employee);
        users.save(user);
    }
}
