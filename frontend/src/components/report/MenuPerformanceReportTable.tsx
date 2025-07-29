"use client";

import React, {useMemo, useState} from "react";
import {useQuery} from "react-query";
import {
    CategorySales,
    MenuPerformanceFilter,
    MenuPerformanceMetrics,
    ReportRow,
} from "@/model/Interfaces";
import {fetchMenuPerformanceReport, fetchMenuPerformanceMetrics} from "@/services/reportService";
import {DataTableSkeleton} from "@/components/skeleton/DataTableSkeleton";
import {MenuPerformanceReportDataTable} from "@/components/report/data-table/MenuPerformanceReportDataTable";
import {ExpandedState} from "@tanstack/react-table";
import {Loader2} from "lucide-react";

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
    const [selectedFilters, setSelectedFilters] = useState<MenuPerformanceFilter>({});

    const [expanded, setExpanded] = useState<ExpandedState>({});

    const hasValidPeriod = !!selectedFilters.startDate && !!selectedFilters.endDate;

    const {
        data: reportData,
        error,
        isLoading: isReportLoading,
        isFetching: isReportFetching
    } = useQuery<CategorySales[]>(
        ['menuPerformanceReport', selectedFilters],
        () => fetchMenuPerformanceReport(selectedFilters),
        {
            keepPreviousData: true,
            enabled: hasValidPeriod
        }
    );

    const {
        data: metricsData,
        isLoading: isMetricsLoading,
        isFetching: isMetricsFetching
    } = useQuery<MenuPerformanceMetrics>(
        ['menuPerformanceMetrics', selectedFilters],
        () => fetchMenuPerformanceMetrics(selectedFilters),
        {
            keepPreviousData: true,
            enabled: hasValidPeriod
        }
    );

    const tableData = useMemo(() => {
        return reportData ? transformDataToReportRows(reportData) : [];
    }, [reportData]);

    if (isReportLoading && !reportData) return <DataTableSkeleton/>;
    if (error) return <div>Ocorreu um erro ao carregar o relatório.</div>;

    return (
        <div className="container mx-auto py-10">
            <div className="flex items-center gap-4 mb-4">
                <h1 className="text-2xl font-bold">Relatório de Performance do Cardápio</h1>
                {isReportFetching && <Loader2 className="h-6 w-6 animate-spin text-muted-foreground"/>}
            </div>

            <MenuPerformanceReportDataTable
                data={tableData}
                expanded={expanded}
                setExpanded={setExpanded}
                selectedFilters={selectedFilters}
                setSelectedFilters={setSelectedFilters}
                metrics={metricsData}
                isMetricsLoading={isMetricsLoading || isMetricsFetching}
                hasValidPeriod={hasValidPeriod}
            />
        </div>
    );
};

export default MenuPerformanceReportTable;
