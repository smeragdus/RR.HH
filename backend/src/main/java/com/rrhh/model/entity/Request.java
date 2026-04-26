package com.rrhh.model.entity;

import com.rrhh.model.enums.RequestStatus;
import com.rrhh.model.enums.RequestType;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "solicitud")
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empleado_id", nullable = false)
    private Employee empleado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RequestType tipo;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Column(name = "dias_solicitados", nullable = false)
    private Integer diasSolicitados;

    @Column(columnDefinition = "TEXT")
    private String motivo;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private RequestStatus estado = RequestStatus.PENDIENTE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aprobado_por")
    private Employee aprobadoPor;

    @Column(name = "fecha_aprobacion")
    private LocalDateTime fechaAprobacion;

    @Column(columnDefinition = "TEXT")
    private String comentarios;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Request() {
        this.estado = RequestStatus.PENDIENTE;
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

    public RequestType getTipo() { return tipo; }
    public void setTipo(RequestType tipo) { this.tipo = tipo; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public Integer getDiasSolicitados() { return diasSolicitados; }
    public void setDiasSolicitados(Integer diasSolicitados) { this.diasSolicitados = diasSolicitados; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public RequestStatus getEstado() { return estado; }
    public void setEstado(RequestStatus estado) { this.estado = estado; }

    public Employee getAprobadoPor() { return aprobadoPor; }
    public void setAprobadoPor(Employee aprobadoPor) { this.aprobadoPor = aprobadoPor; }

    public LocalDateTime getFechaAprobacion() { return fechaAprobacion; }
    public void setFechaAprobacion(LocalDateTime fechaAprobacion) { this.fechaAprobacion = fechaAprobacion; }

    public String getComentarios() { return comentarios; }
    public void setComentarios(String comentarios) { this.comentarios = comentarios; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Static builder method
    public static RequestBuilder builder() {
        return new RequestBuilder();
    }

    public static class RequestBuilder {
        private final Request request = new Request();

        public RequestBuilder id(Long id) { request.id = id; return this; }
        public RequestBuilder empleado(Employee empleado) { request.empleado = empleado; return this; }
        public RequestBuilder tipo(RequestType tipo) { request.tipo = tipo; return this; }
        public RequestBuilder fechaInicio(LocalDate fechaInicio) { request.fechaInicio = fechaInicio; return this; }
        public RequestBuilder fechaFin(LocalDate fechaFin) { request.fechaFin = fechaFin; return this; }
        public RequestBuilder diasSolicitados(Integer diasSolicitados) { request.diasSolicitados = diasSolicitados; return this; }
        public RequestBuilder motivo(String motivo) { request.motivo = motivo; return this; }
        public RequestBuilder estado(RequestStatus estado) { request.estado = estado; return this; }
        public RequestBuilder aprobadoPor(Employee aprobadoPor) { request.aprobadoPor = aprobadoPor; return this; }
        public RequestBuilder fechaAprobacion(LocalDateTime fechaAprobacion) { request.fechaAprobacion = fechaAprobacion; return this; }
        public RequestBuilder comentarios(String comentarios) { request.comentarios = comentarios; return this; }

        public Request build() {
            return request;
        }
    }
}
