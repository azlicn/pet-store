import { Component, Inject, OnInit } from "@angular/core";
import { CommonModule } from "@angular/common";
import {
  FormBuilder,
  FormGroup,
  Validators,
  ReactiveFormsModule,
} from "@angular/forms";
import { Router, ActivatedRoute } from "@angular/router";
import { MatCardModule } from "@angular/material/card";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { MatButtonModule } from "@angular/material/button";
import { MatIconModule } from "@angular/material/icon";
import { MatSnackBarModule, MatSnackBar } from "@angular/material/snack-bar";
import { MatProgressSpinnerModule } from "@angular/material/progress-spinner";
import { MatCheckboxModule } from "@angular/material/checkbox";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";
import { AddressService } from "src/app/services/address.service";
import { AuthService } from "src/app/services/auth.service";

@Component({
  selector: "app-address",
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatSnackBarModule,
    MatProgressSpinnerModule,
    MatCheckboxModule,
  ],
  templateUrl: "./address.component.html",
  styleUrls: ["./address.component.scss"],
})
export class AddressComponent implements OnInit {
  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    private fb: FormBuilder,
    private router: Router,
    private route: ActivatedRoute,
    private snackBar: MatSnackBar,
    private dialogRef: MatDialogRef<AddressComponent>,
    private addressService: AddressService,
    private authService: AuthService
  ) {
    this.addressForm = this.fb.group({
      phoneNumber: ["", Validators.required],
      street: ["", Validators.required],
      city: ["", Validators.required],
      state: ["", Validators.required],
      postalCode: ["", [Validators.required, Validators.pattern("^[0-9]{5}$")]],
      country: ["", Validators.required],
      isDefault: [true],
    });
  }

  addressForm: FormGroup;
  isEditMode = false;
  loading = false;
  submitting = false;
  userId = this.authService.getCurrentUser()?.id;
  userFullName =
    this.authService.getCurrentUser()?.firstName +
    " " +
    this.authService.getCurrentUser()?.lastName;

  ngOnInit(): void {
    // If dialog data contains address, populate form for edit
    if (this.data?.address) {
      this.isEditMode = true;
      this.addressForm.patchValue({
        ...this.data.address,
      });
    } else {
      const addressId = this.route.snapshot.paramMap.get("id");
      if (addressId) {
        this.isEditMode = true;
        this.loadAddress(addressId);
      }
    }
  }

  loadAddress(id: string): void {
    // Load address data by ID
  }

  get street() {
    return this.addressForm.get("street");
  }

  get phoneNumber() {
    return this.addressForm.get("phoneNumber");
  }

  get city() {
    return this.addressForm.get("city");
  }

  get state() {
    return this.addressForm.get("state");
  }

  get postalCode() {
    return this.addressForm.get("postalCode");
  }

  get country() {
    return this.addressForm.get("country");
  }

  get isDefault() {
    return this.addressForm.get("isDefault");
  }

  onSubmit(): void {
    if (this.addressForm.valid) {
      this.submitting = true;
      const addressData = this.addressForm.value;
      addressData.fullName = this.userFullName;
      if (addressData.isDefault) {
        // Check if another default address exists
        this.addressService.getAddresses().subscribe({
          next: (addresses) => {
            const otherDefault = addresses.some(
              (addr) =>
                addr.isDefault &&
                (!this.isEditMode || addr.id !== this.data?.address?.id)
            );
            if (otherDefault) {
              this.snackBar.open(
                "Only one default address is allowed. Please uncheck default on other addresses first.",
                "Close",
                {
                  duration: 4000,
                  panelClass: ["error-snackbar"],
                }
              );
              this.submitting = false;
              return;
            }
            this.saveOrUpdateAddress(addressData);
          },
          error: () => {
            this.snackBar.open("Failed to check existing addresses.", "Close", {
              duration: 3000,
              panelClass: ["error-snackbar"],
            });
            this.submitting = false;
          },
        });
      } else {
        this.saveOrUpdateAddress(addressData);
      }
    }
  }

  saveOrUpdateAddress(addressData: any) {
    if (this.isEditMode) {
      this.addressService.updateAddress(this.data.address.id, addressData).subscribe({
        next: (res) => {
          this.snackBar.open("Address updated successfully", "Close", {
            duration: 3000,
            panelClass: ["success-snackbar"],
          });
          this.submitting = false;
          this.dialogRef.close(true);
          if (this.data?.source === "address-book") {
            this.router.navigate(["/address-book"]);
          } else {
            this.router.navigate(["/checkout/" + this.data?.orderId]);
          }
        },
        error: (error) => {
          console.error("Error updating address:", error);
          this.snackBar.open("Error updating address", "Close", {
            duration: 3000,
            panelClass: ["error-snackbar"],
          });
          this.submitting = false;
        },
      });
    } else {
      this.addressService.createAddress(addressData).subscribe({
        next: (res) => {
          this.snackBar.open("Address saved successfully", "Close", {
            duration: 3000,
            panelClass: ["success-snackbar"],
          });
          this.submitting = false;
          this.dialogRef.close(true);
          if (this.data?.source === "address-book") {
            this.router.navigate(["/address-book"]);
          } else {
            this.router.navigate(["/checkout/" + this.data?.orderId]);
          }
        },
        error: (error) => {
          console.error("Error saving address:", error);
          this.snackBar.open("Error saving address", "Close", {
            duration: 3000,
            panelClass: ["error-snackbar"],
          });
          this.submitting = false;
        },
      });
    }
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  getErrorMessage(controlName: string): string {
    const control = this.addressForm.get(controlName);
    if (control?.hasError("required")) {
      return "This field is required";
    }
    if (control?.hasError("pattern")) {
      return "Invalid ZIP code";
    }
    return "";
  }
}
