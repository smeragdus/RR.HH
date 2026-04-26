package com.rrhh.dto.request;

import com.rrhh.model.enums.RequestType;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class CreateRequestRequest {

    @NotNull(message = "El tipo de solicitud es requerido")
    private RequestType tipo;

    @NotNull(message = "La fecha de inicio es requerida")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de fin es requerida")
    private LocalDate fechaFin;

    private String motivo;

    // Getters and Setters
    public RequestType getTipo() { return tipo; }
    public void setTipo(RequestType tipo) { this.tipo = tipo; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
}
