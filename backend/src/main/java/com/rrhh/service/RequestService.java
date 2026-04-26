package com.rrhh.service;

import com.rrhh.dto.request.CreateRequestRequest;
import com.rrhh.dto.request.RequestActionRequest;
import com.rrhh.dto.response.RequestResponse;
import com.rrhh.model.enums.RequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RequestService {
    Page<RequestResponse> getAllRequests(Pageable pageable);
    Page<RequestResponse> getRequestsByEmpleadoId(Long empleadoId, Pageable pageable);
    Page<RequestResponse> getRequestsByStatus(RequestStatus status, Pageable pageable);
    RequestResponse getRequestById(Long id);
    RequestResponse createRequest(Long empleadoId, CreateRequestRequest request);
    RequestResponse approveRequest(Long requestId, Long aprobadorId, RequestActionRequest actionRequest);
    RequestResponse rejectRequest(Long requestId, Long aprobadorId, RequestActionRequest actionRequest);
    RequestResponse cancelRequest(Long requestId);
    Page<RequestResponse> getPendingApprovals(Long empleadoId, Pageable pageable);
}
