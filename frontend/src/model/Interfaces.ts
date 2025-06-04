export type UserRole = keyof typeof UserRoles;

export type GuestTabStatus = keyof typeof GuestTabStatus;

export type LocalTableStatus = keyof typeof LocalTableStatus;

export type OrderStatus = keyof typeof OrderStatus;

export const UserRoles = {
    ADMIN: { value: 'ADMIN', label: 'Administrador' },
    CASHIER: { value: 'CASHIER', label: 'Caixa' },
    COOK: { value: 'COOK', label: 'Cozinheiroz' },
    WAITER: { value: 'WAITER', label: 'Garçom' },
} as const

export const GuestTabStatus = {
    OPEN: { value: 'OPEN', label: 'Aberta' },
    CLOSED: { value: 'CLOSED', label: 'Fechada' },
    CANCELED: { value: 'CANCELED', label: 'Cancelada' },
} as const

export const LocalTableStatus = {
    FREE: { value: 'FREE', label: 'Livre' },
    OCCUPIED: { value: 'OCCUPIED', label: 'Ocupada' },
    RESERVED: { value: 'RESERVED', label: 'Reservada' },
} as const

export const OrderStatus = {
    IN_PREPARE: { value: 'IN_PREPARE', label: 'Em Preparo' },
    READY: { value: 'READY', label: 'Pronta' },
    CANCELED: { value: 'CANCELED', label: 'Cancelada' },
} as const

export interface OrderFilters {
    tableId?: string;
    guestTabIds?: number[];
    orderIds?: number[];
    orderStatuses?: OrderStatus;
    guestTabStatuses?: GuestTabStatus;
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

interface Order {
  id: string;
  productName: string;
  amount: number;
  unitPrice: number;
  totalItemPrice: number;
  status: OrderStatus;
  observation: string;
  orderedTime: string;
  waiterName: string;
}

interface GuestTab {
  id: string;
  status: GuestTabStatus;
  timeOpened: Date | undefined;
  totalGuestTabPrice: number;
  waiterName?: string;
  orders: Order[];
}