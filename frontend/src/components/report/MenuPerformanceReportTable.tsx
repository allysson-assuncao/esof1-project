"use client";

import React, { useMemo, useState } from "react";
import { useQuery } from "react-query";
import {
    CategorySales,
    MenuPerformanceFilter,
    MenuPerformanceMetrics,
    ReportRow,
} from "@/model/Interfaces";
import { fetchMenuPerformanceReport, fetchMenuPerformanceMetrics } from "@/services/reportService";
import { DataTableSkeleton } from "@/components/skeleton/DataTableSkeleton";
import { MenuPerformanceReportDataTable } from "@/components/report/data-table/MenuPerformanceReportDataTable";
import { ExpandedState } from "@tanstack/react-table";

const transformDataToReportRows = (data: CategorySales[]): ReportRow[] => {
    return data.map((category) => {
        const productSubRows: ReportRow[] = category.productSales.map((product) => ({
            id: product.productId,
            name: product.name,
            quantitySold: product.quantitySold,
            totalValue: product.totalValue,
            unitPrice: product.unitPrice,
            active: product.active,
            type: 'PRODUCT',
        }));

        const categorySubRows: ReportRow[] = transformDataToReportRows(category.subCategorySales);

        return {
            id: category.categoryId,
            name: category.name,
            quantitySold: category.quantitySold,
            totalValue: category.totalValue,
            type: 'CATEGORY',
            subRows: [...categorySubRows, ...productSubRows],
        };
    });
};

const MenuPerformanceReportTable = () => {
    const [selectedFilters, setSelectedFilters] = useState<MenuPerformanceFilter>({
    });

    const [expanded, setExpanded] = useState<ExpandedState>({});

    const { data: reportData, error, isLoading: isReportLoading } = useQuery<CategorySales[]>(
        ['menuPerformanceReport', selectedFilters],
        () => fetchMenuPerformanceReport(selectedFilters),
        { keepPreviousData: true }
    );

    const { data: metricsData, isLoading: isMetricsLoading } = useQuery<MenuPerformanceMetrics>(
        ['menuPerformanceMetrics', selectedFilters],
        () => fetchMenuPerformanceMetrics(selectedFilters),
        { keepPreviousData: true }
    );

    const tableData = useMemo(() => {
        return reportData ? transformDataToReportRows(reportData) : [];
    }, [reportData]);

    if (isReportLoading && !reportData) return <DataTableSkeleton />;
    if (error) return <div>Ocorreu um erro ao carregar o relatório.</div>;

    return (
        <div className="container mx-auto py-10">
            <h1 className="text-2xl font-bold mb-4">Relatório de Performance do Cardápio</h1>

            <MenuPerformanceReportDataTable
                data={tableData}
                expanded={expanded}
                setExpanded={setExpanded}
                selectedFilters={selectedFilters}
                setSelectedFilters={setSelectedFilters}
                metrics={metricsData}
                isMetricsLoading={isMetricsLoading}
            />
        </div>
    );
};

export default MenuPerformanceReportTable;