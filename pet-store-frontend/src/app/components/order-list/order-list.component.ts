import { OrderCardComponent } from "../order-card/order-card.component";
import { Component, OnInit, inject } from "@angular/core";
import { CommonModule } from "@angular/common";
import { MatCardModule } from "@angular/material/card";
import { MatIconModule } from "@angular/material/icon";
import { MatTableModule } from "@angular/material/table";
import { MatProgressSpinnerModule } from "@angular/material/progress-spinner";
import { MatSnackBarModule, MatSnackBar } from "@angular/material/snack-bar";
import { StoreService } from "../../services/store.service";
import { MatButtonModule } from "@angular/material/button";
import { AuthService } from "src/app/services/auth.service";
import { ActivatedRoute, Router } from "@angular/router";

@Component({
  selector: "app-order-list",
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatIconModule,
    MatTableModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
    OrderCardComponent,
  ],
  templateUrl: "./order-list.component.html",
  styleUrl: "./order-list.component.scss",
})
export class OrderListComponent implements OnInit {
  orders: any[] = [];
  loading = false;
  displayedColumns: string[] = [
    "orderNumber",
    "totalAmount",
    "orderStatus",
    "orderCreatedAt",
    "shippingStatus",
    "shippedAt",
    "deliveredAt",
    "actions",
  ];

  constructor(
    public authService: AuthService,
    private storeService: StoreService,
    private snackBar: MatSnackBar,
  ) {}

  ngOnInit(): void {
    this.loadOrders();
  }

  isAdmin(): boolean {
    return this.authService.isAdmin();
  }

  loadOrders(): void {
    this.loading = true;
    this.storeService.getAllOrders().subscribe({
      next: (orders) => {
        this.orders = orders;
        this.loading = false;
      },
      error: (err) => {
        this.loading = false;
        this.snackBar.open("Failed to load orders", "Close", {
          duration: 3000,
          panelClass: ["error-snackbar"],
        });
      },
    });
  }

  editOrder(orderId: number): void {
    // Implement order editing logic here
  }

  deleteOrder(orderId: number): void {
    // Implement order deletion logic here
  }
}
