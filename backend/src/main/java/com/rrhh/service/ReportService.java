package com.rrhh.service;

import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface ReportService {
    byte[] exportAttendanceReportPdf(String startDate, String endDate);
    byte[] exportAttendanceReportExcel(String startDate, String endDate);
    byte[] exportEmployeesReportPdf();
    byte[] exportEmployeesReportExcel();
}
