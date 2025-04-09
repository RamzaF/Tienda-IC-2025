package com.tienda.service.impl;

import com.tienda.service.ReporteService;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Map;
import javax.sql.DataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ReporteServiceImpl implements ReporteService {

    @Autowired
    DataSource datasource;
    
    public ResponseEntity<Resource> generaReporte(
        String reporte,
        Map<String, Object> parametros,
        String tipo) throws IOException {
    try {
        String estilo;
        if ("vPdf".equals(tipo)) {
            estilo = "inline; ";
        } else {
            estilo = "attachment; ";
        }

        String reportePath = "reportes";
        ByteArrayOutputStream salida = new ByteArrayOutputStream();

        ClassPathResource fuente = new ClassPathResource(
                reportePath + File.separator + reporte + ".jasper");

        InputStream elReporte = fuente.getInputStream();

        var reporteJasper = JasperFillManager.fillReport(
                elReporte, parametros, datasource.getConnection());

        MediaType mediaType = null;
        String archivoSalida = "";
        byte[] data;

        if (null != tipo) {
            switch (tipo) {
                case "Pdf", "vPdf" -> {
                    JasperExportManager.exportReportToPdfStream(reporteJasper, salida);
                    mediaType = MediaType.APPLICATION_PDF;
                    archivoSalida = reporte + ".pdf";
                }
                case "Xls" -> {
                    JRXlsxExporter exportador = new JRXlsxExporter();
                    exportador.setExporterInput(new SimpleExporterInput(reporteJasper));
                    exportador.setExporterOutput(new SimpleOutputStreamExporterOutput(salida));
                    exportador.setConfiguration(new SimpleXlsxReportConfiguration());
                    exportador.exportReport();
                    mediaType = MediaType.APPLICATION_OCTET_STREAM;
                    archivoSalida = reporte + ".xlsx";
                }
                case "Csv" -> {
                    JRCsvExporter exportador = new JRCsvExporter();
                    exportador.setExporterInput(new SimpleExporterInput(reporteJasper));
                    exportador.setExporterOutput(new SimpleWriterExporterOutput(salida));
                    exportador.exportReport();
                    mediaType = MediaType.TEXT_PLAIN;
                    archivoSalida = reporte + ".csv";
                }
                default -> throw new IllegalArgumentException("Formato de reporte no soportado: " + tipo);
            }
        }

        data = salida.toByteArray();
        InputStreamResource archivo = new InputStreamResource(new ByteArrayInputStream(data));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, estilo + "filename=" + archivoSalida)
                .contentType(mediaType)
                .body(archivo);

    } catch (JRException | SQLException e) {
        throw new IOException("Error al generar el reporte: " + e.getMessage(), e);
    }
}

}
