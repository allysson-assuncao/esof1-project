import {MenuPerformanceReportDataTable} from "@/components/report/data-table/MenuPerformanceReportDataTable";

import React, {useState} from "react";
import {CategorySales, MenuPerformanceFilter, MenuPerformanceMetrics} from "@/model/Interfaces";
import {useQuery} from "react-query";
import {fetchMenuPerformanceMetrics, fetchMenuPerformanceReport} from "@/services/reportService";
import {DataTableSkeleton} from "@/components/skeleton/DataTableSkeleton";

const MenuPerformanceReportTable = () => {
    const [selectedFilters, setSelectedFilters] = useState<MenuPerformanceFilter>({
        businessDayStartTime: '18:00',
        minPrice: 0,
        maxPrice: 9999,
    });

    const {data: reportData, error, isLoading: isReportLoading} = useQuery<CategorySales[]>(
        ['menuPerformanceReport', selectedFilters],
        () => fetchMenuPerformanceReport(selectedFilters),
        {keepPreviousData: true}
    );

    const {data: metricsData, isLoading: isMetricsLoading} = useQuery<MenuPerformanceMetrics>(
        ['menuPerformanceMetrics', selectedFilters],
        () => fetchMenuPerformanceMetrics(selectedFilters),
        {keepPreviousData: true}
    );

    if (isReportLoading && !reportData) return <DataTableSkeleton/>;
    if (error) return <div>Ocorreu um erro ao carregar o relatório.</div>;

    return (
        <div className="container mx-auto py-10 w-full max-w-[1920px] 5xl:mx-auto 5xl:px-32">
            <div className="flex flex-col md:flex-row justify-center gap-3 md:gap-8 items-start md:items-center">
                <h1 className="text-2xl font-bold">Relatório de Performance do Cardápio</h1>
            </div>
            <MenuPerformanceReportDataTable
                data={reportData || []}
                selectedFilters={selectedFilters}
                setSelectedFilters={setSelectedFilters}
                metrics={metricsData}
                isMetricsLoading={isMetricsLoading}
            />
        </div>
    );
}

export default MenuPerformanceReportTable
