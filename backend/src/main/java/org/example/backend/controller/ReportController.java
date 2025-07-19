package org.example.backend.controller;

import org.example.backend.dto.FilteredPageDTO;
import org.example.backend.dto.Payment.ReportPaymentDTO;
import org.example.backend.dto.Report.GeneralReportFilterDTO;
import org.example.backend.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/app/report")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping("/filter-payments")
    public ResponseEntity<FilteredPageDTO<ReportPaymentDTO>> filterPaymentsReport(
            @RequestBody GeneralReportFilterDTO filterDto,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String orderBy,
            @RequestParam(defaultValue = "ASC") Sort.Direction direction
    ) {
        System.out.println(filterDto.toString());
        Page<ReportPaymentDTO> paymentsPage = this.reportService.getPaymentsByFilters(filterDto, page, size, orderBy, direction);
        return ResponseEntity.ok(new FilteredPageDTO<>(paymentsPage.getContent(), paymentsPage.getTotalPages()));
    }

}
