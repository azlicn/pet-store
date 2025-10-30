import { DiscountService } from "../../services/discount.service";
import { Component, OnInit } from "@angular/core";
import { MatDialogRef } from "@angular/material/dialog";
import { StoreService } from "../../services/store.service";
import { Router } from "@angular/router";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";
import { MatCardModule } from "@angular/material/card";
import { MatChipsModule } from "@angular/material/chips";
import { MatIconModule } from "@angular/material/icon";
import { MatButtonModule } from "@angular/material/button";
import { AuthService } from "src/app/services/auth.service";
import { FormsModule } from "@angular/forms";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { MatSnackBar } from "@angular/material/snack-bar";

@Component({
  selector: "app-cart",
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatCardModule,
    MatChipsModule,
    MatIconModule,
    MatButtonModule,
    FormsModule,
    MatFormFieldModule,
    MatInputModule,
  ],
  templateUrl: "./cart.component.html",
  styleUrl: "./cart.component.scss",
})
export class CartComponent implements OnInit {
  availableDiscounts: any[] = [];
  cart: any;
  userId = this.authService.getCurrentUser()?.id;
  discountCode: string = "";
  total = 0;

  constructor(
    private storeService: StoreService,
    private router: Router,
    private snackBar: MatSnackBar,
    public authService: AuthService,
    private dialogRef: MatDialogRef<CartComponent>,
    private discountService: DiscountService
  ) {}

  ngOnInit() {
    this.loadCart();
    this.discountService.getAvailableActiveDiscounts().subscribe({
      next: (discounts) => (this.availableDiscounts = discounts),
      error: () => (this.availableDiscounts = []),
    });
  }

  loadCart() {
    if (this.userId) {
      this.storeService.getCart(this.userId).subscribe({
        next: (data) => {
          this.cart = data;
          this.total = Array.isArray(data?.items)
            ? data.items.reduce(
                (sum: number, item: any) => sum + (item.price || 0),
                0
              )
            : 0;
        },
        error: (err) => console.error("Failed to load cart", err),
      });
    }
  }

  removeItem(itemId: number) {
    const prevDiscountCode = this.discountCode;
    if (typeof this.userId === "number") {
      this.storeService.removeItem(itemId, this.userId).subscribe(() => {
        this.storeService.updateCartItemCount(this.userId!);
        this.loadCartAndRestoreDiscount(prevDiscountCode);
      });
    }
  }

  loadCartAndRestoreDiscount(discountCode: string) {
    if (this.userId) {
      this.storeService.getCart(this.userId).subscribe({
        next: (data) => {
          this.cart = data;
          this.total = Array.isArray(data?.items)
            ? data.items.reduce(
                (sum: number, item: any) => sum + (item.price || 0),
                0
              )
            : 0;
          if (discountCode && discountCode.trim()) {
            this.discountCode = discountCode;
            this.applyDiscount();
          }
        },
        error: (err) => console.error("Failed to load cart", err),
      });
    }
  }

  copyDiscountCode(code: string) {
    this.discountCode = code;
    navigator.clipboard.writeText(code).then(() => {
      this.snackBar.open(`Discount code pasted: ${code}`, "Close", {
        duration: 2000,
        panelClass: ["success-snackbar"],
      });
    });
  }

  applyDiscount() {
    if (!this.discountCode.trim()) {
      this.snackBar.open("Please enter a discount code.", "Close", {
        duration: 3000,
        panelClass: ["error-snackbar"],
      });
      return;
    }

    this.storeService
      .validateDiscountCode(this.discountCode, this.total)
      .subscribe({
        next: (res) => {
          this.cart.discount = {
            code: res.code,
            percentage: res.percentage,
            discountAmount: res.discountAmount,
          };
          this.cart.totalAfterDiscount = res.newTotal;
          this.snackBar.open(
            `Discount "${res.code}" applied (${res.percentage}% off)!`,
            "Close",
            {
              duration: 3000,
              panelClass: ["success-snackbar"],
            }
          );
        },
        error: (err) => {
          const msg = err.error?.message || "Invalid discount code";
          this.snackBar.open(msg, "Close", {
            duration: 3000,
            panelClass: ["error-snackbar"],
          });
        },
      });
  }

  removeDiscount() {
    this.cart.discount = undefined;
    this.cart.totalAfterDiscount = undefined;
    this.discountCode = "";
    this.total = Array.isArray(this.cart?.items)
      ? this.cart.items.reduce(
          (sum: number, item: any) => sum + (item.price || 0),
          0
        )
      : 0;
  }

  checkout() {
    this.storeService.checkout(this.discountCode).subscribe({
      next: (order: any) => {
        this.checkoutAndCloseModal(order);
      },
      error: (err) => {
        const msg = err.error?.message || "Checkout failed";
        this.snackBar.open(msg, "Close", {
          duration: 3000,
          panelClass: ["error-snackbar"],
        });
      },
    });
  }

  closeDialog() {
    this.dialogRef.close();
  }

  private checkoutAndCloseModal(order: any) {
    this.snackBar.open("Order created successfully!", "Close", {
      duration: 3000,
      panelClass: ["success-snackbar"],
    });
    // Clear cart item count observable
    this.storeService.clearCartItemCount();
    this.closeDialog();
    this.router.navigate(["/checkout", order.id]);
  }
}
