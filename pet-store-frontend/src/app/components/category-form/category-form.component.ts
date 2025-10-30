import { Component, OnInit } from "@angular/core";
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
import { Category } from "../../models/category.model";
import { CategoryService } from "../../services/category.service";

@Component({
  selector: "app-category-form",
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
  ],
  templateUrl: "./category-form.component.html",
  styleUrls: ["./category-form.component.scss"],
})
export class CategoryFormComponent implements OnInit {
  categoryForm: FormGroup;
  isEditMode = false;
  categoryId: number | null = null;
  loading = false;
  submitting = false;

  constructor(
    private fb: FormBuilder,
    private categoryService: CategoryService,
    private router: Router,
    private route: ActivatedRoute,
    private snackBar: MatSnackBar
  ) {
    this.categoryForm = this.fb.group({
      name: [
        "",
        [
          Validators.required,
          Validators.minLength(2),
          Validators.maxLength(30),
        ],
      ],
    });
  }

  ngOnInit(): void {
    this.route.params.subscribe((params) => {
      if (params["id"]) {
        this.isEditMode = true;
        this.categoryId = +params["id"];
        this.loadCategory();
      }
    });
  }

  loadCategory(): void {
    if (this.categoryId) {
      this.loading = true;
      this.categoryService.getCategoryById(this.categoryId).subscribe({
        next: (category) => {
          this.categoryForm.patchValue({
            name: category.name,
          });
          this.loading = false;
        },
        error: (error) => {
          console.error("Error loading category:", error);
          this.snackBar.open("Error loading category", "Close", {
            duration: 3000,
            panelClass: ["error-snackbar"],
          });
          this.loading = false;
          this.router.navigate(["/categories"]);
        },
      });
    }
  }

  onSubmit(): void {
    if (this.categoryForm.valid) {
      this.submitting = true;
      const categoryData: Category = {
        name: this.categoryForm.value.name.trim(),
      };

      if (this.isEditMode && this.categoryId) {
        this.categoryService
          .updateCategory(this.categoryId, categoryData)
          .subscribe({
            next: (updatedCategory) => {
              this.snackBar.open("Category updated successfully!", "Close", {
                duration: 3000,
                panelClass: ["success-snackbar"],
              });
              this.router.navigate(["/categories"]);
            },
            error: (error) => {
              console.error("Error updating category:", error);
              this.snackBar.open("Error updating category", "Close", {
                duration: 3000,
                panelClass: ["error-snackbar"],
              });
              this.submitting = false;
            },
          });
      } else {
        this.categoryService.createCategory(categoryData).subscribe({
          next: (newCategory) => {
            this.snackBar.open("Category created successfully!", "Close", {
              duration: 3000,
              panelClass: ["success-snackbar"],
            });
            this.router.navigate(["/categories"]);
          },
          error: (error) => {
            console.error("Error creating category:", error);
            this.snackBar.open("Error creating category", "Close", {
              duration: 3000,
              panelClass: ["error-snackbar"],
            });
            this.submitting = false;
          },
        });
      }
    } else {
      this.markFormGroupTouched();
    }
  }

  onCancel(): void {
    this.router.navigate(["/categories"]);
  }

  private markFormGroupTouched(): void {
    Object.keys(this.categoryForm.controls).forEach((key) => {
      const control = this.categoryForm.get(key);
      control?.markAsTouched();
    });
  }

  get name() {
    return this.categoryForm.get("name");
  }

  getErrorMessage(controlName: string): string {
    const control = this.categoryForm.get(controlName);
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
