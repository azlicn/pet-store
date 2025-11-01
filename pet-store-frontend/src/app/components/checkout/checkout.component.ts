import { Component, OnInit } from "@angular/core";
import { ActivatedRoute, Router, RouterModule } from "@angular/router";
import { StoreService } from "../../services/store.service";
import { Address } from "../../models/address.model";
import { Order } from "../../models/order.model";
import { MatSnackBar } from "@angular/material/snack-bar";
import { AddressService } from "src/app/services/address.service";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { AddressComponent } from "../address/address.component";
import { MatDialog } from "@angular/material/dialog";
import { PaymentProcessingDialogComponent } from "../payment-processing-dialog/payment-processing-dialog.component";
import { ConfirmDialogComponent } from "../confirm-dialog/confirm-dialog.component";
import { MatIconModule } from "@angular/material/icon";
import { MatButtonModule } from "@angular/material/button";
import { MatCheckboxModule } from "@angular/material/checkbox";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { Location } from "@angular/common";
import {
  WalletType,
  getWalletType,
  PaymentOrderRequest,
  PaymentType,
} from "src/app/models/paymentOrder.model";

@Component({
  selector: "app-checkout",
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatButtonModule,
    MatIconModule,
    FormsModule,
    MatCheckboxModule,
    MatFormFieldModule,
    MatInputModule,
  ],
  templateUrl: "./checkout.component.html",
  styleUrl: "./checkout.component.scss",
})
export class CheckoutComponent implements OnInit {
  selectedEwallet: string = "GRABPAY";
  paypalContact: string = "";
  cardNumber: string = "";
  cardHolder: string = "";
  cardExpiry: string = "";
  cardCVV: string = "";
  paymentTypes: string[] = ["CREDIT_CARD", "DEBIT_CARD", "E_WALLET", "PAYPAL"];
  selectedPaymentType: string = "CREDIT_CARD";
  grabPay: string = "";
  boostPay: string = "";
  touchNGo: string = "";
  billingSameAsShipping: boolean = true;
  selectedBillingAddressId?: number = 0;
  paymentNote: string = "";

  addresses: Address[] = [];
  selectedAddressId?: number = 0;
  orderId?: number;
  order?: Order;
  loading = false;

  constructor(
    private storeService: StoreService,
    private addressService: AddressService,
    private router: Router,
    private route: ActivatedRoute,
    private snackBar: MatSnackBar,
    private dialog: MatDialog,
    private location: Location
  ) {
    this.route.paramMap.subscribe((params) => {
      this.orderId = +params.get("orderId")!;
    });
  }

  ngOnInit(): void {
    this.loadAddresses();
    this.setOrder();
  }

  isPaymentInfoValid(): boolean {
    if (
      this.selectedPaymentType === PaymentType.CREDIT_CARD ||
      this.selectedPaymentType === PaymentType.DEBIT_CARD
    ) {
      // Card number: 16-19 digits, Luhn valid
      const cardNum = this.cardNumber.replace(/\s/g, "");
      if (!cardNum.match(/^\d{16,19}$/) || !this.isValidCardNumber(cardNum))
        return false;
      // Expiry: MM/YY format
      if (!this.cardExpiry.match(/^(0[1-9]|1[0-2])\/(\d{2})$/)) return false;
      // CVV: 3-4 digits
      if (!this.cardCVV.match(/^\d{3,4}$/)) return false;
      // Cardholder name: not empty
      if (!this.cardHolder || this.cardHolder.trim().length < 2) return false;
      return true;
    }
    if (this.selectedPaymentType === PaymentType.PAYPAL) {
      // Paypal contact: not empty, basic email or phone format
      if (!this.paypalContact || this.paypalContact.trim().length < 5)
        return false;
      return true;
    }
    if (this.selectedPaymentType === PaymentType.E_WALLET) {
      if (this.selectedEwallet === WalletType.GRABPAY) {
        return !!this.grabPay && this.grabPay.trim().length > 3;
      }
      if (this.selectedEwallet === WalletType.BOOSTPAY) {
        return !!this.boostPay && this.boostPay.trim().length > 3;
      }
      if (this.selectedEwallet === WalletType.TOUCHNGO) {
        return !!this.touchNGo && this.touchNGo.trim().length > 3;
      }
      return false;
    }
    return true;
  }
  onCardNumberInput(event: any) {
    let value = event.target.value.replace(/\D/g, "");
    let formatted = "";
    for (let i = 0; i < value.length; i += 4) {
      formatted += value.substr(i, 4) + " ";
    }
    formatted = formatted.trim();
    this.cardNumber = formatted;
  }

