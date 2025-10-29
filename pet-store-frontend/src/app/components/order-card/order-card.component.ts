import { Component, Input } from "@angular/core";
import { CommonModule } from "@angular/common";
import { MatCardModule } from "@angular/material/card";
import { MatIconModule } from "@angular/material/icon";
import { MatButtonModule } from "@angular/material/button";
import { MatTooltipModule } from "@angular/material/tooltip";
import { MatDialog } from "@angular/material/dialog";
import { AuthService } from "src/app/services/auth.service";
import { RouterModule } from "@angular/router";
import { StoreService } from '../../services/store.service';
import { UpdateDeliveryStatusDialogComponent } from '../update-delivery-status-dialog/update-delivery-status-dialog.component';

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
  @Input() editOrder!: (id: number) => void;
  @Input() deleteOrder!: (id: number) => void;

  constructor(public authService: AuthService, private dialog: MatDialog, private storeService: StoreService) {}

  isAdmin(): boolean {
    return this.authService.isAdmin();
  }

  updateDeliveryStatus(orderId: number): void {
    const dialogRef = this.dialog.open(UpdateDeliveryStatusDialogComponent, {
      width: '400px',
      data: {
        status: this.order.delivery?.status,
        date: this.order.delivery?.deliveredAt || ''
      }
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.storeService.updateDeliveryStatus(orderId, result.status, result.date).subscribe({
          next: () => {
            // Optionally refresh order or show success
          },
          error: () => {
            // Optionally show error
          }
        });
      }
    });
  }

  canViewOrderDetails(): boolean {
    //let owner = this.authService.getCurrentUser()?.id === this.order.user.id;
    return this.order && (this.order.status === 'APPROVED' || this.order.status === 'DELIVERED');
  }

  canEditOrder(): boolean {
    // Allow editing only if the order status is 'PLACED'
    return this.order && (this.order.status === 'PLACED');
  }

  canUpdateDeliveryStatus(): boolean {
    // Allow updating delivery status only if the order status is 'Approved' or 'Shipped'
    return this.order && this.isAdmin() && (this.order.status === 'APPROVED' || this.order.status === 'SHIPPED');
  }


  canDeleteOrder(): boolean {
    // Allow deletion only if the order status is 'Cancelled'
    return this.order && this.isAdmin() && (this.order.status === 'CANCELLED');
  }

}
