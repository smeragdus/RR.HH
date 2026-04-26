package com.rrhh.model.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "contrato")
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empleado_id", nullable = false)
    private Employee empleado;

    @Column(name = "numero_contrato", unique = true, nullable = false, length = 30)
    private String numeroContrato;

    @Column(nullable = false, length = 50)
    private String tipo;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal salario;

    @Column(length = 100)
    private String puesto;

    @Column(length = 100)
    private String departamento;

    @Column(length = 30)
    private String jornada;

    @Column(name = "salario_diario", precision = 12, scale = 2)
    private BigDecimal salarioDiario;

    @Column(name = "sbc_imss", precision = 12, scale = 2)
    private BigDecimal sbcImss;

    @Column(columnDefinition = "TEXT")
    private String prestaciones;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "documento_url", length = 500)
    private String documentoUrl;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Contract() {
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

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Employee getEmpleado() { return empleado; }
    public void setEmpleado(Employee empleado) { this.empleado = empleado; }

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

    // Static builder method
    public static ContractBuilder builder() {
        return new ContractBuilder();
    }

    public static class ContractBuilder {
        private final Contract contract = new Contract();

        public ContractBuilder id(Long id) { contract.id = id; return this; }
        public ContractBuilder empleado(Employee empleado) { contract.empleado = empleado; return this; }
        public ContractBuilder numeroContrato(String numeroContrato) { contract.numeroContrato = numeroContrato; return this; }
        public ContractBuilder tipo(String tipo) { contract.tipo = tipo; return this; }
        public ContractBuilder fechaInicio(LocalDate fechaInicio) { contract.fechaInicio = fechaInicio; return this; }
        public ContractBuilder fechaFin(LocalDate fechaFin) { contract.fechaFin = fechaFin; return this; }
        public ContractBuilder salario(BigDecimal salario) { contract.salario = salario; return this; }
        public ContractBuilder puesto(String puesto) { contract.puesto = puesto; return this; }
        public ContractBuilder departamento(String departamento) { contract.departamento = departamento; return this; }
        public ContractBuilder jornada(String jornada) { contract.jornada = jornada; return this; }
        public ContractBuilder salarioDiario(BigDecimal salarioDiario) { contract.salarioDiario = salarioDiario; return this; }
        public ContractBuilder sbcImss(BigDecimal sbcImss) { contract.sbcImss = sbcImss; return this; }
        public ContractBuilder prestaciones(String prestaciones) { contract.prestaciones = prestaciones; return this; }
        public ContractBuilder observaciones(String observaciones) { contract.observaciones = observaciones; return this; }
        public ContractBuilder documentoUrl(String documentoUrl) { contract.documentoUrl = documentoUrl; return this; }
        public ContractBuilder activo(Boolean activo) { contract.activo = activo; return this; }

        public Contract build() {
            return contract;
        }
    }
}
