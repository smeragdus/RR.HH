package com.rrhh.service.impl;

import com.rrhh.dto.response.AttendanceResponse;
import com.rrhh.dto.response.EmployeeResponse;
import com.rrhh.repository.AttendanceRepository;
import com.rrhh.repository.EmployeeRepository;
import com.rrhh.service.ReportService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class ReportServiceImpl implements ReportService {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;

    public ReportServiceImpl(AttendanceRepository attendanceRepository, EmployeeRepository employeeRepository) {
        this.attendanceRepository = attendanceRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public byte[] exportAttendanceReportPdf(String startDate, String endDate) {
        // Placeholder - JasperReports requiere templates .jrxml y configuración adicional
        // Por ahora retornamos un mensaje en bytes
        String message = "Reporte PDF de Asistencia: " + startDate + " a " + endDate;
        return message.getBytes();
    }

    @Override
    public byte[] exportAttendanceReportExcel(String startDate, String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        Page<AttendanceResponse> attendancePage = attendanceRepository
                .findByDateRange(start, end, Pageable.unpaged())
                .map(a -> AttendanceResponse.builder()
                        .id(a.getId())
                        .empleadoId(a.getEmpleado().getId())
                        .numeroEmpleado(a.getEmpleado().getNumeroEmpleado())
                        .nombreEmpleado(a.getEmpleado().getNombreCompleto())
                        .fecha(a.getFecha())
                        .horaEntrada(a.getHoraEntrada())
                        .horaSalida(a.getHoraSalida())
                        .horasTrabajadas(a.getHorasTrabajadas())
                        .tipoJornada(a.getTipoJornada())
                        .build());

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Asistencia");

            // Header
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "No. Empleado", "Nombre", "Fecha", "Entrada", "Salida", "Horas", "Tipo"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(createHeaderStyle(workbook));
            }

            // Data
            int rowNum = 1;
            for (AttendanceResponse attendance : attendancePage) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(attendance.getId());
                row.createCell(1).setCellValue(attendance.getNumeroEmpleado());
                row.createCell(2).setCellValue(attendance.getNombreEmpleado());
                row.createCell(3).setCellValue(attendance.getFecha() != null ?
                        attendance.getFecha().format(DateTimeFormatter.ISO_LOCAL_DATE) : "");
                row.createCell(4).setCellValue(attendance.getHoraEntrada() != null ?
                        attendance.getHoraEntrada().toString() : "");
                row.createCell(5).setCellValue(attendance.getHoraSalida() != null ?
                        attendance.getHoraSalida().toString() : "");
                row.createCell(6).setCellValue(attendance.getHorasTrabajadas() != null ?
                        attendance.getHorasTrabajadas().doubleValue() : 0);
                row.createCell(7).setCellValue(attendance.getTipoJornada());
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generando Excel: " + e.getMessage());
        }
    }

    @Override
    public byte[] exportEmployeesReportPdf() {
        String message = "Reporte PDF de Empleados";
        return message.getBytes();
    }

    @Override
    public byte[] exportEmployeesReportExcel() {
        Page<EmployeeResponse> employeesPage = employeeRepository
                .findByActivoTrue(Pageable.unpaged())
                .map(e -> EmployeeResponse.builder()
                        .id(e.getId())
                        .numeroEmpleado(e.getNumeroEmpleado())
                        .nombreCompleto(e.getNombreCompleto())
                        .departamento(e.getDepartamento())
                        .puesto(e.getPuesto())
                        .fechaIngreso(e.getFechaIngreso())
                        .emailPersonal(e.getEmailPersonal())
                        .telefono(e.getTelefono())
                        .build());

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Empleados");

            // Header
            Row headerRow = sheet.createRow(0);
            String[] headers = {"No. Empleado", "Nombre", "Departamento", "Puesto", "Fecha Ingreso", "Email", "Telefono"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(createHeaderStyle(workbook));
            }

            // Data
            int rowNum = 1;
            for (EmployeeResponse employee : employeesPage) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(employee.getNumeroEmpleado());
                row.createCell(1).setCellValue(employee.getNombreCompleto());
                row.createCell(2).setCellValue(employee.getDepartamento() != null ? employee.getDepartamento() : "");
                row.createCell(3).setCellValue(employee.getPuesto() != null ? employee.getPuesto() : "");
                row.createCell(4).setCellValue(employee.getFechaIngreso() != null ?
                        employee.getFechaIngreso().format(DateTimeFormatter.ISO_LOCAL_DATE) : "");
                row.createCell(5).setCellValue(employee.getEmailPersonal() != null ? employee.getEmailPersonal() : "");
                row.createCell(6).setCellValue(employee.getTelefono() != null ? employee.getTelefono() : "");
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generando Excel: " + e.getMessage());
        }
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }
}
