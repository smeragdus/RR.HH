package com.rrhh.controller;

import com.rrhh.dto.request.CreateRequestRequest;
import com.rrhh.dto.request.RequestActionRequest;
import com.rrhh.dto.response.ApiResponse;
import com.rrhh.dto.response.RequestResponse;
import com.rrhh.model.enums.RequestStatus;
import com.rrhh.security.CustomUserDetails;
import com.rrhh.service.RequestService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/requests")
public class RequestController {

    private final RequestService requestService;

    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RRHH', 'JEFE', 'EMPLEADO')")
    public ResponseEntity<ApiResponse<Page<RequestResponse>>> getAllRequests(
            @RequestParam(required = false) RequestStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<RequestResponse> requests;
        if (status != null) {
            requests = requestService.getRequestsByStatus(status, pageable);
        } else {
            requests = requestService.getAllRequests(pageable);
        }
        return ResponseEntity.ok(ApiResponse.success(requests));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RRHH', 'JEFE', 'EMPLEADO')")
    public ResponseEntity<ApiResponse<RequestResponse>> getRequestById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(requestService.getRequestById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RRHH', 'JEFE', 'EMPLEADO')")
    public ResponseEntity<ApiResponse<RequestResponse>> createRequest(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CreateRequestRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Solicitud creada",
                requestService.createRequest(userDetails.getEmpleadoId(), request)));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'RRHH', 'JEFE')")
    public ResponseEntity<ApiResponse<RequestResponse>> approveRequest(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody(required = false) RequestActionRequest actionRequest) {
        if (actionRequest == null) {
            actionRequest = new RequestActionRequest();
        }
        return ResponseEntity.ok(ApiResponse.success("Solicitud aprobada",
                requestService.approveRequest(id, userDetails.getEmpleadoId(), actionRequest)));
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'RRHH', 'JEFE')")
    public ResponseEntity<ApiResponse<RequestResponse>> rejectRequest(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody(required = false) RequestActionRequest actionRequest) {
        if (actionRequest == null) {
            actionRequest = new RequestActionRequest();
        }
        return ResponseEntity.ok(ApiResponse.success("Solicitud rechazada",
                requestService.rejectRequest(id, userDetails.getEmpleadoId(), actionRequest)));
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'RRHH', 'JEFE', 'EMPLEADO')")
    public ResponseEntity<ApiResponse<RequestResponse>> cancelRequest(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Solicitud cancelada",
                requestService.cancelRequest(id)));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('ADMIN', 'RRHH', 'JEFE')")
    public ResponseEntity<ApiResponse<Page<RequestResponse>>> getPendingApprovals(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                requestService.getPendingApprovals(userDetails.getEmpleadoId(), pageable)));
    }

    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('ADMIN', 'RRHH', 'JEFE', 'EMPLEADO')")
    public ResponseEntity<ApiResponse<Page<RequestResponse>>> getMyRequests(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                requestService.getRequestsByEmpleadoId(userDetails.getEmpleadoId(), pageable)));
    }
}
