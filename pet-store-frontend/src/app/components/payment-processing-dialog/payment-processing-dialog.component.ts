import { Component, Inject } from "@angular/core";
import { CommonModule } from "@angular/common";
import { MatProgressBarModule } from "@angular/material/progress-bar";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { FormsModule } from "@angular/forms";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";
import { MatButtonModule } from "@angular/material/button";
import { WalletType, PaymentType } from "src/app/models/paymentOrder.model";

interface PaymentDialogData {
  paymentType: string;
  selectedEwallet?: string;
}

@Component({
  selector: "app-payment-processing-dialog",
  standalone: true,
  imports: [
    CommonModule,
    MatProgressBarModule,
    MatFormFieldModule,
    MatInputModule,
    FormsModule,
    MatButtonModule,
  ],
  templateUrl: "./payment-processing-dialog.component.html",
  styleUrls: ["./payment-processing-dialog.component.scss"],
})
export class PaymentProcessingDialogComponent {
  progress = 0;
  intervalId: any;
  logo: string;
  password: string = "";
  passwordStep: boolean = false;
  animationStep: boolean = false;
  
  constructor(
    public dialogRef: MatDialogRef<PaymentProcessingDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: PaymentDialogData
  ) {
    this.logo = this.getLogo(data.paymentType, data.selectedEwallet);
    if (
      data.paymentType === "CREDIT_CARD" ||
      data.paymentType === "DEBIT_CARD"
    ) {
      this.animationStep = true;
      this.startProgress();
    } else {
      this.passwordStep = true;
    }
  }

  onPasswordProceed() {
    if (this.password && this.password.length >= 4) {
      this.passwordStep = false;
      this.animationStep = true;
      this.startProgress();
    }
  }

  onCancel() {
    this.dialogRef.close(false);
  }

  startProgress() {
    this.progress = 0;
    this.intervalId = setInterval(() => {
      this.progress += 100 / 15;
      if (this.progress >= 100) {
        clearInterval(this.intervalId);
        setTimeout(() => this.dialogRef.close(true), 500);
      }
    }, 1000);
  }

  getLogo(type: string, selectedEwallet?: string): string {
    if (type === PaymentType.CREDIT_CARD || type === PaymentType.DEBIT_CARD)
      return "assets/images/visa_master.png";
    if (type === PaymentType.PAYPAL) return "assets/images/paypal.png";
    if (type === PaymentType.E_WALLET) {
      if (selectedEwallet === WalletType.GRABPAY) return "assets/images/grabpay.png";
      if (selectedEwallet === WalletType.BOOSTPAY) return "assets/images/boostpay.png";
      if (selectedEwallet === WalletType.TOUCHNGO) return "assets/images/tng.png";
      return "assets/images/grabpay.png";
    }
    return "";
  }
}
