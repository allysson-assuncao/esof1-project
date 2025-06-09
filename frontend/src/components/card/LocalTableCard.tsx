import {LocalTable, LocalTableStatus} from "@/model/Interfaces";
import {Users} from "lucide-react";

interface TableCardProps {
    table: LocalTable;
    onSelect: (tableId: string) => void;
}

const statusStyles: Record<LocalTableStatus, string> = {
    FREE: "bg-green-100 border-green-400 text-green-800 hover:bg-green-200",
    OCCUPIED: "bg-red-100 border-red-400 text-red-800 hover:bg-red-200",
    RESERVED: "bg-gray-200 border-gray-400 text-gray-600 hover:bg-gray-300",
};

export function LocalTableCard({table, onSelect}: TableCardProps) {
    const styles = statusStyles[table.status] || statusStyles.RESERVED;

    return (
        <div
            onClick={() => onSelect(table.id)}
            className={`flex flex-col items-center justify-center p-4 border-2 rounded-lg 
                 shadow-md aspect-square cursor-pointer transition-colors duration-200 ${styles}`}
        >
            <span className="text-4xl font-bold">{String(table.number).padStart(2, '0')}</span>
            <span className="text-sm font-semibold mt-1">
        Mesa {table.number}
      </span>
            <div className="flex items-center gap-2 mt-3 text-xs">
                <Users size={16}/>
                <span>{table.guestTabCountToday} Comanda(s)</span>
            </div>
        </div>
    );
}