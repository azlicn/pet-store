import { PaymentType } from "./paymentOrder.model";

export interface Payment {
  id?: number;
  amount: number;
  status: string;
  paymentType: PaymentType;
  paymentNote: string;
  paidAt: string;
}