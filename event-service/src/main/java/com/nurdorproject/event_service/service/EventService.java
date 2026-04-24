package com.nurdorproject.event_service.service;

import com.nurdorproject.event_service.exception.EventNotFoundException;
import com.nurdorproject.event_service.model.Event;
import com.nurdorproject.event_service.proxy.EventsLogProxy;
import com.nurdorproject.event_service.repository.EventRepository;
import lombok.AllArgsConstructor;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
    private EventsLogProxy eventsLogProxy;

    public List<Event> findAll() {
        return eventRepository.findAll();
    }

    public Event findById(Integer idEvent) {
        return eventRepository.findById(idEvent).orElseThrow(() -> new EventNotFoundException("Event with id: " + idEvent + " is not found!"));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Event findByIdAndReleaseConn(Integer idEvent) {
        return eventRepository.findById(idEvent).orElseThrow(() -> new EventNotFoundException("Event with id: " + idEvent + " is not found!"));
    }

    public Event save(Event event) {
        return eventRepository.save(event);
    }

    @Transactional
    public String delete(Integer idEvent) {
        Event event = findById(idEvent);
        String message = eventsLogProxy.deleteLogsByIdEvent(idEvent);
        if(message.contains("SUCCESS")) {
            eventRepository.delete(event);
            return "SUCCESS: Event deleted, id: " + idEvent;
        } else {
            return "ERROR: Event with id: " + idEvent + " is not deleted properly (logs are still present!)";
        }
    }

    public byte[] createEventPdf(Event event, String lang) {
        byte[] eventImage = event.getEventImg();
        String jasperReportFileName;
        if(lang.equals("SRB")) {
            jasperReportFileName = "eventPdfSrb.jrxml";
        } else {
            jasperReportFileName = "eventPdfEng.jrxml";
        }
        try(InputStream jasperReportStream = this.getClass().getResourceAsStream("/jasperreports/" + jasperReportFileName);
            InputStream logoStream = this.getClass().getResourceAsStream("/static/images/640px-Nurdor-logo.jpg");
            InputStream qrStream = this.getClass().getResourceAsStream("/static/images/NBSIPSQR.png");
            InputStream eventImageStream = eventImage != null
                    ? new ByteArrayInputStream(eventImage)
                    : this.getClass().getResourceAsStream("/static/images/testEventImg.png");
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

            JasperReport jasperReport = JasperCompileManager.compileReport(jasperReportStream);

            SimpleJasperReportsContext jasperReportsContext = new SimpleJasperReportsContext();
//            jasperReportsContext.setProperty("net.sf.jasperreports.default.pdf.font.name", "DejaVu Sans");
//            jasperReportsContext.setProperty("net.sf.jasperreports.default.pdf.encoding", "Identity-H");
//            jasperReportsContext.setProperty("net.sf.jasperreports.default.pdf.embedded", "true");

            Map<String, Object> params = new HashMap<>();

            params.put("nurdorLogo", logoStream);
            params.put("qrCode", qrStream);
            params.put("eventImage", eventImageStream);
            params.put("eventName", event.getEventName());
            params.put("startDate", getDateString(event.getStartTime()).trim());
            params.put("startTime", getTimeString(event.getStartTime()).trim());
            params.put("locationDetails", event.getLocationDesc());
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, new JREmptyDataSource());

            JRPdfExporter exporter = new JRPdfExporter(jasperReportsContext);
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(byteArrayOutputStream));
            exporter.exportReport();
            return byteArrayOutputStream.toByteArray();
        } catch (JRException e) {
            System.out.println("JRException: " + e.getMessage());
        } catch (IOException | NullPointerException e) {
            System.out.println("Exception (JRE or NPE): " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
        return null;
    }

    public List<Event> findFinishedEvents() {
        return eventRepository.findFinishedEvents(LocalDateTime.now());
    }

    public List<Event> findByIsStarted(byte isStarted) {
        return eventRepository.findByIsStarted(isStarted);
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
