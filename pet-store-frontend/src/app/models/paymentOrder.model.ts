export enum PaymentType {
  CREDIT_CARD = "CREDIT_CARD",
  DEBIT_CARD = "DEBIT_CARD",
  E_WALLET = "E_WALLET",
  PAYPAL = "PAYPAL",
}

export interface PaymentOrderRequest {
  paymentType: string;
  paymentNote?: string;
  shippingAddressId: number;
  billingAddressId?: number;
}
