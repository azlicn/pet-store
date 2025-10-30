import { Component, Input, Output, EventEmitter } from "@angular/core";
import { CommonModule } from "@angular/common";
import { MatIconModule } from "@angular/material/icon";
import { MatCardModule } from "@angular/material/card";
import { MatButtonModule } from "@angular/material/button";

@Component({
  selector: "app-cart-overlay",
  templateUrl: "./cart-overlay.component.html",
  styleUrls: ["./cart-overlay.component.scss"],
  standalone: true,
  imports: [CommonModule, MatIconModule, MatCardModule, MatButtonModule],
})
export class CartOverlayComponent {
  @Input() cartItems: any[] = [];
  @Output() cartItemRemoved = new EventEmitter<any>();

  removeItem(item: any) {
    this.cartItemRemoved.emit(item);
  }

  viewCart() {
    this.cartItemRemoved.emit({ action: "viewCart" });
  }
}
