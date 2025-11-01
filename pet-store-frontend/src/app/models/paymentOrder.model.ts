export enum PaymentType {
  CREDIT_CARD = "CREDIT_CARD",
  DEBIT_CARD = "DEBIT_CARD",
  E_WALLET = "E_WALLET",
  PAYPAL = "PAYPAL",
}

export enum WalletType {
  GRABPAY = "GRABPAY",
  BOOSTPAY = "BOOSTPAY",
  TOUCHNGO = "TOUCHNGO"
}

export function getWalletType(value: string): WalletType | undefined {
  switch (value) {
    case "GRABPAY":
      return WalletType.GRABPAY;
    case "BOOSTPAY":
      return WalletType.BOOSTPAY;
    case "TOUCHNGO":
      return WalletType.TOUCHNGO;
    default:
      return undefined;
  }
}

export interface PaymentOrderRequest {

  paymentType: string;
  paymentNote?: string;
  shippingAddressId: number;
  billingAddressId?: number;
  walletType?: WalletType;
  cardNumber?: string;
  walletId?: string;
  paypalId?: string;

}
