package org.example.backend.controller;

import org.example.backend.dto.Category.CategorySalesDTO;
import org.example.backend.dto.FilteredPageDTO;
import org.example.backend.dto.Payment.PaymentGroupDTO;
import org.example.backend.dto.Report.FinancialMetricsDTO;
import org.example.backend.dto.Report.GeneralReportFilterDTO;
import org.example.backend.dto.Report.MenuPerformanceMetricsDTO;
import org.example.backend.dto.Report.MenuReportFilterDTO;
import org.example.backend.service.MenuReportService;
import org.example.backend.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/app/report")
public class ReportController {

    private final ReportService reportService;
    private final MenuReportService menuReportService;

    @Autowired
    public ReportController(ReportService reportService, MenuReportService menuReportService) {
        this.reportService = reportService;
        this.menuReportService = menuReportService;
    }

    @PostMapping("/filter-payments")
    public ResponseEntity<FilteredPageDTO<PaymentGroupDTO>> filterPaymentsReport(
            @RequestBody GeneralReportFilterDTO filterDto,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        System.out.println(filterDto);
        Page<PaymentGroupDTO> paymentsPage = this.reportService.getGroupedPaymentsByFilters(filterDto, page, size, "date", Sort.Direction.DESC);
        System.out.println(paymentsPage);
        return ResponseEntity.ok(new FilteredPageDTO<>(paymentsPage.getContent(), paymentsPage.getTotalPages()));
    }

    @PostMapping("/filter-menu")
    public ResponseEntity<List<CategorySalesDTO>> getMenuSalesReport(@RequestBody MenuReportFilterDTO filter) {
        List<CategorySalesDTO> report = this.menuReportService.getMenuSalesReport(filter);
        return ResponseEntity.ok(report);
    }

    @PostMapping("/menu-metrics")
    public ResponseEntity<MenuPerformanceMetricsDTO> getMenuPerformanceMetrics(@RequestBody MenuReportFilterDTO filter) {
        MenuPerformanceMetricsDTO metrics = this.menuReportService.getMenuPerformanceMetrics(filter);
        return ResponseEntity.ok(metrics);
    }

    @PostMapping("/payment-metrics")
    public ResponseEntity<FinancialMetricsDTO> getPaymentMetrics(@RequestBody GeneralReportFilterDTO filter) {
        FinancialMetricsDTO metrics = this.reportService.getFinancialMetrics(filter);
        return ResponseEntity.ok(metrics);
    }

}
