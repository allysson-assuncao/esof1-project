export interface LoginFormData {
    email: string;
    password: string;
}

export interface RegisterFormData {
    username: string;
    email: string;
    password: string;
    name: string;
    role: string;
}

export interface WorkstationRegisterFormData {
    name: string;
    categoryIds: string[];
}

export interface ProductRegisterFormData {
    name: string;
    description: string;
    price: number;
    idCategory: string;
}

export interface LocalTableRegisterFormData {
    number: number;
}

export interface GuestTabRegisterFormData {
    localTableId: string;
    guestName: string;
}

export interface RegisterOrderItemDTO {
  productId: string;
  amount: number;
  observation?: string;
}

export interface RegisterOrdersFormData {
  guestTabId: number;
  parentOrderId: number | null;
  waiterEmail: string;
  items: RegisterOrderItemDTO[];
}

export type CategoryFormData = {
    name: string;
    isMultiple: boolean;
    subcategories: string[];
    workstationId: string;
};
