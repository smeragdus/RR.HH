package com.rrhh.model.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "empleado")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_empleado", unique = true, nullable = false, length = 20)
    private String numeroEmpleado;

    @Column(nullable = false, length = 100)
    private String nombres;

    @Column(name = "apellido_paterno", nullable = false, length = 50)
    private String apellidoPaterno;

    @Column(name = "apellido_materno", length = 50)
    private String apellidoMaterno;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(length = 10)
    private String genero;

    @Column(columnDefinition = "TEXT")
    private String direccion;

    @Column(length = 20)
    private String telefono;

    @Column(name = "email_personal", length = 100)
    private String emailPersonal;

    @Column(name = "foto_url", length = 500)
    private String fotoUrl;

    @Column(length = 100)
    private String departamento;

    @Column(length = 100)
    private String puesto;

    @Column(name = "tipo_contrato", length = 50)
    private String tipoContrato;

    @Column(precision = 12, scale = 2)
    private BigDecimal salario;

    @Column(name = "fecha_ingreso")
    private LocalDate fechaIngreso;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private User usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supervisor_id")
    private Employee supervisor;

    @OneToMany(mappedBy = "supervisor", fetch = FetchType.LAZY)
    private List<Employee> subordinados = new ArrayList<>();

    @OneToMany(mappedBy = "empleado", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Attendance> asistenciaList = new ArrayList<>();

    @OneToMany(mappedBy = "empleado", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Request> solicitudes = new ArrayList<>();

    @OneToMany(mappedBy = "empleado", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Contract> contratos = new ArrayList<>();

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Employee() {
        this.subordinados = new ArrayList<>();
        this.asistenciaList = new ArrayList<>();
        this.solicitudes = new ArrayList<>();
        this.contratos = new ArrayList<>();
        this.activo = true;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNumeroEmpleado() { return numeroEmpleado; }
    public void setNumeroEmpleado(String numeroEmpleado) { this.numeroEmpleado = numeroEmpleado; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidoPaterno() { return apellidoPaterno; }
    public void setApellidoPaterno(String apellidoPaterno) { this.apellidoPaterno = apellidoPaterno; }

    public String getApellidoMaterno() { return apellidoMaterno; }
    public void setApellidoMaterno(String apellidoMaterno) { this.apellidoMaterno = apellidoMaterno; }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getEmailPersonal() { return emailPersonal; }
    public void setEmailPersonal(String emailPersonal) { this.emailPersonal = emailPersonal; }

    public String getFotoUrl() { return fotoUrl; }
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }

    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }

    public String getPuesto() { return puesto; }
    public void setPuesto(String puesto) { this.puesto = puesto; }

    public String getTipoContrato() { return tipoContrato; }
    public void setTipoContrato(String tipoContrato) { this.tipoContrato = tipoContrato; }

    public BigDecimal getSalario() { return salario; }
    public void setSalario(BigDecimal salario) { this.salario = salario; }

    public LocalDate getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(LocalDate fechaIngreso) { this.fechaIngreso = fechaIngreso; }

    public User getUsuario() { return usuario; }
    public void setUsuario(User usuario) { this.usuario = usuario; }

    public Employee getSupervisor() { return supervisor; }
    public void setSupervisor(Employee supervisor) { this.supervisor = supervisor; }

    public List<Employee> getSubordinados() { return subordinados; }
    public void setSubordinados(List<Employee> subordinados) { this.subordinados = subordinados; }

    public List<Attendance> getAsistenciaList() { return asistenciaList; }
    public void setAsistenciaList(List<Attendance> asistenciaList) { this.asistenciaList = asistenciaList; }

    public List<Request> getSolicitudes() { return solicitudes; }
    public void setSolicitudes(List<Request> solicitudes) { this.solicitudes = solicitudes; }

    public List<Contract> getContratos() { return contratos; }
    public void setContratos(List<Contract> contratos) { this.contratos = contratos; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getNombreCompleto() {
        return apellidoPaterno + (apellidoMaterno != null ? " " + apellidoMaterno : "") + " " + nombres;
    }

    public static EmployeeBuilder builder() {
        return new EmployeeBuilder();
    }

    public static class EmployeeBuilder {
        private final Employee employee = new Employee();

        public EmployeeBuilder id(Long id) { employee.id = id; return this; }
        public EmployeeBuilder numeroEmpleado(String numeroEmpleado) { employee.numeroEmpleado = numeroEmpleado; return this; }
        public EmployeeBuilder nombres(String nombres) { employee.nombres = nombres; return this; }
        public EmployeeBuilder apellidoPaterno(String apellidoPaterno) { employee.apellidoPaterno = apellidoPaterno; return this; }
        public EmployeeBuilder apellidoMaterno(String apellidoMaterno) { employee.apellidoMaterno = apellidoMaterno; return this; }
        public EmployeeBuilder fechaNacimiento(LocalDate fechaNacimiento) { employee.fechaNacimiento = fechaNacimiento; return this; }
        public EmployeeBuilder genero(String genero) { employee.genero = genero; return this; }
        public EmployeeBuilder direccion(String direccion) { employee.direccion = direccion; return this; }
        public EmployeeBuilder telefono(String telefono) { employee.telefono = telefono; return this; }
        public EmployeeBuilder emailPersonal(String emailPersonal) { employee.emailPersonal = emailPersonal; return this; }
        public EmployeeBuilder fotoUrl(String fotoUrl) { employee.fotoUrl = fotoUrl; return this; }
        public EmployeeBuilder departamento(String departamento) { employee.departamento = departamento; return this; }
        public EmployeeBuilder puesto(String puesto) { employee.puesto = puesto; return this; }
        public EmployeeBuilder tipoContrato(String tipoContrato) { employee.tipoContrato = tipoContrato; return this; }
        public EmployeeBuilder salario(BigDecimal salario) { employee.salario = salario; return this; }
        public EmployeeBuilder fechaIngreso(LocalDate fechaIngreso) { employee.fechaIngreso = fechaIngreso; return this; }
        public EmployeeBuilder usuario(User usuario) { employee.usuario = usuario; return this; }
        public EmployeeBuilder supervisor(Employee supervisor) { employee.supervisor = supervisor; return this; }
        public EmployeeBuilder activo(Boolean activo) { employee.activo = activo; return this; }

        public Employee build() {
            return employee;
        }
    }
}