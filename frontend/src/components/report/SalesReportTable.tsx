import {DataTableSkeleton} from "@/components/skeleton/DataTableSkeleton";
import {useQuery} from "react-query";
import {useState} from "react";
import {GuestTabFilters, PaymentFilters, PaymentMetrics} from "@/model/Interfaces";
import {SalesDataTable} from "@/components/report/data-table/SalesDataTable";
import {paymentGroupColumns} from "@/components/report/columns/SalesColumns";
import {fetchFilteredPayments, fetchPaymentMetrics} from "@/services/reportService";

const SalesReportTable = () => {
    const [selectedFilters, setSelectedFilters] = useState<PaymentFilters>({
        startDate: undefined,
        endDate: undefined,
        businessDayStartTime: '18:00',
        businessDayEndTime: '02:00',
        paymentMethodIds: [],
    });
    const [page, setPage] = useState(0);
    const [pageSize, setPageSize] = useState(5);
    const [totalPages, setTotalPages] = useState(0);
    const [cachedPages, setCachedPages] = useState<{ [key: number]: GuestTabFilters[] }>({});

    const {data: tableData, error: tableError, isLoading: isTableLoading} = useQuery(
        ['paymentGroups', selectedFilters, page, pageSize],
        async () => {
            const res = await fetchFilteredPayments({
                filter: selectedFilters,
                page: page,
                size: pageSize,
                orderBy: 'createdAt',
                direction: 'ASC',
            });
            setTotalPages(res.totalPages)
            setCachedPages((prev) => ({...prev, [page]: res.content}))
            return res.content
        },
        {
            keepPreviousData: true,
            initialData: cachedPages[page] || undefined,
        },
    );

    const {data: metricsData, isLoading: isMetricsLoading} = useQuery<PaymentMetrics>(
        ['paymentMetrics', selectedFilters],
        () => fetchPaymentMetrics(selectedFilters),
        {keepPreviousData: true}
    );

    if (isTableLoading) return <DataTableSkeleton/>;
    if (tableError) return <div>Erro ao carregar os pagamentos.</div>;

    return (
        <div className="container mx-auto py-10 w-full max-w-[1920px] 5xl:mx-auto 5xl:px-32">
            <div className="flex flex-col md:flex-row justify-center gap-3 md:gap-8 items-start md:items-center">
                <h1 className="text-2xl font-bold">Relatório de Pagamentos</h1>
            </div>
            <SalesDataTable
                columns={paymentGroupColumns()}
                data={tableData || []}
                setPage={setPage}
                selectedFilters={selectedFilters}
                setSelectedFilters={setSelectedFilters}
                page={page}
                totalPages={totalPages}
                setPageSize={setPageSize}
                metrics={metricsData}
                isMetricsLoading={isMetricsLoading}
            />
        </div>
    );
};

export default SalesReportTable;
