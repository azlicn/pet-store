import { Component, OnInit } from "@angular/core";
import { DiscountService } from "../../services/discount.service";
import { Discount } from "../../models/discount.model";
import { MatSnackBar, MatSnackBarModule } from "@angular/material/snack-bar";
import { CommonModule } from "@angular/common";
import { MatButtonModule } from "@angular/material/button";
import { MatCardModule } from "@angular/material/card";
import { MatDialog, MatDialogModule } from "@angular/material/dialog";
import { MatIconModule } from "@angular/material/icon";
import { MatProgressSpinnerModule } from "@angular/material/progress-spinner";
import { MatTableModule } from "@angular/material/table";
import { MatTooltipModule } from "@angular/material/tooltip";
import { RouterModule } from "@angular/router";
import { HttpErrorResponse } from "@angular/common/http";
import { ErrorHandlerService } from "src/app/services/error-handler.service";
import {
  ConfirmDialogComponent,
  ConfirmDialogData,
} from "../confirm-dialog/confirm-dialog.component";

@Component({
  selector: "app-discount-list",
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatTableModule,
    MatDialogModule,
    MatProgressSpinnerModule,
    MatTooltipModule,
    MatSnackBarModule,
  ],
  templateUrl: "./discount-list.component.html",
  styleUrls: ["./discount-list.component.scss"],
})
export class DiscountListComponent implements OnInit {
  discounts: Discount[] = [];
  loading = false;
  displayedColumns: string[] = [
    "id",
    "code",
    "percentage",
    "validFrom",
    "validTo",
    "active",
    "actions",
  ];

  constructor(
    private discountService: DiscountService,
    private errorHandler: ErrorHandlerService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadDiscounts();
  }

  loadDiscounts(): void {
    this.loading = true;
    this.discountService.getAllDiscounts().subscribe({
      next: (discounts) => {
        this.discounts = discounts;
        this.loading = false;
      },
      error: (error: HttpErrorResponse) => {
        console.error("Error loading discounts:", error);

        const errorMessage = this.errorHandler.extractErrorMessage(
          error,
          "Failed to load discounts"
        );

        this.snackBar.open(errorMessage, "Close", {
          duration: 3000,
          panelClass: ["error-snackbar"],
        });
        this.loading = false;
      },
    });
  }

  onDeleteDiscount(discount: Discount): void {
    if (!discount.id) {
      return;
    }

    const dialogData: ConfirmDialogData = {
      title: "Delete Discount",
      message: `Are you sure you want to delete discount "<strong>${discount.code}</strong>"?<br>
                     <strong>
                     This action cannot be undone and will permanently remove this discount from the system.`,
      confirmText: "Delete Discount",
      cancelText: "Keep Discount",
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
      if (result && discount.id) {
        this.deleteDiscount(discount.id);
      }
    });
  }

  private deleteDiscount(discountId: number): void {
    this.discountService.deleteDiscount(discountId).subscribe({
      next: () => {
        this.snackBar.open("Discount deleted successfully", "Close", {
          duration: 3000,
          panelClass: ["success-snackbar"],
        });
        this.loadDiscounts();
      },
      error: (error: HttpErrorResponse) => {
        console.error("Error deleting discount:", error);

        const errorMessage = this.errorHandler.extractErrorMessage(
          error,
          "Failed to delete discount"
        );
        const duration = this.errorHandler.getErrorDuration(error);

        this.snackBar.open(errorMessage, "Close", {
          duration: duration,
          panelClass: ["error-snackbar"],
        });
      },
    });
  }
}
