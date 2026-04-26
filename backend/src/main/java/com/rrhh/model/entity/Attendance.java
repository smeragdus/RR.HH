package com.rrhh.model.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "asistencia", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"empleado_id", "fecha"})
})
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empleado_id", nullable = false)
    private Employee empleado;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(name = "hora_entrada")
    private LocalTime horaEntrada;

    @Column(name = "hora_salida")
    private LocalTime horaSalida;

    @Column(name = "horas_trabajadas", precision = 4, scale = 2)
    private BigDecimal horasTrabajadas;

    @Column(name = "tipo_jornada", length = 20)
    private String tipoJornada = "ORDINARIA";

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Attendance() {
        this.tipoJornada = "ORDINARIA";
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public void calcularHorasTrabajadas() {
        if (horaEntrada != null && horaSalida != null) {
            long minutos = java.time.Duration.between(horaEntrada, horaSalida).toMinutes();
            this.horasTrabajadas = BigDecimal.valueOf(minutos / 60.0);
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Employee getEmpleado() { return empleado; }
    public void setEmpleado(Employee empleado) { this.empleado = empleado; }

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
    public static AttendanceBuilder builder() {
        return new AttendanceBuilder();
    }

    public static class AttendanceBuilder {
        private final Attendance attendance = new Attendance();

        public AttendanceBuilder id(Long id) { attendance.id = id; return this; }
        public AttendanceBuilder empleado(Employee empleado) { attendance.empleado = empleado; return this; }
        public AttendanceBuilder fecha(LocalDate fecha) { attendance.fecha = fecha; return this; }
        public AttendanceBuilder horaEntrada(LocalTime horaEntrada) { attendance.horaEntrada = horaEntrada; return this; }
        public AttendanceBuilder horaSalida(LocalTime horaSalida) { attendance.horaSalida = horaSalida; return this; }
        public AttendanceBuilder horasTrabajadas(BigDecimal horasTrabajadas) { attendance.horasTrabajadas = horasTrabajadas; return this; }
        public AttendanceBuilder tipoJornada(String tipoJornada) { attendance.tipoJornada = tipoJornada; return this; }
        public AttendanceBuilder observaciones(String observaciones) { attendance.observaciones = observaciones; return this; }

        public Attendance build() {
            return attendance;
        }
    }
}
