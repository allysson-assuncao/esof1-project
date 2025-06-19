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

export interface GuestTabFilters {
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

export interface FetchGuestTabParams {
    filter: GuestTabFilters;
    page?: number;
    size?: number;
    orderBy?: string;
    direction?: 'ASC' | 'DESC';
}

export interface DisplayOrderItem {
    id: number;
    amount: number;
    status: OrderStatus;
    observation: string;
    orderedTime: number[];
    additionalOrders: number[];
    productName: string;
    productUnitPrice: number;
    waiterName: string;
}

export interface DisplayGuestTabItem {
    id: number;
    status: GuestTabStatus | null;
    timeOpened: Date | null;
    timeClosed: Date | null;
    orders: DisplayOrderItem[];
    totalPrice: number;
    waiterName?: string;
}

export interface LocalTable {
  id: string;
  number: number;
  status: LocalTableStatus;
  guestTabCountToday: number;
}

export interface SimpleGuestTab {
    id: number;
    clientName: string;
}

export interface SimpleOrder {
    id: number;
}

export interface SimpleWaiter {
    id: string;
    userName: string;
}
