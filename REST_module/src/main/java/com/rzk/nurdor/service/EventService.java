package com.rzk.nurdor.service;

import com.rzk.nurdor.model.City;
import com.rzk.nurdor.model.Event;
import com.rzk.nurdor.model.EventsLog;
import com.rzk.nurdor.model.EventsLogDto;
import com.rzk.nurdor.repository.CityRepository;
import com.rzk.nurdor.repository.EventRepository;
import com.rzk.nurdor.repository.EventsLogRepository;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanArrayDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.data.JRMapArrayDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.springframework.stereotype.Service;
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
public class EventService {

    private final EventRepository eventRepository;
    private final EventsLogRepository eventsLogRepository;
    private final CityRepository cityRepository;

    public EventService(EventRepository eventRepository, EventsLogRepository eventsLogRepository, CityRepository cityRepository) {
        this.eventRepository = eventRepository;
        this.eventsLogRepository = eventsLogRepository;
        this.cityRepository = cityRepository;
    }

    public List<Event> getEvents() {
        return eventRepository.findAll();
    }

    public List<EventsLog> getEventsLogs() {
        return eventsLogRepository.findAll();
    }

    public Event getEventById(int id) {
        return eventRepository.findById(id).orElseThrow();
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

    public Event insertEvent(Event event) {
        return eventRepository.save(event);
    }

    public City findCityByZipCode(String zipCode) {
        return cityRepository.findById(zipCode).orElseThrow();
    }

    private String getTimeString(LocalDateTime startTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy'T'HH:mm");
        return startTime.format(formatter).split("T")[1];
    }

    private String getDateString(LocalDateTime startTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy'T'HH:mm");
        return startTime.format(formatter).split("T")[0];
    }

    public EventsLog insertLog(EventsLog eventsLog) {
        return eventsLogRepository.save(eventsLog);
    }

    @Transactional
    public EventsLog markAsPresent(EventsLogDto log) {
        EventsLog eventsLog = eventsLogRepository
                .findInitLogByVolunteerAndEvent(log.getVolunteer(), log.getEvent())
                .orElseThrow();
        eventsLog.setIsPresent(log.getIsPresent());
        return eventsLogRepository.save(eventsLog);
    }

    public List<EventsLog> insertInitLogs(List<EventsLog> initLogs) {
        return eventsLogRepository.saveAll(initLogs);
    }
}
