package com.rrhh.dto.response;

import com.rrhh.model.entity.Employee;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class EmployeeResponse {

    private Long id;
    private String numeroEmpleado;
    private String nombres;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String nombreCompleto;
    private LocalDate fechaNacimiento;
    private String genero;
    private String direccion;
    private String telefono;
    private String emailPersonal;
    private String fotoUrl;
    private String departamento;
    private String puesto;
    private String tipoContrato;
    private BigDecimal salario;
    private LocalDate fechaIngreso;
    private Long usuarioId;
    private String username;
    private String email;
    private String rol;
    private Long supervisorId;
    private String supervisorNombre;
    private Boolean activo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public EmployeeResponse() {}

    public static EmployeeResponse fromEntity(Employee employee) {
        EmployeeResponse response = new EmployeeResponse();
        response.setId(employee.getId());
        response.setNumeroEmpleado(employee.getNumeroEmpleado());
        response.setNombres(employee.getNombres());
        response.setApellidoPaterno(employee.getApellidoPaterno());
        response.setApellidoMaterno(employee.getApellidoMaterno());
        response.setNombreCompleto(employee.getNombreCompleto());
        response.setFechaNacimiento(employee.getFechaNacimiento());
        response.setGenero(employee.getGenero());
        response.setDireccion(employee.getDireccion());
        response.setTelefono(employee.getTelefono());
        response.setEmailPersonal(employee.getEmailPersonal());
        response.setFotoUrl(employee.getFotoUrl());
        response.setDepartamento(employee.getDepartamento());
        response.setPuesto(employee.getPuesto());
        response.setTipoContrato(employee.getTipoContrato());
        response.setSalario(employee.getSalario());
        response.setFechaIngreso(employee.getFechaIngreso());
        response.setUsuarioId(employee.getUsuario() != null ? employee.getUsuario().getId() : null);
        response.setUsername(employee.getUsuario() != null ? employee.getUsuario().getUsername() : null);
        response.setEmail(employee.getUsuario() != null ? employee.getUsuario().getEmail() : null);
        response.setRol(employee.getUsuario() != null ? employee.getUsuario().getRol().name() : null);
        response.setSupervisorId(employee.getSupervisor() != null ? employee.getSupervisor().getId() : null);
        response.setSupervisorNombre(employee.getSupervisor() != null ? employee.getSupervisor().getNombreCompleto() : null);
        response.setActivo(employee.getActivo());
        response.setCreatedAt(employee.getCreatedAt());
        response.setUpdatedAt(employee.getUpdatedAt());
        return response;
    }

    // Getters and Setters
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

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

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

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public Long getSupervisorId() { return supervisorId; }
    public void setSupervisorId(Long supervisorId) { this.supervisorId = supervisorId; }

    public String getSupervisorNombre() { return supervisorNombre; }
    public void setSupervisorNombre(String supervisorNombre) { this.supervisorNombre = supervisorNombre; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Static builder method
    public static EmployeeResponseBuilder builder() {
        return new EmployeeResponseBuilder();
    }

    public static class EmployeeResponseBuilder {
        private final EmployeeResponse response = new EmployeeResponse();

        public EmployeeResponseBuilder id(Long id) { response.id = id; return this; }
        public EmployeeResponseBuilder numeroEmpleado(String numeroEmpleado) { response.numeroEmpleado = numeroEmpleado; return this; }
        public EmployeeResponseBuilder nombres(String nombres) { response.nombres = nombres; return this; }
        public EmployeeResponseBuilder apellidoPaterno(String apellidoPaterno) { response.apellidoPaterno = apellidoPaterno; return this; }
        public EmployeeResponseBuilder apellidoMaterno(String apellidoMaterno) { response.apellidoMaterno = apellidoMaterno; return this; }
        public EmployeeResponseBuilder nombreCompleto(String nombreCompleto) { response.nombreCompleto = nombreCompleto; return this; }
        public EmployeeResponseBuilder fechaNacimiento(LocalDate fechaNacimiento) { response.fechaNacimiento = fechaNacimiento; return this; }
        public EmployeeResponseBuilder genero(String genero) { response.genero = genero; return this; }
        public EmployeeResponseBuilder direccion(String direccion) { response.direccion = direccion; return this; }
        public EmployeeResponseBuilder telefono(String telefono) { response.telefono = telefono; return this; }
        public EmployeeResponseBuilder emailPersonal(String emailPersonal) { response.emailPersonal = emailPersonal; return this; }
        public EmployeeResponseBuilder fotoUrl(String fotoUrl) { response.fotoUrl = fotoUrl; return this; }
        public EmployeeResponseBuilder departamento(String departamento) { response.departamento = departamento; return this; }
        public EmployeeResponseBuilder puesto(String puesto) { response.puesto = puesto; return this; }
        public EmployeeResponseBuilder tipoContrato(String tipoContrato) { response.tipoContrato = tipoContrato; return this; }
        public EmployeeResponseBuilder salario(BigDecimal salario) { response.salario = salario; return this; }
        public EmployeeResponseBuilder fechaIngreso(LocalDate fechaIngreso) { response.fechaIngreso = fechaIngreso; return this; }
        public EmployeeResponseBuilder usuarioId(Long usuarioId) { response.usuarioId = usuarioId; return this; }
        public EmployeeResponseBuilder username(String username) { response.username = username; return this; }
        public EmployeeResponseBuilder email(String email) { response.email = email; return this; }
        public EmployeeResponseBuilder rol(String rol) { response.rol = rol; return this; }
        public EmployeeResponseBuilder supervisorId(Long supervisorId) { response.supervisorId = supervisorId; return this; }
        public EmployeeResponseBuilder supervisorNombre(String supervisorNombre) { response.supervisorNombre = supervisorNombre; return this; }
        public EmployeeResponseBuilder activo(Boolean activo) { response.activo = activo; return this; }
        public EmployeeResponseBuilder createdAt(LocalDateTime createdAt) { response.createdAt = createdAt; return this; }
        public EmployeeResponseBuilder updatedAt(LocalDateTime updatedAt) { response.updatedAt = updatedAt; return this; }

        public EmployeeResponse build() {
            return response;
        }
    }
}
