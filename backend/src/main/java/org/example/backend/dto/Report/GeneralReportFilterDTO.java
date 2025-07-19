package org.example.backend.dto.Report;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

public record GeneralReportFilterDTO(@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate, @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate, @DateTimeFormat(pattern = "HH:mm") LocalTime businessDayStartTime, @DateTimeFormat(pattern = "HH:mm") LocalTime businessDayEndTime, Set<Long> paymentMethodIds) { }
