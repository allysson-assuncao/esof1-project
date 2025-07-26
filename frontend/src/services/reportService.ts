import {CategorySales, FetchPaymentParams, MenuPerformanceFilter, PaymentFilters} from "@/model/Interfaces";
import {report} from "@/services/index";

export const fetchFilteredPayments = async (params: FetchPaymentParams) => {
    const response = await report.post(`/filter-payments`, params.filter, {
        headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`,
        },
        params: {
            page: params.page || 0,
            size: params.size || 5,
            orderBy: params.orderBy || 'updatedAt',
            direction: params.direction || 'ASC',
        },
    });
    console.log(params);
    console.log(response.data);
    return response.data;
}

export const fetchPaymentMetrics = async (filters: PaymentFilters) => {
    const response = await report.post('/payment-metrics', filters, {
        headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`,
        },
    });
    return response.data;
};

function transformDataForTable(nodes: CategorySales[]): CategorySales[] {
    return nodes.map(node => {
        const productSubRows = node.productSales.map(p => ({ ...p, subRows: undefined }));
        const categorySubRows = transformDataForTable(node.subCategorySales);

        return {
            ...node,
            subRows: [...categorySubRows, ...productSubRows],
        };
    });
}

export const fetchMenuPerformanceReport = async (filter: MenuPerformanceFilter) => {
    const response = await report.post(`/filter-menu`, filter, {
        headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`,
        },
    });
    console.log(filter);
    console.log(transformDataForTable(response.data));
    return transformDataForTable(response.data);
}

export const fetchMenuPerformanceMetrics = async (filters: MenuPerformanceFilter) => {
    const response = await report.post('/menu-metrics', filters, {
        headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`,
        },
    });
    return response.data;
};
