package com.webscraper.infrastructure.export;

import com.webscraper.application.port.out.ExportService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Implementation of ExportService using Apache POI for Excel
 * and standard Java for CSV.
 */
@Component
public class PoiExportService implements ExportService {

    @Override
    public void exportToExcel(List<Map<String, Object>> data, List<String> headers, OutputStream output) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Data");
            
            // Create header style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            
            // Create header row
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers.get(i));
                cell.setCellStyle(headerStyle);
            }
            
            // Create data rows
            int rowNum = 1;
            for (Map<String, Object> rowData : data) {
                Row row = sheet.createRow(rowNum++);
                for (int i = 0; i < headers.size(); i++) {
                    Cell cell = row.createCell(i);
                    Object value = rowData.get(headers.get(i));
                    setCellValue(cell, value);
                }
            }
            
            // Auto-size columns
            for (int i = 0; i < headers.size(); i++) {
                sheet.autoSizeColumn(i);
            }
            
            workbook.write(output);
        } catch (IOException e) {
            throw new RuntimeException("Failed to export to Excel", e);
        }
    }

    @Override
    public void exportToCsv(List<Map<String, Object>> data, List<String> headers, OutputStream output) {
        PrintWriter writer = new PrintWriter(output);
        
        // Write header
        writer.println(String.join(",", headers.stream()
                .map(this::escapeCsv)
                .toList()));
        
        // Write data rows
        for (Map<String, Object> rowData : data) {
            String row = headers.stream()
                    .map(h -> {
                        Object value = rowData.get(h);
                        return escapeCsv(value != null ? value.toString() : "");
                    })
                    .reduce((a, b) -> a + "," + b)
                    .orElse("");
            writer.println(row);
        }
        
        writer.flush();
    }

    private void setCellValue(Cell cell, Object value) {
        if (value == null) {
            cell.setCellValue("");
        } else if (value instanceof Number num) {
            cell.setCellValue(num.doubleValue());
        } else if (value instanceof Boolean bool) {
            cell.setCellValue(bool);
        } else if (value instanceof Instant instant) {
            cell.setCellValue(instant.toString());
        } else {
            cell.setCellValue(value.toString());
        }
    }

    private String escapeCsv(String value) {
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
