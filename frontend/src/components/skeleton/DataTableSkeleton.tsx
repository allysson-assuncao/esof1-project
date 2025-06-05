import { Skeleton } from "@/components/ui/skeleton";
import {Table, TableBody, TableCell, TableHead, TableHeader, TableRow} from "@/components/ui/table";

export function DataTableSkeleton() {
    return (
        <div className="container mx-auto py-10">
            <div className="rounded-md border">
                <Table>
                    <TableHeader>
                        <TableRow>
                            <TableHead>Coluna 1</TableHead>
                            <TableHead>Coluna 2</TableHead>
                            <TableHead>Coluna 3</TableHead>
                            <TableHead>Coluna 4</TableHead>
                            <TableHead>Coluna 5</TableHead>
                            <TableHead>Coluna 6</TableHead>
                        </TableRow>
                    </TableHeader>
                    <TableBody>
                        {[...Array(8)].map((_, index) => (
                            <TableRow key={index}>
                                <TableCell><Skeleton className="h-4 w-[100px]"/></TableCell>
                                <TableCell><Skeleton className="h-4 w-[150px]"/></TableCell>
                                <TableCell><Skeleton className="h-4 w-[150px]"/></TableCell>
                                <TableCell><Skeleton className="h-4 w-[100px]"/></TableCell>
                                <TableCell><Skeleton className="h-4 w-[150px]"/></TableCell>
                                <TableCell><Skeleton className="h-4 w-[100px]"/></TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </div>
        </div>
    );
}
