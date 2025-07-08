export type UserRole = keyof typeof UserRoles;

export type GuestTabStatus = keyof typeof GuestTabStatus;

export type LocalTableStatus = keyof typeof LocalTableStatus;

export type OrderStatus = keyof typeof OrderStatus;

export type OrderKanbanStatus = keyof typeof OrderKanbanStatus;

export const UserRoles = {
    ADMIN: {value: 'ADMIN', label: 'Administrador'},
    CASHIER: {value: 'CASHIER', label: 'Caixa'},
    WAITER: {value: 'WAITER', label: 'Garçom'},
    COOK: {value: 'COOK', label: 'Cozinheiro'},
    BARMAN: {value: 'BARMAN', label: 'Barista'},
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
    SENT: {value: 'SENT', label: 'Enviado'},
    IN_PREPARE: {value: 'IN_PREPARE', label: 'Em Preparo'},
    READY: {value: 'READY', label: 'Pronta'},
    DELIVERED: {value: 'DELIVERED', label: 'Entregue'},
    CANCELED: {value: 'CANCELED', label: 'Cancelada'},
} as const

export const OrderKanbanStatus = {
    SENT: {value: 'SENT', label: 'Enviado'},
    IN_PREPARE: {value: 'IN_PREPARE', label: 'Em Preparo'},
    READY: {value: 'READY', label: 'Pronta'},
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
    additionalOrders: DisplayOrderItem[];
    productName: string;
    productUnitPrice: number;
    waiterName: string;
}

export interface DisplayOrderGroup {
    representativeTime: string;
    groupTotalPrice: number;
    numberOfItems: number;
    orders: DisplayOrderItem[];
}

export interface DisplayGuestTabItem {
    id: number;
    status: GuestTabStatus | null;
    guestName: string;
    timeOpened: string;
    timeClosed: string;
    orderGroups: DisplayOrderGroup[];
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

export interface SimpleCategory {
    id: string;
    name: string;
}

export interface SimpleProduct {
    id: string;
    name: string;
}

export interface SimpleWorkstation {
    id: string;
    name: string;
}

export interface HierarchicalCategoryDTO {
    id: string;
    name: string;
    subCategories: HierarchicalCategoryDTO[];
}

export interface ProductDTO {
    id: string;
    name: string;
    description: string;
    price: number;
    idCategory: string;
}

export interface OrderKanban {
    id: number;
    productName: string;
    amount: number;
    observation?: string;
    orderedTime: string;
    status: OrderStatus;
    additionalOrders: OrderKanban[];
    workstationName: string;
}

export interface KanbanOrderResultsFilter {
    workstationIds: string[],
}

export interface FetchKanbanOrderResultsParams {
    filter: KanbanOrderResultsFilter;
    page?: number;
    size?: number;
    orderBy?: string;
    direction?: 'ASC' | 'DESC';
}

export interface FilteredPage<T> {
    content: T[];
    totalPages: number;
}

export interface KanbanOrders {
    sentOrders: FilteredPage<OrderKanban>;
    inPrepareOrders: FilteredPage<OrderKanban>;
    readyOrders: FilteredPage<OrderKanban>;
}
