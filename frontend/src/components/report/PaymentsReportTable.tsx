import {DataTableSkeleton} from "@/components/skeleton/DataTableSkeleton";
import {fetchFilteredPayments} from "@/services/paymentService";
import {useQuery} from "react-query";
import {useState} from "react";
import {PaymentFilters} from "@/model/Interfaces";
import {PaymentsDataTable} from "@/components/report/data-table/PaymentsDataTable";
import {paymentGroupColumns} from "@/components/report/columns/PaymentColumns";

const PaymentsReportTable = () => {
    const [selectedFilters, setSelectedFilters] = useState<PaymentFilters>({
        startDate: undefined,
        endDate: undefined,
        businessDayStartTime: '18:00',
        businessDayEndTime: '02:00',
        paymentMethodIds: [],
    });
    const [page, setPage] = useState(0);
    const [pageSize, setPageSize] = useState(10);
    const [totalPages, setTotalPages] = useState(0);

    const {data, error, isLoading} = useQuery(
        ['paymentGroups', selectedFilters, page, pageSize], // Query key
        async () => {
            const res = await fetchFilteredPayments({
                filter: selectedFilters,
                page: page,
                size: pageSize,
            });
            setTotalPages(res.totalPages);
            return res.content;
        },
        {keepPreviousData: true}
    );

    if (isLoading) return <DataTableSkeleton/>;
    if (error) return <div>Erro ao carregar os pagamentos.</div>;

    return (
        <div className="container mx-auto py-10">
            <h1 className="text-2xl font-bold">Relatório de Pagamentos</h1>
            <PaymentsDataTable
                columns={paymentGroupColumns()}
                data={data || []}
                setPage={setPage}
                selectedFilters={selectedFilters}
                setSelectedFilters={setSelectedFilters}
                page={page}
                totalPages={totalPages}
                setPageSize={setPageSize}
            />
        </div>
    );
};

export default PaymentsReportTable;