  onCardExpiryInput(event: any) {
    let value = event.target.value.replace(/[^0-9]/g, "");
    if (value.length > 2) {
      value = value.substr(0, 2) + "/" + value.substr(2, 2);
    }
    this.cardExpiry = value;
  }

  isValidCardNumber(num: string): boolean {
    if (!num || num.length < 16 || num.length > 19) return false;
    let sum = 0;
    let shouldDouble = false;
    for (let i = num.length - 1; i >= 0; i--) {
      let digit = parseInt(num.charAt(i));
      if (shouldDouble) {
        digit *= 2;
        if (digit > 9) digit -= 9;
      }
      sum += digit;
      shouldDouble = !shouldDouble;
    }
    return sum % 10 === 0;
  }

  onBillingSameAsShippingChange() {
    if (this.billingSameAsShipping) {
      this.selectedBillingAddressId = undefined;
    }
  }

  setOrder(): void {
    if (this.orderId) {
      this.storeService.getOrder(this.orderId).subscribe({
        next: (order: Order) => {
          this.order = order;
        },
        error: (err: any) => {
          const msg = err.error?.message || "Failed to load order";
          this.snackBar.open(msg, "Close", {
            duration: 3000,
            panelClass: ["error-snackbar"],
          });
        },
      });
    }
  }

  loadAddresses(): void {
    this.addressService.getAddresses().subscribe({
      next: (res: any) => {
        this.addresses = res;
        const defaultAddr = this.addresses.find((a) => a.isDefault);
        this.selectedAddressId = defaultAddr ? defaultAddr.id : undefined;
      },
      error: (err: any) => {
        const msg = err.error?.message || "Failed to load addresses";
        this.snackBar.open(msg, "Close", {
          duration: 3000,
          panelClass: ["error-snackbar"],
        });
      },
    });
  }

  confirmCheckout() {
    if (!this.selectedAddressId) {
      this.snackBar.open("Please select a shipping address.", "Close", {
        duration: 3000,
        panelClass: ["error-snackbar"],
      });
      return;
    }
    const dialogRef = this.dialog.open(PaymentProcessingDialogComponent, {
      width: "400px",
      disableClose: true,
      data: {
        paymentType: this.selectedPaymentType,
        selectedEwallet: this.selectedEwallet,
      },
    });
    dialogRef.afterClosed().subscribe((success) => {
      const paymentOrderRequest = this.buildPaymentOrderRequest();

      if (success && this.order?.id) {
        console.log(
          "Payment successful! Order confirmed.",
          paymentOrderRequest
        );
        this.loading = true;
        this.storeService
          .makePayment(this.order.id, paymentOrderRequest)
          .subscribe({
            next: () => {
              this.loading = false;
              this.snackBar.open("Payment successful! Order placed.", "Close", {
                duration: 3000,
                panelClass: ["success-snackbar"],
              });
              // Optionally navigate or update UI
              this.router.navigate(["/orders"]);
            },
            error: (err: any) => {
              this.loading = false;
              const msg = err.error?.message || "Payment failed";
              this.snackBar.open(msg, "Close", {
                duration: 3000,
                panelClass: ["error-snackbar"],
              });
            },
          });
      }
    });
  }

  goBack(): void {
    this.location.back();
  }

