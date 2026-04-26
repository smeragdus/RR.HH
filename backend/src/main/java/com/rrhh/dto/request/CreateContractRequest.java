package com.rrhh.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CreateContractRequest {

    @NotNull(message = "El ID del empleado es requerido")
    private Long empleadoId;

    private String numeroContrato;

    @NotBlank(message = "El tipo de contrato es requerido")
    private String tipo;

    @NotNull(message = "La fecha de inicio es requerida")
    private LocalDate fechaInicio;

    private LocalDate fechaFin;

    @NotNull(message = "El salario es requerido")
    private BigDecimal salario;

    private String puesto;

    private String departamento;

    private String jornada;

    private BigDecimal salarioDiario;

    private BigDecimal sbcImss;

    private String prestaciones;

    private String observaciones;

    // Getters and Setters
    public Long getEmpleadoId() { return empleadoId; }
    public void setEmpleadoId(Long empleadoId) { this.empleadoId = empleadoId; }

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
}
