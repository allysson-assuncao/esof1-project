"use client";

import React, {SetStateAction} from "react";
import {
    useReactTable,
    getCoreRowModel,
    flexRender,
    getExpandedRowModel,
    ExpandedState,
} from "@tanstack/react-table";
import {Table, TableHeader, TableBody, TableRow, TableCell, TableHead} from "@/components/ui/table";
import {
    MenuPerformanceFilter,
    MenuPerformanceMetrics,
    ReportRow,
    SimpleCategory,
    SimpleProduct
} from "@/model/Interfaces";
import {Skeleton} from "@/components/ui/skeleton";
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card";
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
            <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">{[...Array(4)].map((_, i) =>
                <Skeleton key={i} className="h-28"/>
            )}
            </div>
        );
    }
    return (
        <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
            <Card>
                <CardHeader className="pb-2">
                    <CardTitle className="text-sm font-medium">Receita Total</CardTitle>
                </CardHeader>
                <CardContent>
                    <p className="text-2xl font-bold">R$ {metrics?.totalRevenue?.toFixed(2) ?? '0.00'}</p>
                </CardContent>
            </Card>
            <Card>
                <CardHeader className="pb-2">
                    <CardTitle className="text-sm font-medium">Itens Vendidos</CardTitle>
                </CardHeader>
                <CardContent>
                    <p className="text-2xl font-bold">{metrics?.totalItemsSold ?? 0}</p>
                </CardContent>
            </Card>
            <Card>
                <CardHeader className="pb-2">
                    <CardTitle className="text-sm font-medium">Produtos Únicos</CardTitle>
                </CardHeader>
                <CardContent>
                    <p className="text-2xl font-bold">{metrics?.uniqueProductsSold ?? 0}</p>
                </CardContent>
            </Card>
        </div>
    );
};

interface DataTableProps {
    data: ReportRow[];
    selectedFilters: MenuPerformanceFilter;
    setSelectedFilters: (action: SetStateAction<MenuPerformanceFilter>) => void;
    metrics?: MenuPerformanceMetrics;
    isMetricsLoading: boolean;
    expanded: ExpandedState;
    setExpanded: React.Dispatch<SetStateAction<ExpandedState>>;
}

export function MenuPerformanceReportDataTable({
                                                   data,
                                                   expanded,
                                                   setExpanded,
                                                   selectedFilters,
                                                   setSelectedFilters,
                                                   metrics,
                                                   isMetricsLoading
                                               }: DataTableProps) {

    const table = useReactTable({
        data,
        columns: menuPerformanceReportColumns,
        state: {
            expanded,
        },
        onExpandedChange: setExpanded,
        getSubRows: (row) => row.subRows,
        getCoreRowModel: getCoreRowModel(),
        getExpandedRowModel: getExpandedRowModel(),
    });

    const {data: simpleCategories, isLoading: isLoadingCategoryOptions} = useQuery<SimpleCategory[]>(
        'simpleCategories', fetchSimpleCategories
    );
    const {data: simpleProducts, isLoading: isLoadingSimpleProducts} = useQuery<SimpleProduct[]>(
        'simpleProducts', fetchSimpleProducts
    );

    const categoryOptions =
        simpleCategories?.map((category) => ({
            value: category.id,
            label: category.name,
        })) ?? [];

    const productOptions =
        simpleProducts?.map((product) => ({
            value: product.id,
            label: product.name,
        })) ?? [];

    return (
        <div>
            {}
            <div className="mb-4">
                <MetricsDisplay metrics={metrics} isLoading={isMetricsLoading}/>
            </div>

            {}
            <div
                className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 5xl:grid-cols-4 gap-4 md:gap-6 py-4">
                <DatePicker
                    onDateSelected={(startDate) => setSelectedFilters({...selectedFilters, startDate})}
                />
                <DatePicker
                    onDateSelected={(endDate) => setSelectedFilters({...selectedFilters, endDate})}
                />
                <MultiSelect
                    options={categoryOptions}
                    onValueChange={(selectedValues) =>
                        setSelectedFilters({
                            ...selectedFilters,
                            categoryIds: selectedValues,
                        })
                    }
                    defaultValue={selectedFilters.categoryIds ?? []}
                    placeholder="Categorias"
                    disabled={isLoadingCategoryOptions}
                    animation={2}
                    maxCount={2}
                />
                <MultiSelect
                    options={productOptions}
                    onValueChange={(selectedValues) =>
                        setSelectedFilters({
                            ...selectedFilters,
                            productIds: selectedValues,
                        })
                    }
                    defaultValue={selectedFilters.productIds ?? []}
                    placeholder="Produtos"
                    disabled={isLoadingSimpleProducts}
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
                            defaultValue={[selectedFilters.minPrice ?? 0, selectedFilters.maxPrice ?? 9999]}
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

            {}
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
                                <TableRow key={row.id} data-state={row.getIsSelected() && "selected"}>
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