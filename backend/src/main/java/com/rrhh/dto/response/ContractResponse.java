package com.rrhh.dto.response;

import com.rrhh.model.entity.Contract;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ContractResponse {

    private Long id;
    private Long empleadoId;
    private String numeroEmpleado;
    private String nombreEmpleado;
    private String numeroContrato;
    private String tipo;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private BigDecimal salario;
    private String puesto;
    private String departamento;
    private String jornada;
    private BigDecimal salarioDiario;
    private BigDecimal sbcImss;
    private String prestaciones;
    private String observaciones;
    private String documentoUrl;
    private Boolean activo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ContractResponse() {}

    public static ContractResponse fromEntity(Contract contract) {
        ContractResponse response = new ContractResponse();
        response.setId(contract.getId());
        response.setEmpleadoId(contract.getEmpleado().getId());
        response.setNumeroEmpleado(contract.getEmpleado().getNumeroEmpleado());
        response.setNombreEmpleado(contract.getEmpleado().getNombreCompleto());
        response.setNumeroContrato(contract.getNumeroContrato());
        response.setTipo(contract.getTipo());
        response.setFechaInicio(contract.getFechaInicio());
        response.setFechaFin(contract.getFechaFin());
        response.setSalario(contract.getSalario());
        response.setPuesto(contract.getPuesto());
        response.setDepartamento(contract.getDepartamento());
        response.setJornada(contract.getJornada());
        response.setSalarioDiario(contract.getSalarioDiario());
        response.setSbcImss(contract.getSbcImss());
        response.setPrestaciones(contract.getPrestaciones());
        response.setObservaciones(contract.getObservaciones());
        response.setDocumentoUrl(contract.getDocumentoUrl());
        response.setActivo(contract.getActivo());
        response.setCreatedAt(contract.getCreatedAt());
        response.setUpdatedAt(contract.getUpdatedAt());
        return response;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEmpleadoId() { return empleadoId; }
    public void setEmpleadoId(Long empleadoId) { this.empleadoId = empleadoId; }

    public String getNumeroEmpleado() { return numeroEmpleado; }
    public void setNumeroEmpleado(String numeroEmpleado) { this.numeroEmpleado = numeroEmpleado; }

    public String getNombreEmpleado() { return nombreEmpleado; }
    public void setNombreEmpleado(String nombreEmpleado) { this.nombreEmpleado = nombreEmpleado; }

    public String getNumeroContrato() { return numeroContrato; }
    public void setNumeroContrato(String numeroContrato) { this.numeroContrato = numeroContrato; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public BigDecimal getSalario() { return salario; }
    public void setSalario(BigDecimal salario) { this.salario = salario; }

    public String getPuesto() { return puesto; }
    public void setPuesto(String puesto) { this.puesto = puesto; }

    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }

    public String getJornada() { return jornada; }
    public void setJornada(String jornada) { this.jornada = jornada; }

    public BigDecimal getSalarioDiario() { return salarioDiario; }
    public void setSalarioDiario(BigDecimal salarioDiario) { this.salarioDiario = salarioDiario; }

    public BigDecimal getSbcImss() { return sbcImss; }
    public void setSbcImss(BigDecimal sbcImss) { this.sbcImss = sbcImss; }

    public String getPrestaciones() { return prestaciones; }
    public void setPrestaciones(String prestaciones) { this.prestaciones = prestaciones; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public String getDocumentoUrl() { return documentoUrl; }
    public void setDocumentoUrl(String documentoUrl) { this.documentoUrl = documentoUrl; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
