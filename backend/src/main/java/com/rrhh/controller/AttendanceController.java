package com.rrhh.controller;

import com.rrhh.dto.response.ApiResponse;
import com.rrhh.dto.response.AttendanceResponse;
import com.rrhh.security.CustomUserDetails;
import com.rrhh.service.AttendanceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping("/checkin")
    @PreAuthorize("hasAnyRole('ADMIN', 'RRHH', 'JEFE', 'EMPLEADO')")
    public ResponseEntity<ApiResponse<AttendanceResponse>> checkIn(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        AttendanceResponse response = attendanceService.checkIn(userDetails.getEmpleadoId());
        return ResponseEntity.ok(ApiResponse.success("Check-in registrado", response));
    }

    @PostMapping("/checkout")
    @PreAuthorize("hasAnyRole('ADMIN', 'RRHH', 'JEFE', 'EMPLEADO')")
    public ResponseEntity<ApiResponse<AttendanceResponse>> checkOut(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        AttendanceResponse response = attendanceService.checkOut(userDetails.getEmpleadoId());
        return ResponseEntity.ok(ApiResponse.success("Check-out registrado", response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RRHH')")
    public ResponseEntity<ApiResponse<Page<AttendanceResponse>>> getAllAttendance(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<AttendanceResponse> attendance;
        if (fecha != null) {
            attendance = attendanceService.getAttendanceByDate(fecha, pageable);
        } else if (startDate != null && endDate != null) {
            attendance = attendanceService.getAttendanceByDateRange(startDate, endDate, pageable);
        } else {
            attendance = attendanceService.getAllAttendance(pageable);
        }
        return ResponseEntity.ok(ApiResponse.success(attendance));
    }

    @GetMapping("/today")
    @PreAuthorize("hasAnyRole('ADMIN', 'RRHH')")
    public ResponseEntity<ApiResponse<List<AttendanceResponse>>> getTodayAttendance() {
        return ResponseEntity.ok(ApiResponse.success(attendanceService.getTodayAttendance()));
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('ADMIN', 'RRHH', 'JEFE', 'EMPLEADO')")
    public ResponseEntity<ApiResponse<AttendanceResponse>> getMyAttendanceToday(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(
                attendanceService.getAttendanceByEmpleadoAndFecha(userDetails.getEmpleadoId(), LocalDate.now())));
    }
}
