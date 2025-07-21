import {PerformanceReportDataTable} from "@/components/report/data-table/PerformanceReportDataTable";
import {performanceReportColumns} from "@/components/report/columns/PerformanceReportColumns";

import React, {useState} from "react";
import {PaymentFilters} from "@/model/Interfaces";
import {Input} from "@/components/ui/input";
import {DatePicker} from "@/components/ui/date-picker";
import {MultiSelect} from "@/components/ui/multi-select";

const categoryOptions = [ //placeholder
    {label: "Bebidas", value: "Bebidas"},
    {label: "Comidas", value: "Comidas"},
    {label: "Sobremesas", value: "Sobremesas"},
];
const productOptions = [ //placeholder
    {label: "Coca-Cola", value: "Coca-Cola"},
    {label: "Água", value: "Água"},
    {label: "Hambúrguer", value: "Hambúrguer"},
];

const PerformanceReportTable = () => {
    // Estados controlados para os campos monetários como string (máscara igual AddProductForm)
    const [minPriceInput, setMinPriceInput] = useState('0,00');
    const [maxPriceInput, setMaxPriceInput] = useState('0,00');
    const [filters, setFilters] = useState<PaymentFilters>({});

    // Função de formatação igual AddProductForm
    function formatCurrencyInput(v: string) {
        v = v.replace(/\D/g, '');
        if (v.length === 0) v = '000';
        while (v.length < 3) v = '0' + v;
        const reais = v.slice(0, -2);
        const cents = v.slice(-2);
        return `${parseInt(reais, 10).toString()},${cents}`;
    }

    // Atualiza o estado de filters sempre que o valor string muda
    React.useEffect(() => {
        const parseBRL = (str: string) => {
            if (!str) return undefined;
            const normalized = str.replace(/\./g, '').replace(',', '.');
            const num = parseFloat(normalized);
            return isNaN(num) ? undefined : Math.max(0, num);
        };
        setFilters(f => ({
            ...f,
            minPrice: parseBRL(minPriceInput),
            maxPrice: parseBRL(maxPriceInput),
        }));
    }, [minPriceInput, maxPriceInput]);

    return (
        <div className="container mx-auto py-10 w-full max-w-[1920px] 5xl:mx-auto 5xl:px-32">
            <div className="flex flex-col md:flex-row justify-center gap-3 md:gap-8 items-start md:items-center">
                <h1 className="text-2xl font-bold">Relatório de Vendas</h1>
            </div>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4 my-6">
                <div className="flex items-center gap-2">
                    <span className="text-muted-foreground font-medium">R$</span>
                    <Input
                        inputMode="decimal"
                        pattern="^\d*[\,\.]?\d{0,2}$"
                        min="0"
                        placeholder="Preço mínimo"
                        value={minPriceInput}
                        onChange={e => setMinPriceInput(formatCurrencyInput(e.target.value))}
                    />
                </div>
                <div className="flex items-center gap-2">
                    <span className="text-muted-foreground font-medium">R$</span>
                    <Input
                        inputMode="decimal"
                        pattern="^\d*[\,\.]?\d{0,2}$"
                        min="0"
                        placeholder="Preço máximo"
                        value={maxPriceInput}
                        onChange={e => setMaxPriceInput(formatCurrencyInput(e.target.value))}
                    />
                </div>
                <DatePicker
                    onDateSelected={date => setFilters(f => ({...f, startDate: date}))}
                />
                <DatePicker
                    onDateSelected={date => setFilters(f => ({...f, endDate: date}))}
                />
                {/*<MultiSelect
                    options={categoryOptions}
                    onValueChange={values => setFilters(f => ({...f, categoryNames: values}))}
                    defaultValue={filters.categoryNames || []}
                    placeholder="Categorias"
                />
                <MultiSelect
                    options={productOptions}
                    onValueChange={values => setFilters(f => ({...f, productNames: values}))}
                    defaultValue={filters.productNames || []}
                    placeholder="Produtos"
                />*/}
            </div>
            <PerformanceReportDataTable
                columns={performanceReportColumns}
                data={[]}
            />
        </div>
    )
}

export default PerformanceReportTable
