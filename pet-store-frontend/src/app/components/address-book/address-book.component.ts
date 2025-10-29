import { Component, OnInit } from "@angular/core";
import { CommonModule } from "@angular/common";
import { MatCardModule } from "@angular/material/card";
import { MatButtonModule } from "@angular/material/button";
import { MatIconModule } from "@angular/material/icon";
import { MatTooltipModule } from "@angular/material/tooltip";
import { MatProgressSpinnerModule } from "@angular/material/progress-spinner";
import { MatExpansionModule } from "@angular/material/expansion";
import {
  FormBuilder,
  FormGroup,
  Validators,
  ReactiveFormsModule,
} from "@angular/forms";
import { Router, ActivatedRoute } from "@angular/router";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { MatSnackBarModule, MatSnackBar } from "@angular/material/snack-bar";
import { AddressService } from "src/app/services/address.service";
import { Address } from "src/app/models/address.model";
import { AuthService } from "src/app/services/auth.service";
import { AddressComponent } from "../address/address.component";
import { ConfirmDialogComponent, ConfirmDialogData } from "../confirm-dialog/confirm-dialog.component";

@Component({
  selector: "app-address-book",
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatExpansionModule,
    MatTooltipModule,
  ],
  templateUrl: "./address-book.component.html",
  styleUrl: "./address-book.component.scss",
})
export class AddressBookComponent implements OnInit {
  constructor(
    private fb: FormBuilder,
    private addressService: AddressService,
    private authService: AuthService,
    private snackBar: MatSnackBar,
    private router: Router,
    private route: ActivatedRoute,
    private dialog: MatDialog
  ) {}

  loading = false;
  addresses: Address[] = [];
  userId = this.authService.getCurrentUser()?.id;
  userFullName =
    this.authService.getCurrentUser()?.firstName +
    " " +
    this.authService.getCurrentUser()?.lastName;

  ngOnInit(): void {
    this.loadAddresses();
  }

  loadAddresses() {
    this.loading = true;
    if (this.userId) {
      this.addressService.getAddresses().subscribe({
        next: (addresses) => {
          this.addresses = addresses;
          this.loading = false;
        },
        error: () => {
          this.loading = false;
        },
      });
    }
  }

  onDeleteAddress(addressId: number | undefined): void {
      if (!addressId) {
        return;
      }
  
      const dialogData: ConfirmDialogData = {
            title: 'Delete Address',
            message: `Are you sure you want to delete this address?<br>
                     This action cannot be undone and will permanently remove this address from the system.`,
            confirmText: 'Delete Address',
            cancelText: 'Keep Address',
            icon: 'delete_forever'
          };
  
      const dialogRef = this.dialog.open(ConfirmDialogComponent, {
        width: '450px',
        maxWidth: '90vw',
        data: dialogData,
        disableClose: true,
        panelClass: 'confirm-dialog-container',
        hasBackdrop: true,
        backdropClass: 'confirm-dialog-backdrop'
      });
  
      dialogRef.afterClosed().subscribe(result => {
        if (result && addressId) {
          this.deleteAddress(addressId);
        }
      });
    }

  

  private deleteAddress(addressId: number | undefined) {

    if (addressId) {
      this.addressService.deleteAddress(addressId).subscribe({
        next: () => {
          this.snackBar.open("Address deleted successfully", "Close", {
            duration: 3000,
          });
          this.loadAddresses();
        },
        error: () => {
          this.snackBar.open("Failed to delete address", "Close", {
            duration: 3000,
          });
        },
      });
    }
  }


  openAddressForm(address?: Address) {
    if (address) {
      const dialogRef = this.dialog.open(AddressComponent, {
        maxWidth: "600px",
        maxHeight: "800px",
        disableClose: true,
        data: { source: "address-book", address },
      });
      dialogRef.afterClosed().subscribe((result) => {
        if (result) {
          this.loadAddresses();
        }
      });
    } else {
      const dialogRef = this.dialog.open(AddressComponent, {
        maxWidth: "600px",
        maxHeight: "800px",
        disableClose: true,
        data: { source: "address-book" },
      });
      dialogRef.afterClosed().subscribe((result) => {
        if (result) {
          this.loadAddresses();
        }
      });
    }
  }
}
