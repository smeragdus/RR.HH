package com.rrhh.service;

import com.rrhh.dto.request.CreateContractRequest;
import com.rrhh.dto.response.ContractResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ContractService {
    Page<ContractResponse> getAllContracts(Pageable pageable);
    Page<ContractResponse> getContractsByEmpleadoId(Long empleadoId, Pageable pageable);
    ContractResponse getContractById(Long id);
    ContractResponse createContract(CreateContractRequest request);
    ContractResponse updateContract(Long id, CreateContractRequest request);
    void deactivateContract(Long id);
}
