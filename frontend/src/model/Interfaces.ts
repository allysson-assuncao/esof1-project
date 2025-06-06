import {undefined} from "zod";

export type UserRole = keyof typeof UserRoles;

export type GuestTabStatus = keyof typeof GuestTabStatus;

export type LocalTableStatus = keyof typeof LocalTableStatus;

export type OrderStatus = keyof typeof OrderStatus;

export const UserRoles = {
    ADMIN: {value: 'ADMIN', label: 'Administrador'},
    CASHIER: {value: 'CASHIER', label: 'Caixa'},
    COOK: {value: 'COOK', label: 'Cozinheiroz'},
    WAITER: {value: 'WAITER', label: 'Garçom'},
} as const

export const GuestTabStatus = {
    OPEN: {value: 'OPEN', label: 'Aberta'},
    CLOSED: {value: 'CLOSED', label: 'Fechada'},
    CANCELED: {value: 'CANCELED', label: 'Cancelada'},
} as const

export const LocalTableStatus = {
    FREE: {value: 'FREE', label: 'Livre'},
    OCCUPIED: {value: 'OCCUPIED', label: 'Ocupada'},
    RESERVED: {value: 'RESERVED', label: 'Reservada'},
} as const

export const OrderStatus = {
    IN_PREPARE: {value: 'IN_PREPARE', label: 'Em Preparo'},
    READY: {value: 'READY', label: 'Pronta'},
    CANCELED: {value: 'CANCELED', label: 'Cancelada'},
} as const

export interface OrderFilters {
    tableId?: string;
    guestTabIds?: number[];
    orderIds?: number[];
    orderStatuses?: string[];
    guestTabStatuses?: string[];
    minPrice?: number;
    maxPrice?: number;
    startTime?: Date | undefined;
    endTime?: Date | undefined;
    waiterIds?: string[];
    productName?: string;
}

export interface FetchOrdersParams {
    filter: OrderFilters;
    page?: number;
    size?: number;
    orderBy?: string;
    direction?: 'ASC' | 'DESC';
}

export interface DisplayOrderItem {
    orderId: number;
    amount: number;
    orderStatus: OrderStatus;
    observation: string;
    orderedTime: string;
    productUnitPrice: number;
    productName: string;
    waiterName: string;
}

export interface OrderDTO {
    guestTabId: number;
    guestTabStatus: GuestTabStatus;
    guestTabTimeOpened: Date | undefined;
    totalPrice: number;
    additionalOrders: number[];
    orderId: number;
    amount: number;
    orderStatus: OrderStatus;
    observation: string;
    orderedTime: string;
    productName: string;
    productUnitPrice: number;
    waiterName: string;
    localTableNumber: number;
}

export interface DisplayGuestTabItem {
    guestTabId: number;
    guestTabStatus: GuestTabStatus | null;
    guestTabTimeOpened: Date | null;
    totalPrice: number;
    waiterName?: string;
    orders: DisplayOrderItem[];
}

export function groupOrdersByGuestTab(orders: OrderDTO[]): DisplayGuestTabItem[] {
    const guestTabMap = new Map<number, DisplayGuestTabItem>();

    orders.forEach((order) => {
        const guestTabId = (order as OrderDTO).guestTabId;
        if (!guestTabId) return;

        if (!guestTabMap.has(guestTabId)) {
            guestTabMap.set(guestTabId, {
                guestTabId: guestTabId,
                guestTabStatus: null,
                guestTabTimeOpened: null,
                waiterName: "",
                totalPrice: 0,
                orders: []
            });
        }

        const guestTab = guestTabMap.get(guestTabId)!;

        guestTab.orders.push(order as DisplayOrderItem);
        guestTab.totalPrice += order.totalPrice;
    });

    return Array.from(guestTabMap.values());
}
