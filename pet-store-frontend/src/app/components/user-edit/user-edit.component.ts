import { Component, OnInit } from "@angular/core";
import { CommonModule } from "@angular/common";
import {
  ReactiveFormsModule,
  FormBuilder,
  FormGroup,
  Validators,
  AbstractControl,
} from "@angular/forms";
import { Router, ActivatedRoute } from "@angular/router";
import { MatCardModule } from "@angular/material/card";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { MatButtonModule } from "@angular/material/button";
import { MatIconModule } from "@angular/material/icon";
import { MatProgressSpinnerModule } from "@angular/material/progress-spinner";
import { MatSelectModule } from "@angular/material/select";

import {
  UserService,
  User,
  UserUpdateRequest,
} from "../../services/user.service";
import { AuthService } from "../../services/auth.service";
import { MatSnackBar } from "@angular/material/snack-bar";

@Component({
  selector: "app-user-edit",
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatSelectModule,
  ],
  templateUrl: "./user-edit.component.html",
  styleUrl: "./user-edit.component.scss",
})
export class UserEditComponent implements OnInit {
  userForm!: FormGroup;
  loading = false;
  isEditingOwnProfile = false;
  currentUser: any;
  userId!: number;
  hideCurrentPassword = true;
  hideNewPassword = true;
  hideConfirmPassword = true;

  constructor(
    private userService: UserService,
    public authService: AuthService,
    private router: Router,
    private route: ActivatedRoute,
    private fb: FormBuilder,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.currentUser = this.authService.getCurrentUser();
    this.userId = Number(this.route.snapshot.paramMap.get("id"));
    this.isEditingOwnProfile = this.currentUser?.id === this.userId;

    if (!this.authService.isAdmin() && !this.isEditingOwnProfile) {
      this.router.navigate(["/"]);
      return;
    }

    this.initializeForm();
    this.loadUserData();
  }

  initializeForm(): void {
    this.userForm = this.fb.group(
      {
        firstName: ["", [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
        lastName: ["", [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
        email: ["", [Validators.required, Validators.email, Validators.maxLength(150)]],
        phoneNumber: ["", [Validators.maxLength(20)]],
        currentPassword: [""],
        newPassword: [""],
        confirmPassword: [""],
        roles: [{ value: [], disabled: !this.authService.isAdmin() }],
      },
      {
        validators: this.passwordMatchValidator,
      }
    );

    this.userForm.get("newPassword")?.valueChanges.subscribe((value) => {
      const currentPasswordControl = this.userForm.get("currentPassword");
      const confirmPasswordControl = this.userForm.get("confirmPassword");

      if (value) {
        currentPasswordControl?.setValidators([Validators.required]);
        confirmPasswordControl?.setValidators([Validators.required]);
        this.userForm
          .get("newPassword")
          ?.setValidators([Validators.required, Validators.minLength(6)]);
      } else {
        currentPasswordControl?.clearValidators();
        confirmPasswordControl?.clearValidators();
        this.userForm.get("newPassword")?.clearValidators();
      }

      currentPasswordControl?.updateValueAndValidity();
      confirmPasswordControl?.updateValueAndValidity();
      this.userForm.get("newPassword")?.updateValueAndValidity();
    });
  }

  passwordMatchValidator(
    control: AbstractControl
  ): { [key: string]: any } | null {
    const newPassword = control.get("newPassword");
    const confirmPassword = control.get("confirmPassword");

    if (
      newPassword &&
      confirmPassword &&
      newPassword.value !== confirmPassword.value
    ) {
      return { passwordMismatch: true };
    }

    return null;
  }

  loadUserData(): void {
    this.loading = true;
    this.userService.getUserById(this.userId).subscribe({
      next: (user: User) => {
        this.userForm.patchValue({
          firstName: user.firstName,
          lastName: user.lastName,
          email: user.email,
          phoneNumber: user.phoneNumber,
          roles: user.roles,
        });
        this.loading = false;
      },
      error: (error) => {
        console.error("Error loading user:", error);
        this.snackBar.open("Error loading user", "Close", { duration: 3000 });
        this.router.navigate(["/"]);
        this.loading = false;
      },
    });
  }

  onSubmit(): void {
    if (this.userForm.valid) {
      this.loading = true;
      const formValue = this.userForm.value;

      const updateRequest: UserUpdateRequest = {
        firstName: formValue.firstName,
        lastName: formValue.lastName,
        email: formValue.email,
        phoneNumber: formValue.phoneNumber,
      };

      if (formValue.newPassword) {
        updateRequest.password = formValue.newPassword;
      }

      if (this.authService.isAdmin() && formValue.roles) {
        updateRequest.roles = formValue.roles;
      }

      this.userService.updateUser(this.userId, updateRequest).subscribe({
        next: (response: any) => {
          this.snackBar.open("Profile updated successfully!", "Close", { duration: 3000 });

          if (this.isEditingOwnProfile) {
            const updatedUser = response.user;
            this.authService.updateCurrentUser(updatedUser);
          }

          // Navigate to appropriate page based on user role
          if (this.authService.isAdmin()) {
            this.router.navigate(["/users"]);
          } else {
            this.router.navigate(["/pets"]);
          }
          this.loading = false;
        },
        error: (error: any) => {
          console.error("Error updating user:", error);
          this.snackBar.open(error.error?.message || "Failed to update profile", "Close", { duration: 3000 });
          this.loading = false;
        },
      });
    } else {
      this.markFormGroupTouched();
    }
  }

  onCancel(): void {
    this.router.navigate(["/pets"]);
  }

  togglePasswordVisibility(field: string): void {
    switch (field) {
      case "current":
        this.hideCurrentPassword = !this.hideCurrentPassword;
        break;
      case "new":
        this.hideNewPassword = !this.hideNewPassword;
        break;
      case "confirm":
        this.hideConfirmPassword = !this.hideConfirmPassword;
        break;
    }
  }

  private markFormGroupTouched(): void {
    Object.keys(this.userForm.controls).forEach((key) => {
      this.userForm.get(key)?.markAsTouched();
    });
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.userForm.get(fieldName);
    return !!(field?.invalid && field?.touched);
  }

  getFieldError(fieldName: string): string {
    const field = this.userForm.get(fieldName);

    if (field?.hasError("required")) {
      return `${this.getFieldDisplayName(fieldName)} is required`;
    }

    if (field?.hasError("email")) {
      return "Please enter a valid email address";
    }

    if (field?.hasError("minlength")) {
      const requiredLength = field.errors?.["minlength"].requiredLength;
      return `${this.getFieldDisplayName(
        fieldName
      )} must be at least ${requiredLength} characters`;
    }

    if (field?.hasError("maxlength")) {
      const requiredLength = field.errors?.["maxlength"].requiredLength;
      return `${this.getFieldDisplayName(
        fieldName
      )} cannot exceed ${requiredLength} characters`;
    }

    if (
      fieldName === "confirmPassword" &&
      this.userForm.hasError("passwordMismatch")
    ) {
      return "Passwords do not match";
    }

    return "";
  }

  private getFieldDisplayName(fieldName: string): string {
    const displayNames: { [key: string]: string } = {
      firstName: "First name",
      lastName: "Last name",
      email: "Email",
      phoneNumber: "Phone number",
      currentPassword: "Current password",
      newPassword: "New password",
      confirmPassword: "Confirm password",
    };

    return displayNames[fieldName] || fieldName;
  }
}
