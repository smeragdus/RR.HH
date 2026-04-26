package com.rrhh.service;

import com.rrhh.dto.response.AttendanceResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {
    AttendanceResponse checkIn(Long empleadoId);
    AttendanceResponse checkOut(Long empleadoId);
    Page<AttendanceResponse> getAllAttendance(Pageable pageable);
    Page<AttendanceResponse> getAttendanceByDate(LocalDate fecha, Pageable pageable);
    Page<AttendanceResponse> getAttendanceByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable);
    List<AttendanceResponse> getTodayAttendance();
    AttendanceResponse getAttendanceByEmpleadoAndFecha(Long empleadoId, LocalDate fecha);
}
