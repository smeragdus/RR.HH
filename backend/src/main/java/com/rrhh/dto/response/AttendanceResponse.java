package com.rrhh.dto.response;

import com.rrhh.model.entity.Attendance;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class AttendanceResponse {

    private Long id;
    private Long empleadoId;
    private String numeroEmpleado;
    private String nombreEmpleado;
    private LocalDate fecha;
    private LocalTime horaEntrada;
    private LocalTime horaSalida;
    private BigDecimal horasTrabajadas;
    private String tipoJornada;
    private String observaciones;
    private LocalDateTime createdAt;

    public AttendanceResponse() {}

    public static AttendanceResponse fromEntity(Attendance attendance) {
        AttendanceResponse response = new AttendanceResponse();
        response.setId(attendance.getId());
        response.setEmpleadoId(attendance.getEmpleado().getId());
        response.setNumeroEmpleado(attendance.getEmpleado().getNumeroEmpleado());
        response.setNombreEmpleado(attendance.getEmpleado().getNombreCompleto());
        response.setFecha(attendance.getFecha());
        response.setHoraEntrada(attendance.getHoraEntrada());
        response.setHoraSalida(attendance.getHoraSalida());
        response.setHorasTrabajadas(attendance.getHorasTrabajadas());
        response.setTipoJornada(attendance.getTipoJornada());
        response.setObservaciones(attendance.getObservaciones());
        response.setCreatedAt(attendance.getCreatedAt());
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

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public LocalTime getHoraEntrada() { return horaEntrada; }
    public void setHoraEntrada(LocalTime horaEntrada) { this.horaEntrada = horaEntrada; }

    public LocalTime getHoraSalida() { return horaSalida; }
    public void setHoraSalida(LocalTime horaSalida) { this.horaSalida = horaSalida; }

    public BigDecimal getHorasTrabajadas() { return horasTrabajadas; }
    public void setHorasTrabajadas(BigDecimal horasTrabajadas) { this.horasTrabajadas = horasTrabajadas; }

    public String getTipoJornada() { return tipoJornada; }
    public void setTipoJornada(String tipoJornada) { this.tipoJornada = tipoJornada; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // Static builder method
    public static AttendanceResponseBuilder builder() {
        return new AttendanceResponseBuilder();
    }

    public static class AttendanceResponseBuilder {
        private final AttendanceResponse response = new AttendanceResponse();

        public AttendanceResponseBuilder id(Long id) { response.id = id; return this; }
        public AttendanceResponseBuilder empleadoId(Long empleadoId) { response.empleadoId = empleadoId; return this; }
        public AttendanceResponseBuilder numeroEmpleado(String numeroEmpleado) { response.numeroEmpleado = numeroEmpleado; return this; }
        public AttendanceResponseBuilder nombreEmpleado(String nombreEmpleado) { response.nombreEmpleado = nombreEmpleado; return this; }
        public AttendanceResponseBuilder fecha(LocalDate fecha) { response.fecha = fecha; return this; }
        public AttendanceResponseBuilder horaEntrada(LocalTime horaEntrada) { response.horaEntrada = horaEntrada; return this; }
        public AttendanceResponseBuilder horaSalida(LocalTime horaSalida) { response.horaSalida = horaSalida; return this; }
        public AttendanceResponseBuilder horasTrabajadas(BigDecimal horasTrabajadas) { response.horasTrabajadas = horasTrabajadas; return this; }
        public AttendanceResponseBuilder tipoJornada(String tipoJornada) { response.tipoJornada = tipoJornada; return this; }
        public AttendanceResponseBuilder observaciones(String observaciones) { response.observaciones = observaciones; return this; }
        public AttendanceResponseBuilder createdAt(LocalDateTime createdAt) { response.createdAt = createdAt; return this; }

        public AttendanceResponse build() {
            return response;
        }
    }
}
