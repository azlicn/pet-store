import { Component } from "@angular/core";
import { CommonModule } from "@angular/common";
import { Router, RouterModule } from "@angular/router";
import { MatToolbarModule } from "@angular/material/toolbar";
import { MatButtonModule } from "@angular/material/button";
import { MatIconModule } from "@angular/material/icon";
import { MatTooltipModule } from "@angular/material/tooltip";
import { MatMenuModule } from "@angular/material/menu";
import { MatDividerModule } from "@angular/material/divider";
import { MatDialog } from "@angular/material/dialog";
import { CartComponent } from "../cart/cart.component";
import { CartOverlayComponent } from "../cart-overlay/cart-overlay.component";

import { AuthService, User } from "../../services/auth.service";
import { StoreService } from "../../services/store.service";
import { Observable } from "rxjs";
import { Overlay, OverlayRef } from "@angular/cdk/overlay";
import {
  ViewContainerRef,
  TemplateRef,
  ViewChild,
  ElementRef,
} from "@angular/core";
import { TemplatePortal } from "@angular/cdk/portal";

@Component({
  selector: "app-header",
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatTooltipModule,
    MatMenuModule,
    MatDividerModule,
    CartOverlayComponent,
  ],
  templateUrl: "./header.component.html",
  styleUrl: "./header.component.scss",
})
export class HeaderComponent {
  @ViewChild("cartIcon", { static: false }) cartIcon!: ElementRef;

  currentUser$: Observable<User | null> = this.authService.currentUser$;
  cartItemCount$ = this.storeService.cartItemCount$;
  overlayRef: OverlayRef | null = null;
  cartItems: any[] = [];

  private overlayMouseInside = false;
  private cartIconMouseInside = false;

  constructor(
    private authService: AuthService,
    private storeService: StoreService,
    private router: Router,
    private dialog: MatDialog,
    private overlay: Overlay,
    private vcr: ViewContainerRef
  ) {
    // Update cart count and subscribe to cart changes reactively
    this.currentUser$.subscribe((user) => {
      if (user?.id) {
        // Only call getCart once - it updates both cart data and count
        this.storeService.getCart(user.id).subscribe();
        this.storeService.cart$.subscribe((cart) => {
          this.cartItems = cart?.items || [];
        });
      } else {
        this.cartItems = [];
      }
    });
  }

  onLogout(): void {
    this.authService.logout();
    this.router.navigate(["/"]);
  }

  navigateToLogin(): void {
    this.router.navigate(["/login"]);
  }

  isAdmin(): boolean {
    return this.authService.isAdmin();
  }

  onCartOverlayAction(event: any) {
    if (event?.action === "viewCart") {
      this.openCartDialog();
      this.onCartLeave();
    }
  }

  openCartDialog(): void {
    const dialogRef = this.dialog.open(CartComponent, {
      width: "500px",
      height: "100vh",
      position: { right: "0" },
      panelClass: "cart-dialog-panel",
      autoFocus: false,
    });

    dialogRef.beforeClosed().subscribe(() => {
      const panel = document.querySelector(".cart-dialog-panel");
      if (panel) {
        panel.classList.add("slideOutRight");
      }
    });
  }

  onCartHover(cartIcon: HTMLElement, cartOverlayTpl: TemplateRef<any>): void {
    if (this.overlayRef) return;
    const positionStrategy = this.overlay
      .position()
      .flexibleConnectedTo(cartIcon)
      .withPositions([
        {
          originX: "end",
          originY: "bottom",
          overlayX: "end",
          overlayY: "top",
          offsetY: 8,
        },
      ]);
    this.overlayRef = this.overlay.create({
      positionStrategy,
      hasBackdrop: false,
    });
    this.overlayRef.attach(new TemplatePortal(cartOverlayTpl, this.vcr));
    this.overlayMouseInside = false;
    this.cartIconMouseInside = true;
  }
  onCartIconMouseEnter(): void {
    this.cartIconMouseInside = true;
  }

  onCartIconMouseLeave(): void {
    this.cartIconMouseInside = false;
    setTimeout(() => {
      if (!this.overlayMouseInside && !this.cartIconMouseInside) {
        this.onCartLeave();
      }
    }, 50);
  }

  onOverlayMouseEnter(): void {
    this.overlayMouseInside = true;
  }

  onCartLeave(): void {
    if (this.overlayRef) {
      this.overlayRef.detach();
      this.overlayRef.dispose();
      this.overlayRef = null;
    }
    this.overlayMouseInside = false;
    this.cartIconMouseInside = false;
  }
}