  cancelOrder() {
    if (!this.order?.id) return;
    const dialogData = {
      title: "Cancel Order",
      message: `Are you sure you want to cancel this order?`,
      confirmText: "Cancel Order",
      cancelText: "Keep Order",
      icon: "delete_forever",
    };
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: "450px",
      maxWidth: "90vw",
      data: dialogData,
      disableClose: true,
      panelClass: "confirm-dialog-container",
      hasBackdrop: true,
      backdropClass: "confirm-dialog-backdrop",
    });
    dialogRef.afterClosed().subscribe((result) => {
      if (result && typeof this.order?.id === "number") {
        this.loading = true;
        this.storeService.cancelOrder(this.order.id!).subscribe({
          next: () => {
            this.loading = false;
            this.snackBar.open("Order cancelled.", "Close", {
              duration: 3000,
              panelClass: ["success-snackbar"],
            });
            this.router.navigate(["/orders"]);
          },
          error: (err: any) => {
            this.loading = false;
            const msg = err.error?.message || "Failed to cancel order";
            this.snackBar.open(msg, "Close", {
              duration: 3000,
              panelClass: ["error-snackbar"],
            });
          },
        });
      }
    });
  }

  addNewAddress() {
    const dialogRef = this.dialog.open(AddressComponent, {
      maxWidth: "600px",
      maxHeight: "800px",
      disableClose: true,
      data: { source: "checkout", orderId: this.orderId },
    });
    dialogRef.afterClosed().subscribe(() => {
      this.loadAddresses();
    });
  }

  getSubtotal(): number {
    if (!this.order || !this.order.items) {
      return 0;
    }
    return this.order.items.reduce((sum, item) => sum + item.price, 0);
  }

  getDiscountAmount(): number {
    if (this.order && this.order.discount) {
      return (this.getSubtotal() * this.order.discount.percentage) / 100;
    }
    return 0;
  }

  private buildPaymentOrderRequest(): PaymentOrderRequest {
    let cardNumber: string;
    let walletId: string;
    let paypalId: string;
    //let paymentNote = "";
    if (this.selectedPaymentType === PaymentType.E_WALLET) {
      //let ewalletValue = "";
      if (this.selectedEwallet === WalletType.GRABPAY) {
        //ewalletValue = this.grabPay;
        walletId = this.grabPay;
      } else if (this.selectedEwallet === WalletType.BOOSTPAY) {
        //ewalletValue = this.boostPay;
        walletId = this.boostPay;
      } else if (this.selectedEwallet === WalletType.TOUCHNGO) {
        //ewalletValue = this.touchNGo;
        walletId = this.touchNGo;
      }
      //paymentNote = this.selectedEwallet + " Account ID: " + ewalletValue;
    } else if (this.selectedPaymentType === PaymentType.PAYPAL) {
      walletId = this.paypalContact;
      //paymentNote = "PAYPAL Account ID: " + this.paypalContact;
    } else if (
      this.selectedPaymentType === PaymentType.CREDIT_CARD ||
      this.selectedPaymentType === PaymentType.DEBIT_CARD
    ) {
      //const cardNum = this.maskCardNumber(this.cardNumber);
      //paymentNote = "Last 4 digits: " + cardNum.slice(-4);
      cardNumber = this.maskCardNumber(this.cardNumber);
    }
    return {
      paymentType: this.selectedPaymentType,
      shippingAddressId: this.selectedAddressId!,
      billingAddressId: this.billingSameAsShipping
        ? this.selectedAddressId!
        : this.selectedBillingAddressId,
      walletType: this.validateAndReturnWalletType(this.selectedPaymentType, this.selectedEwallet),
      walletId: this.validateAndReturnWalletId(this.selectedPaymentType, walletId!),
      paypalId: this.validateAndReturnPaypalId(this.selectedPaymentType, walletId!),
      cardNumber: this.validateCardAndReturnCardNNumber(
        this.selectedPaymentType,
        this.cardNumber
      ),
    };
  }

  private maskCardNumber(cardNumber: string): string {
    if (!cardNumber || cardNumber.length <= 4) {
      return cardNumber;
    }

    const visibleDigits = 4;
    const maskedSection = "*".repeat(cardNumber.length - visibleDigits);
    const visibleSection = cardNumber.slice(-visibleDigits);

    return maskedSection + visibleSection;
  }

  private validateAndReturnWalletType(
    paymentType: string,
    walletValue: string
  ): WalletType | undefined {
    if (paymentType === PaymentType.E_WALLET) {
      return getWalletType(walletValue);
    }
    return undefined;
  }

  private validateCardAndReturnCardNNumber(
    paymentType: string,
    cardNumber: string
  ): string | undefined {
    if (
      paymentType === PaymentType.CREDIT_CARD ||
      paymentType === PaymentType.DEBIT_CARD
    ) {
      return this.maskCardNumber(cardNumber);
    }
    return undefined;
  }

  private validateAndReturnPaypalId(
    paymentType: string,
    paypalId: string
  ): string | undefined {
    if (paymentType === PaymentType.PAYPAL) {
      return paypalId;
    }
    return undefined;
  }

  private validateAndReturnWalletId(
    paymentType: string,
    walletId: string
  ): string | undefined {
    if (paymentType === PaymentType.E_WALLET) {
      return walletId;
    }
    return undefined;
  } 

}
