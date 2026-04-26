package com.rrhh.service.impl;

import com.rrhh.dto.request.CreateRequestRequest;
import com.rrhh.dto.request.RequestActionRequest;
import com.rrhh.dto.response.RequestResponse;
import com.rrhh.exception.BadRequestException;
import com.rrhh.exception.ResourceNotFoundException;
import com.rrhh.model.entity.Attendance;
import com.rrhh.model.entity.Employee;
import com.rrhh.model.entity.Request;
import com.rrhh.model.enums.RequestStatus;
import com.rrhh.model.enums.RequestType;
import com.rrhh.repository.AttendanceRepository;
import com.rrhh.repository.EmployeeRepository;
import com.rrhh.repository.RequestRepository;
import com.rrhh.service.RequestService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;

    public RequestServiceImpl(RequestRepository requestRepository, EmployeeRepository employeeRepository,
                            AttendanceRepository attendanceRepository) {
        this.requestRepository = requestRepository;
        this.employeeRepository = employeeRepository;
        this.attendanceRepository = attendanceRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RequestResponse> getAllRequests(Pageable pageable) {
        return requestRepository.findAll(pageable).map(RequestResponse::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RequestResponse> getRequestsByEmpleadoId(Long empleadoId, Pageable pageable) {
        return requestRepository.findByEmpleadoId(empleadoId, pageable).map(RequestResponse::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RequestResponse> getRequestsByStatus(RequestStatus status, Pageable pageable) {
        return requestRepository.findByEstado(status, pageable).map(RequestResponse::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public RequestResponse getRequestById(Long id) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud", "id", id));
        return RequestResponse.fromEntity(request);
    }

    @Override
    @Transactional
    public RequestResponse createRequest(Long empleadoId, CreateRequestRequest requestDto) {
        Employee employee = employeeRepository.findById(empleadoId)
                .orElseThrow(() -> new ResourceNotFoundException("Empleado", "id", empleadoId));

        if (requestDto.getFechaFin().isBefore(requestDto.getFechaInicio())) {
            throw new BadRequestException("La fecha de fin debe ser posterior a la fecha de inicio");
        }

        long dias = ChronoUnit.DAYS.between(requestDto.getFechaInicio(), requestDto.getFechaFin()) + 1;

        Request request = Request.builder()
                .empleado(employee)
                .tipo(requestDto.getTipo())
                .fechaInicio(requestDto.getFechaInicio())
                .fechaFin(requestDto.getFechaFin())
                .diasSolicitados((int) dias)
                .motivo(requestDto.getMotivo())
                .estado(RequestStatus.PENDIENTE)
                .build();

        return RequestResponse.fromEntity(requestRepository.save(request));
    }

    @Override
    @Transactional
    public RequestResponse approveRequest(Long requestId, Long aprobadorId, RequestActionRequest actionRequest) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud", "id", requestId));

        if (request.getEstado() != RequestStatus.PENDIENTE) {
            throw new BadRequestException("La solicitud no está pendiente");
        }

        Employee aprobador = employeeRepository.findById(aprobadorId)
                .orElseThrow(() -> new ResourceNotFoundException("Empleado", "id", aprobadorId));

        request.setEstado(RequestStatus.APROBADA);
        request.setAprobadoPor(aprobador);
        request.setFechaAprobacion(LocalDateTime.now());
        request.setComentarios(actionRequest.getComentarios());

        // Si son vacaciones, registrar en asistencia
        if (request.getTipo() == RequestType.VACACIONES) {
            registerVacations(request);
        }

        return RequestResponse.fromEntity(requestRepository.save(request));
    }

    @Override
    @Transactional
    public RequestResponse rejectRequest(Long requestId, Long aprobadorId, RequestActionRequest actionRequest) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud", "id", requestId));

        if (request.getEstado() != RequestStatus.PENDIENTE) {
            throw new BadRequestException("La solicitud no está pendiente");
        }

        Employee aprobador = employeeRepository.findById(aprobadorId)
                .orElseThrow(() -> new ResourceNotFoundException("Empleado", "id", aprobadorId));

        request.setEstado(RequestStatus.RECHAZADA);
        request.setAprobadoPor(aprobador);
        request.setFechaAprobacion(LocalDateTime.now());
        request.setComentarios(actionRequest.getComentarios());

        return RequestResponse.fromEntity(requestRepository.save(request));
    }

    @Override
    @Transactional
    public RequestResponse cancelRequest(Long requestId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud", "id", requestId));

        if (request.getEstado() != RequestStatus.PENDIENTE) {
            throw new BadRequestException("Solo se pueden cancelar solicitudes pendientes");
        }

        request.setEstado(RequestStatus.CANCELADA);
        return RequestResponse.fromEntity(requestRepository.save(request));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RequestResponse> getPendingApprovals(Long empleadoId, Pageable pageable) {
        return requestRepository.findByEstado(RequestStatus.PENDIENTE, pageable)
                .map(RequestResponse::fromEntity);
    }

    private void registerVacations(Request request) {
        LocalDate current = request.getFechaInicio();
        while (!current.isAfter(request.getFechaFin())) {
            if (!attendanceRepository.existsByEmpleadoIdAndFecha(request.getEmpleado().getId(), current)) {
                Attendance attendance = Attendance.builder()
                        .empleado(request.getEmpleado())
                        .fecha(current)
                        .tipoJornada("VACACIONES")
                        .observaciones("Vacaciones aprobadas")
                        .build();
                attendanceRepository.save(attendance);
            }
            current = current.plusDays(1);
        }
    }
}
