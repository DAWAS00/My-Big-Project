package com.webscraper.application.port.out;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * Port for data export operations.
 * Supports Excel and CSV formats.
 */
public interface ExportService {
    
    /**
     * Export data to Excel format.
     * @param data List of rows (each row is a map of column->value)
     * @param headers Column headers in order
     * @param output Stream to write to
     */
    void exportToExcel(List<Map<String, Object>> data, List<String> headers, OutputStream output);
    
    /**
     * Export data to CSV format.
     */
    void exportToCsv(List<Map<String, Object>> data, List<String> headers, OutputStream output);
    
    /**
     * Get supported formats.
     */
    default List<String> getSupportedFormats() {
        return List.of("excel", "csv");
    }
}
