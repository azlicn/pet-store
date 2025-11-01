import { Component, OnInit } from "@angular/core";
import { CommonModule } from "@angular/common";
import {
  FormBuilder,
  FormGroup,
  Validators,
  ReactiveFormsModule,
} from "@angular/forms";
import { Router, RouterModule } from "@angular/router";
import { MatCardModule } from "@angular/material/card";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { MatButtonModule } from "@angular/material/button";
import { MatIconModule } from "@angular/material/icon";
import { MatSnackBar, MatSnackBarModule } from "@angular/material/snack-bar";
import { AuthService } from "../../services/auth.service";

@Component({
  selector: "app-register",
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatSnackBarModule,
  ],
  templateUrl: "./register.component.html",
  styleUrls: ["./register.component.scss"],
})
export class RegisterComponent implements OnInit {
  registerForm!: FormGroup;
  isLoading = false;
  hidePassword = true;
  hideConfirmPassword = true;

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.initializeForm();
  }

  private initializeForm(): void {
    this.registerForm = this.formBuilder.group(
      {
        firstName: ["", [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
        lastName: ["", [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
        email: ["", [Validators.required, Validators.email, Validators.maxLength(150)]],
        password: ["", [Validators.required, Validators.minLength(6)]],
        confirmPassword: ["", [Validators.required]],
      },
      { validators: this.passwordMatchValidator }
    );
  }

  private passwordMatchValidator(group: FormGroup): any {
    const password = group.get("password")?.value;
    const confirmPassword = group.get("confirmPassword")?.value;

    if (password !== confirmPassword) {
      group.get("confirmPassword")?.setErrors({ passwordMismatch: true });
      return { passwordMismatch: true };
    } else {
      group.get("confirmPassword")?.setErrors(null);
      return null;
    }
  }

  onSubmit(): void {
    if (this.registerForm.valid) {
      this.isLoading = true;

      const registerData = {
        firstName: this.registerForm.value.firstName,
        lastName: this.registerForm.value.lastName,
        email: this.registerForm.value.email,
        password: this.registerForm.value.password,
      };

      this.authService.register(registerData).subscribe({
        next: (response) => {
          this.snackBar.open(
            "Registration successful! Please login with your credentials.",
            "Close",
            {
              duration: 5000,
              panelClass: ["success-snackbar"],
            }
          );
          this.router.navigate(["/login"]);
        },
        error: (error) => {
          this.isLoading = false;
          let errorMessage = "Registration failed. Please try again.";

          if (error.error?.message) {
            errorMessage = error.error.message;
          } else if (error.status === 409) {
            errorMessage =
              "Email already exists. Please use a different email address.";
          } else if (error.status === 400) {
            errorMessage =
              "Invalid registration data. Please check your information.";
          }

          this.snackBar.open(errorMessage, "Close", {
            duration: 5000,
            panelClass: ["error-snackbar"],
          });
          console.error("Registration error:", error);
        },
      });
    } else {
      this.markFormGroupTouched();
    }
  }

  private markFormGroupTouched(): void {
    Object.keys(this.registerForm.controls).forEach((key) => {
      const control = this.registerForm.get(key);
      control?.markAsTouched();
    });
  }

  togglePasswordVisibility(): void {
    this.hidePassword = !this.hidePassword;
  }

  toggleConfirmPasswordVisibility(): void {
    this.hideConfirmPassword = !this.hideConfirmPassword;
  }

  getFieldError(fieldName: string): string {
    const field = this.registerForm.get(fieldName);
    if (field?.hasError("required")) {
      return `${this.getFieldDisplayName(fieldName)} is required`;
    }
    if (field?.hasError("email")) {
      return "Please enter a valid email address";
    }
    if (field?.hasError("minlength")) {
      const minLength = field.getError("minlength").requiredLength;
      return `${this.getFieldDisplayName(
        fieldName
      )} must be at least ${minLength} characters`;
    }
    if (field?.hasError("maxlength")) {
      const maxLength = field.getError("maxlength").requiredLength;
      return `${this.getFieldDisplayName(
        fieldName
      )} cannot exceed ${maxLength} characters`;
    }
    if (field?.hasError("passwordMismatch")) {
      return "Passwords do not match";
    }
    return "";
  }

  private getFieldDisplayName(fieldName: string): string {
    const displayNames: { [key: string]: string } = {
      firstName: "First name",
      lastName: "Last name",
      email: "Email",
      password: "Password",
      confirmPassword: "Confirm password",
    };
    return displayNames[fieldName] || fieldName;
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.registerForm.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched));
  }
}
