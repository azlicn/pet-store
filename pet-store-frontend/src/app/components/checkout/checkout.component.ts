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
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { PaymentProcessingDialogComponent } from "../payment-processing-dialog/payment-processing-dialog.component";
import { ConfirmDialogComponent } from "../confirm-dialog/confirm-dialog.component";
import { MatIconModule } from "@angular/material/icon";
import { MatButtonModule } from "@angular/material/button";
import { MatCheckboxModule } from "@angular/material/checkbox";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { PaymentOrderRequest } from "src/app/models/paymentOrder.model";

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
    private dialog: MatDialog
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
      this.selectedPaymentType === "CREDIT_CARD" ||
      this.selectedPaymentType === "DEBIT_CARD"
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
    if (this.selectedPaymentType === "PAYPAL") {
      // Paypal contact: not empty, basic email or phone format
      if (!this.paypalContact || this.paypalContact.trim().length < 5)
        return false;
      return true;
    }
    if (this.selectedPaymentType === "E_WALLET") {
      if (this.selectedEwallet === "GRABPAY") {
        return !!this.grabPay && this.grabPay.trim().length > 3;
      }
      if (this.selectedEwallet === "BOOSTPAY") {
        return !!this.boostPay && this.boostPay.trim().length > 3;
      }
      if (this.selectedEwallet === "TOUCHNGO") {
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

  cancelOrder() {
    if (!this.order?.id) return;
    const dialogData = {
      title: "Cancel Order",
      message: `Are you sure you want to cancel this order?<br><strong>This action cannot be undone and will permanently remove this order from the system.</strong>`,
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

    let paymentNote = "";
    if (this.selectedPaymentType === "E_WALLET") {
      let ewalletValue = "";
      if (this.selectedEwallet === "GRABPAY") {
        ewalletValue = this.grabPay;
      } else if (this.selectedEwallet === "BOOSTPAY") {
        ewalletValue = this.boostPay;
      } else if (this.selectedEwallet === "TOUCHNGO") {
        ewalletValue = this.touchNGo;
      }
      paymentNote = this.selectedEwallet + " by " + ewalletValue;
    } else if (this.selectedPaymentType === "PAYPAL") {
      paymentNote = this.selectedEwallet + " by " + this.paypalContact;
    } else if (
      this.selectedPaymentType === "CREDIT_CARD" ||
      this.selectedPaymentType === "DEBIT_CARD"
    ) {
      const cardNum = this.cardNumber.replace(/\s/g, "");
      paymentNote = "Last 4 digits: " + cardNum.slice(-4);
    }
    return {
      paymentType: this.selectedPaymentType,
      shippingAddressId: this.selectedAddressId!,
      billingAddressId: this.billingSameAsShipping
        ? this.selectedAddressId!
        : this.selectedBillingAddressId,
      paymentNote: paymentNote,
    };
  }
}
