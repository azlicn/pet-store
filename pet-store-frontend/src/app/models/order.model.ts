import { Address } from "./address.model";
import { Delivery } from "./delivery.model";
import { Discount } from "./discount.model";
import { OrderItem } from "./orderItem.model";
import { Payment } from "./payment.model";

export enum OrderStatus {
  PLACED = "PLACED",
  APPROVED = "APPROVED",
  DELIVERED = "DELIVERED",
  CANCELED = "CANCELED",
}

export interface Order {
  id?: number;
  orderNumber: string;
  status: string;
  totalAmount: number;
  discount: Discount | null;
  payment: Payment;
  delivery: Delivery;
  items: OrderItem[];
  createdAt?: string;
  updatedAt?: string;
  shippingAddress: Address;
  billingAddress: Address;
}