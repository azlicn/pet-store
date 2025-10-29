
export enum DeliveryStatus {
  PENDING = "PENDING",
  SHIPPED = "SHIPPED",
  DELIVERED = "DELIVERED"
}

export interface Delivery {
  id?: number;
  address: string;
  status: string;
  createdAt: string;
  shippedAt: string;
  deliveredAt: string;
}