package com.rrhh.service.impl;

import com.rrhh.dto.response.AttendanceResponse;
import com.rrhh.exception.BadRequestException;
import com.rrhh.exception.ResourceNotFoundException;
import com.rrhh.model.entity.Attendance;
import com.rrhh.model.entity.Employee;
import com.rrhh.repository.AttendanceRepository;
import com.rrhh.repository.EmployeeRepository;
import com.rrhh.service.AttendanceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;

    public AttendanceServiceImpl(AttendanceRepository attendanceRepository, EmployeeRepository employeeRepository) {
        this.attendanceRepository = attendanceRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    @Transactional
    public AttendanceResponse checkIn(Long empleadoId) {
        Employee employee = employeeRepository.findById(empleadoId)
                .orElseThrow(() -> new ResourceNotFoundException("Empleado", "id", empleadoId));

        LocalDate today = LocalDate.now();

        if (attendanceRepository.existsByEmpleadoIdAndFecha(empleadoId, today)) {
            throw new BadRequestException("Ya existe un registro de asistencia para hoy");
        }

        Attendance attendance = Attendance.builder()
                .empleado(employee)
                .fecha(today)
                .horaEntrada(LocalTime.now())
                .tipoJornada("ORDINARIA")
                .build();

        return AttendanceResponse.fromEntity(attendanceRepository.save(attendance));
    }

    @Override
    @Transactional
    public AttendanceResponse checkOut(Long empleadoId) {
        LocalDate today = LocalDate.now();

        Attendance attendance = attendanceRepository.findByEmpleadoIdAndFecha(empleadoId, today)
                .orElseThrow(() -> new BadRequestException("No existe un registro de entrada para hoy"));

        if (attendance.getHoraSalida() != null) {
            throw new BadRequestException("Ya se registró la salida para hoy");
        }

        attendance.setHoraSalida(LocalTime.now());
        attendance.calcularHorasTrabajadas();

        return AttendanceResponse.fromEntity(attendanceRepository.save(attendance));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AttendanceResponse> getAllAttendance(Pageable pageable) {
        return attendanceRepository.findAll(pageable)
                .map(AttendanceResponse::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AttendanceResponse> getAttendanceByDate(LocalDate fecha, Pageable pageable) {
        return attendanceRepository.findByFecha(fecha, pageable)
                .map(AttendanceResponse::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AttendanceResponse> getAttendanceByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return attendanceRepository.findByDateRange(startDate, endDate, pageable)
                .map(AttendanceResponse::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResponse> getTodayAttendance() {
        return attendanceRepository.findByFecha(LocalDate.now(), Pageable.unpaged())
                .stream()
                .map(AttendanceResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AttendanceResponse getAttendanceByEmpleadoAndFecha(Long empleadoId, LocalDate fecha) {
        Attendance attendance = attendanceRepository.findByEmpleadoIdAndFecha(empleadoId, fecha)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Asistencia no encontrada para empleado " + empleadoId + " en fecha " + fecha));
        return AttendanceResponse.fromEntity(attendance);
    }
}
