package com.nurdorproject.event_service.service;

import com.nurdorproject.event_service.exception.EventNotFoundException;
import com.nurdorproject.event_service.model.Event;
import com.nurdorproject.event_service.repository.EventRepository;
import lombok.AllArgsConstructor;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class EventService {

    private EventRepository eventRepository;

    public List<Event> findAll() {
        return eventRepository.findAll();
    }

    public Event findById(Integer idEvent) {
        return eventRepository.findById(idEvent).orElseThrow(() -> new EventNotFoundException("Event with id: " + idEvent + " is not found!"));
    }

    public byte[] createEventPdf(Event event) throws JRException, IOException {
        SimpleJasperReportsContext jasperReportsContext = new SimpleJasperReportsContext();
        jasperReportsContext.setProperty("net.sf.jasperreports.default.pdf.font.name", "DejaVu Sans");
        jasperReportsContext.setProperty("net.sf.jasperreports.default.pdf.encoding", "Identity-H");
        jasperReportsContext.setProperty("net.sf.jasperreports.default.pdf.embedded", "true");

        InputStream inputStream = this.getClass().getResourceAsStream("/jasperreports/eventPdfSrp.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
        Map<String, Object> params = new HashMap<>();
        InputStream logoStream = this.getClass().getResourceAsStream("/static/images/640px-Nurdor-logo.jpg");
        params.put("nurdorLogo", logoStream);
        InputStream qrStream = this.getClass().getResourceAsStream("/static/images/NBSIPSQR.png");
        params.put("qrCode", qrStream);
        InputStream eventImageStream = new ByteArrayInputStream(event.getEventImg());
        params.put("eventImage", eventImageStream);
        params.put("eventName", event.getEventName());
        params.put("description", event.getDescription());
        params.put("startDate", getDateString(event.getStartTime()).trim());
        params.put("startTime", getTimeString(event.getStartTime()).trim());
        params.put("locationDetails", event.getLocationDesc());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, new JREmptyDataSource());
        inputStream.close();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        JRPdfExporter exporter = new JRPdfExporter(jasperReportsContext);
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(byteArrayOutputStream));
        exporter.exportReport();
        return byteArrayOutputStream.toByteArray();
    }

    private String getTimeString(LocalDateTime startTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy'T'HH:mm");
        return startTime.format(formatter).split("T")[1];
    }

    private String getDateString(LocalDateTime startTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy'T'HH:mm");
        return startTime.format(formatter).split("T")[0];
    }
}
