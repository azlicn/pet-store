import { Component, Input } from "@angular/core";
import { CommonModule } from "@angular/common";
import { MatCardModule } from "@angular/material/card";
import { MatIconModule } from "@angular/material/icon";
import { MatButtonModule } from "@angular/material/button";
import { MatTooltipModule } from "@angular/material/tooltip";
import { MatDialog } from "@angular/material/dialog";
import { AuthService } from "src/app/services/auth.service";
import { Router, RouterModule } from "@angular/router";
import { StoreService } from "../../services/store.service";
import { UpdateDeliveryStatusDialogComponent } from "../update-delivery-status-dialog/update-delivery-status-dialog.component";
import { MatSnackBar } from "@angular/material/snack-bar";

@Component({
  selector: "app-order-card",
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatIconModule,
    MatButtonModule,
    MatTooltipModule,
    RouterModule,
  ],
  templateUrl: "./order-card.component.html",
  styleUrl: "./order-card.component.scss",
})
export class OrderCardComponent {
  @Input() order: any;

  constructor(
    public authService: AuthService,
    private dialog: MatDialog,
    private storeService: StoreService,
    private snackBar: MatSnackBar,
    private router: Router
  ) {}

  isAdmin(): boolean {
    return this.authService.isAdmin();
  }

  updateDeliveryStatus(orderId: number): void {
    const dialogRef = this.dialog.open(UpdateDeliveryStatusDialogComponent, {
      width: "400px",
      data: {
        orderNumber: this.order.orderNumber,
        status: this.order.delivery?.status,
        date: this.order.delivery?.deliveredAt || "",
      },
    });
    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        console.log("Updating delivery status with:", result);
        this.storeService
          .updateDeliveryStatus(orderId, result.status, result.date)
          .subscribe({
            next: () => {
              this.snackBar.open(
                `Delivery status updated to "${result.status}"`,
                "Close",
                { duration: 3000 }
              );
              this.router
                .navigateByUrl("/", { skipLocationChange: true })
                .then(() => {
                  this.router.navigate(["/orders"]);
                });
            },
            error: () => {
              this.snackBar.open("Failed to update delivery status", "Close", {
                duration: 3000,
              });
            },
          });
      }
    });
  }

  canViewOrderDetails(): boolean {
    return (
      this.order &&
      (this.order.status === "APPROVED" || this.order.status === "DELIVERED")
    );
  }

  canEditOrder(): boolean {
    return this.order && this.order.status === "PLACED";
  }

  canUpdateDeliveryStatus(): boolean {
    return (
      this.order &&
      this.isAdmin() &&
      (this.order.status === "APPROVED" || this.order.status === "SHIPPED")
    );
  }

  canDeleteOrder(): boolean {
    return this.order && this.isAdmin() && this.order.status === "CANCELLED";
  }

  deleteOrder(orderId: number): void {
    // Implement order deletion logic here
  }
}
