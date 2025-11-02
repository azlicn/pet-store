import { Component, OnInit } from "@angular/core";
import {
  FormBuilder,
  FormGroup,
  Validators,
  ReactiveFormsModule,
} from "@angular/forms";
import { ActivatedRoute, Router } from "@angular/router";
import { DiscountService } from "../../services/discount.service";
import { Discount } from "../../models/discount.model";
import { MatSnackBar, MatSnackBarModule } from "@angular/material/snack-bar";
import { CommonModule } from "@angular/common";
import { MatButtonModule } from "@angular/material/button";
import { MatCardModule } from "@angular/material/card";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatIconModule } from "@angular/material/icon";
import { MatInputModule } from "@angular/material/input";
import { MatCheckboxModule } from "@angular/material/checkbox";
import { MatProgressSpinnerModule } from "@angular/material/progress-spinner";
import { MatDatepickerModule } from "@angular/material/datepicker";
import { MatNativeDateModule } from "@angular/material/core";

@Component({
  selector: "app-discount-form",
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatSnackBarModule,
    MatProgressSpinnerModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatButtonModule,
    MatIconModule,
    MatDatepickerModule,
    MatCheckboxModule,
  ],
  templateUrl: "./discount-form.component.html",
  styleUrls: ["./discount-form.component.scss"],
})
export class DiscountFormComponent implements OnInit {
  discountForm: FormGroup;
  isEditMode = false;
  discountId: number | null = null;
  loading = false;
  submitting = false;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private discountService: DiscountService,
    private snackBar: MatSnackBar
  ) {
    this.discountForm = this.fb.group(
      {
        code: ["", Validators.required],
        description: [""],
        percentage: [
          0,
          [Validators.required, Validators.min(1), Validators.max(100)],
        ],
        validFrom: ["", Validators.required],
        validTo: ["", Validators.required],
        active: [true],
      },
      { validators: this.dateRangeValidator }
    );
  }

  ngOnInit(): void {
    this.route.params.subscribe((params) => {
      if (params["id"]) {
        this.isEditMode = true;
        this.discountId = +params["id"];
        console.log("Editing discount with ID:", this.discountId);
        this.loadDiscount();
      }
    });
  }

  loadDiscount(): void {
    if (this.discountId) {
      this.loading = true;
      this.discountService.getDiscountById(this.discountId).subscribe({
        next: (discount) => {
          this.discountForm.patchValue(discount);
          this.loading = false;
        },
        error: (error) => {
          console.error("Error loading discount:", error);
          this.snackBar.open("Error loading discount", "Close", {
            duration: 3000,
            panelClass: ["error-snackbar"],
          });
          this.loading = false;
          this.router.navigate(["/discounts"]);
        },
      });
    }
  }

  onSubmit(): void {
    if (this.discountForm.valid) {
      this.submitting = true;
      const formatDate = (date: any) => {
        if (!date) return null;
        if (
          typeof date === "string" &&
          date.match(/^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}$/)
        ) {
          return date;
        }
        const d = new Date(date);
        return (
          d.getFullYear() +
          "-" +
          String(d.getMonth() + 1).padStart(2, "0") +
          "-" +
          String(d.getDate()).padStart(2, "0") +
          "T" +
          String(d.getHours()).padStart(2, "0") +
          ":" +
          String(d.getMinutes()).padStart(2, "0") +
          ":" +
          String(d.getSeconds()).padStart(2, "0")
        );
      };
      const discountData: Discount = {
        code: this.discountForm.value.code.trim(),
        description: this.discountForm.value.description.trim(),
        percentage: this.discountForm.value.percentage,
        validFrom: formatDate(this.discountForm.value.validFrom) ?? "",
        validTo: formatDate(this.discountForm.value.validTo) ?? "",
        active: this.discountForm.value.active,
      };
      if (this.isEditMode && this.discountId) {
        this.discountService
          .updateDiscount(this.discountId, discountData)
          .subscribe({
            next: () => {
              this.snackBar.open("Discount updated successfully!", "Close", {
                duration: 3000,
              });
              this.router.navigate(["/discounts"]);
            },
            error: (error) => {
              console.error("Error updating discount:", error);
              this.snackBar.open(
                error?.error?.message || "Error updating discount",
                "Close",
                {
                  duration: 3000,
                  panelClass: ["error-snackbar"],
                }
              );
              this.submitting = false;
            },
          });
      } else {
        this.discountService.createDiscount(discountData).subscribe({
          next: () => {
            this.snackBar.open("Discount created successfully!", "Close", {
              duration: 3000,
            });
            this.router.navigate(["/discounts"]);
          },
          error: (error) => {
            console.error("Error creating discount:", error);
            this.snackBar.open(
              error?.error?.message || "Error creating discount",
              "Close",
              {
                duration: 3000,
                panelClass: ["error-snackbar"],
              }
            );
            this.submitting = false;
          },
        });
      }
    } else {
      this.markFormGroupTouched();
    }
  }

  dateRangeValidator(group: FormGroup): any {
    const validFrom = group.get("validFrom")?.value;
    const validTo = group.get("validTo")?.value;
    if (validFrom && validTo && new Date(validFrom) > new Date(validTo)) {
      return { dateRange: "Valid From must not be after Valid To" };
    }
    return null;
  }

  onCancel(): void {
    this.router.navigate(["/discounts"]);
  }

  onCodeInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.code?.setValue(input.value.toUpperCase());
  }

  private markFormGroupTouched(): void {
    Object.keys(this.discountForm.controls).forEach((key) => {
      const control = this.discountForm.get(key);
      control?.markAsTouched();
    });
  }

  get code() {
    return this.discountForm.get("code");
  }

  get description() {
    return this.discountForm.get("description");
  }

  getErrorMessage(controlName: string): string {
    const control = this.discountForm.get(controlName);
    if (control?.hasError("required")) {
      return `${
        controlName.charAt(0).toUpperCase() + controlName.slice(1)
      } is required`;
    }
    if (control?.hasError("minlength")) {
      return `${
        controlName.charAt(0).toUpperCase() + controlName.slice(1)
      } must be at least ${
        control.errors?.["minlength"].requiredLength
      } characters`;
    }
    if (control?.hasError("maxlength")) {
      return `${
        controlName.charAt(0).toUpperCase() + controlName.slice(1)
      } must not exceed ${
        control.errors?.["maxlength"].requiredLength
      } characters`;
    }
    return "";
  }
}
