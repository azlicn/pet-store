import { Component, OnInit } from "@angular/core";
import { CommonModule } from "@angular/common";
import { Router, RouterModule, ActivatedRoute } from "@angular/router";
import { MatCardModule } from "@angular/material/card";
import { MatButtonModule } from "@angular/material/button";
import { MatIconModule } from "@angular/material/icon";
import { AuthService } from "../../services/auth.service";

@Component({
  selector: "app-unauthorized",
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
  ],
  templateUrl: "./unauthorized.component.html",
  styleUrls: ["./unauthorized.component.scss"],
})
export class UnauthorizedComponent implements OnInit {
  errorType: "not-logged-in" | "forbidden" | "general" = "general";
  attemptedUrl: string | null = null;
  errorMessage = "You do not have permission to access this page.";

  constructor(
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe((params) => {
      this.errorType = params["type"] || "general";
      this.attemptedUrl = params["returnUrl"] || null;
      this.setErrorMessage();
    });
  }

  private setErrorMessage(): void {
    switch (this.errorType) {
      case "not-logged-in":
        this.errorMessage = "You need to log in to access this page.";
        break;
      case "forbidden":
        if (
          this.attemptedUrl &&
          this.attemptedUrl.includes("/users/") &&
          this.attemptedUrl.includes("/edit")
        ) {
          this.errorMessage =
            "You can only edit your own profile or you need admin permissions.";
        } else if (
          this.attemptedUrl &&
          this.attemptedUrl.includes("/pets/edit/")
        ) {
          this.errorMessage =
            "You can only edit pets that you created or you need admin permissions.";
        } else {
          this.errorMessage =
            "You do not have sufficient permissions to access this page.";
        }
        break;
      default:
        this.errorMessage = "You do not have permission to access this page.";
        break;
    }
  }

  onLogin(): void {
    const returnUrl = this.attemptedUrl || "/";
    this.router.navigate(["/login"], {
      queryParams: { returnUrl },
    });
  }

  onGoHome(): void {
    this.router.navigate(["/"]);
  }

  onGoBack(): void {
    window.history.back();
  }

  get isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }

  get showLoginButton(): boolean {
    return this.errorType === "not-logged-in" || !this.isLoggedIn;
  }

  get pageTitle(): string {
    switch (this.errorType) {
      case "not-logged-in":
        return "Login Required";
      case "forbidden":
        return "Access Forbidden";
      default:
        return "Unauthorized Access";
    }
  }

  get pageIcon(): string {
    switch (this.errorType) {
      case "not-logged-in":
        return "login";
      case "forbidden":
        return "block";
      default:
        return "security";
    }
  }
}
