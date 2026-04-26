package com.rrhh.dto.response;

import com.rrhh.model.entity.Request;
import com.rrhh.model.enums.RequestStatus;
import com.rrhh.model.enums.RequestType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class RequestResponse {

    private Long id;
    private Long empleadoId;
    private String numeroEmpleado;
    private String nombreEmpleado;
    private RequestType tipo;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Integer diasSolicitados;
    private String motivo;
    private RequestStatus estado;
    private Long aprobadoPorId;
    private String aprobadoPorNombre;
    private LocalDateTime fechaAprobacion;
    private String comentarios;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public RequestResponse() {}

    public static RequestResponse fromEntity(Request request) {
        RequestResponse response = new RequestResponse();
        response.setId(request.getId());
        response.setEmpleadoId(request.getEmpleado().getId());
        response.setNumeroEmpleado(request.getEmpleado().getNumeroEmpleado());
        response.setNombreEmpleado(request.getEmpleado().getNombreCompleto());
        response.setTipo(request.getTipo());
        response.setFechaInicio(request.getFechaInicio());
        response.setFechaFin(request.getFechaFin());
        response.setDiasSolicitados(request.getDiasSolicitados());
        response.setMotivo(request.getMotivo());
        response.setEstado(request.getEstado());
        response.setAprobadoPorId(request.getAprobadoPor() != null ? request.getAprobadoPor().getId() : null);
        response.setAprobadoPorNombre(request.getAprobadoPor() != null ? request.getAprobadoPor().getNombreCompleto() : null);
        response.setFechaAprobacion(request.getFechaAprobacion());
        response.setComentarios(request.getComentarios());
        response.setCreatedAt(request.getCreatedAt());
        response.setUpdatedAt(request.getUpdatedAt());
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

    public Long getAprobadoPorId() { return aprobadoPorId; }
    public void setAprobadoPorId(Long aprobadoPorId) { this.aprobadoPorId = aprobadoPorId; }

    public String getAprobadoPorNombre() { return aprobadoPorNombre; }
    public void setAprobadoPorNombre(String aprobadoPorNombre) { this.aprobadoPorNombre = aprobadoPorNombre; }

    public LocalDateTime getFechaAprobacion() { return fechaAprobacion; }
    public void setFechaAprobacion(LocalDateTime fechaAprobacion) { this.fechaAprobacion = fechaAprobacion; }

    public String getComentarios() { return comentarios; }
    public void setComentarios(String comentarios) { this.comentarios = comentarios; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
