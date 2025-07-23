import React, {SetStateAction} from "react";
import {
    useReactTable,
    getCoreRowModel,
    flexRender,
} from "@tanstack/react-table";
import {Table, TableHeader, TableBody, TableRow, TableCell, TableHead} from "@/components/ui/table";
import {
    CategorySales,
    MenuPerformanceFilter,
    MenuPerformanceMetrics
} from "@/model/Interfaces";
import {Skeleton} from "@/components/ui/skeleton";
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card";
import {getExpandedRowModel} from "@tanstack/table-core";
import {useQuery} from "react-query";
import {fetchSimpleCategories} from "@/services/categoryService";
import {fetchSimpleProducts} from "@/services/productService";
import {DatePicker} from "@/components/ui/date-picker";
import {Input} from "@/components/ui/input";
import {MultiSelect} from "@/components/ui/multi-select";
import {Slider} from "@/components/ui/slider";
import {menuPerformanceReportColumns} from "@/components/report/columns/MenuPerformanceReportColumns";

const MetricsDisplay = ({metrics, isLoading}: { metrics?: MenuPerformanceMetrics; isLoading: boolean }) => {
    if (isLoading) {
        return (
            <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">{[...Array(4)].map((_, i) =>
                <Skeleton key={i} className="h-28"/>
            )}
            </div>
        );
    }
    return (
        <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
            <Card>
                <CardHeader>
                    <CardTitle>Itens Vendidos</CardTitle>
                </CardHeader>
                <CardContent><p className="text-2xl font-bold">{metrics?.totalItemsSold ?? '...'}</p>
                </CardContent>
            </Card>
            <Card>
                <CardHeader>
                    <CardTitle>Produto Top 1</CardTitle>
                </CardHeader>
                <CardContent><p className="text-2xl font-bold">{metrics?.topSellingProduct?.name ?? 'N/A'}</p>
                </CardContent>
            </Card>
            <Card>
                <CardHeader>
                    <CardTitle>Categoria Top 1</CardTitle>
                </CardHeader>
                <CardContent><p className="text-2xl font-bold">{metrics?.topSellingCategory?.name ?? 'N/A'}</p>
                </CardContent>
            </Card>
            <Card>
                <CardHeader>
                    <CardTitle>Itens/Pedido</CardTitle>
                </CardHeader>
                <CardContent><p
                    className="text-2xl font-bold">{metrics?.averageItemsPerOrder?.toFixed(1) ?? '...'}</p>
                </CardContent>
            </Card>
        </div>
    );
};

interface DataTableProps {
    data: CategorySales[];
    selectedFilters: MenuPerformanceFilter;
    setSelectedFilters: (filters: SetStateAction<MenuPerformanceFilter>) => void;
    metrics?: MenuPerformanceMetrics;
    isMetricsLoading: boolean;
}

export function MenuPerformanceReportDataTable({
                                                   data,
                                                   selectedFilters,
                                                   setSelectedFilters,
                                                   metrics,
                                                   isMetricsLoading
                                               }: DataTableProps) {
    const {data: simpleCategories, isLoading: isLoadingCategoryOptions} = useQuery(
        'simpleCategories', fetchSimpleCategories
    );
    const {data: productOptions, isLoading: isLoadingProductOptions} = useQuery(
        'simpleProducts', fetchSimpleProducts
    );

    const categoryOptions =
        simpleCategories?.map((category) => ({
            value: category.id,
            label: category.name,
        })) ?? [];

    const table = useReactTable({
        data,
        columns: menuPerformanceReportColumns,
        state: {
            expanded: true,
        },
        getSubRows: (row) => row.subRows,
        getCoreRowModel: getCoreRowModel(),
        getExpandedRowModel: getExpandedRowModel(),
    });

    return (
        <div>
            {/* Filters */}
            <div
                className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 5xl:grid-cols-4 gap-4 md:gap-6 py-4">
                {/*<MetricsDisplay metrics={metrics} isLoading={isMetricsLoading}/>*/}
                <DatePicker
                    onDateSelected={(startDate) => setSelectedFilters({...selectedFilters, startDate})}
                />
                <DatePicker
                    onDateSelected={(endDate) => setSelectedFilters({...selectedFilters, endDate})}
                />
                <MultiSelect
                    options={categoryOptions || []}
                    onValueChange={(selectedValues) =>
                        setSelectedFilters({
                            ...selectedFilters,
                            categoryIds: selectedValues.map((id) => id),
                        })
                    }
                    defaultValue={
                        selectedFilters.categoryIds ?? []
                            ? selectedFilters.categoryIds?.map(String)
                            : []
                    }
                    placeholder="Categorias"
                    disabled={isLoadingCategoryOptions}
                    animation={2}
                    maxCount={2}
                />
                <MultiSelect
                    options={productOptions || []}
                    onValueChange={(selectedValues) =>
                        setSelectedFilters({
                            ...selectedFilters,
                            productIds: selectedValues.map((id) => id),
                        })
                    }
                    defaultValue={
                        selectedFilters.productIds ?? []
                            ? selectedFilters.productIds?.map(String)
                            : []
                    }
                    placeholder="Produtos"
                    disabled={isLoadingProductOptions}
                    animation={2}
                    maxCount={2}
                />
                <div className="col-span-1 md:col-span-2 lg:col-span-2 flex flex-col gap-2">
                    <label>Faixa de Preço (R$)</label>
                    <div className="flex items-center gap-4">
                        <Input type="number" value={selectedFilters.minPrice}
                               onChange={e => setSelectedFilters(f => ({...f, minPrice: +e.target.value}))}
                               className="w-24"
                        />
                        <Slider
                            defaultValue={[0, 9999]}
                            max={9999}
                            step={1}
                            onValueCommit={([min, max]) => setSelectedFilters(f => ({
                                ...f,
                                minPrice: min,
                                maxPrice: max
                            }))}
                        />
                        <Input type="number" value={selectedFilters.maxPrice}
                               onChange={e => setSelectedFilters(f => ({...f, maxPrice: +e.target.value}))}
                               className="w-24"
                        />
                    </div>
                </div>
            </div>

            {/* Table */}
            <div className="rounded-md border overflow-x-auto">
                <Table>
                    <TableHeader>
                        {table.getHeaderGroups().map(hg => (
                            <TableRow key={hg.id}>
                                {hg.headers.map(h => (
                                    <TableHead key={h.id}>
                                        {flexRender(h.column.columnDef.header, h.getContext())}
                                    </TableHead>
                                ))}
                            </TableRow>
                        ))}
                    </TableHeader>
                    <TableBody>
                        {table.getRowModel().rows.length ? (
                            table.getRowModel().rows.map(row => (
                                <TableRow key={row.id}>
                                    {row.getVisibleCells().map(cell => (
                                        <TableCell key={cell.id}>
                                            {flexRender(cell.column.columnDef.cell, cell.getContext())}
                                        </TableCell>
                                    ))}
                                </TableRow>
                            ))
                        ) : (
                            <TableRow>
                                <TableCell colSpan={table.getAllColumns().length} className="h-24 text-center">
                                    Nenhum resultado.
                                </TableCell>
                            </TableRow>
                        )}
                    </TableBody>
                </Table>
            </div>
        </div>
    );
}
