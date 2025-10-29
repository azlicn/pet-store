import { Component, Input, Output, EventEmitter } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";
import { MatButtonModule } from "@angular/material/button";
import { MatIconModule } from "@angular/material/icon";
import { MatCardModule } from "@angular/material/card";
import { MatChipsModule } from "@angular/material/chips";
import { MatTooltipModule } from "@angular/material/tooltip";
import { MatDialog } from "@angular/material/dialog";
import { Pet, PetStatus } from "../../models/pet.model";
import { AuthService } from "../../services/auth.service";
import {
  ImageModalComponent,
  ImageModalData,
} from "../image-modal/image-modal.component";

@Component({
  selector: "app-pet-card",
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    MatChipsModule,
    MatTooltipModule,
  ],
  templateUrl: "./pet-card.component.html",
  styleUrls: ["./pet-card.component.scss"],
})
export class PetCardComponent {
  @Input({ required: true }) pet!: Pet;

  // Event emitters for parent component to handle actions
  @Output() statusUpdate = new EventEmitter<Pet>();
  @Output() petDelete = new EventEmitter<Pet>();
  @Output() petPurchase = new EventEmitter<Pet>();
  @Output() petAddToCart = new EventEmitter<Pet>();

  constructor(public authService: AuthService, private dialog: MatDialog) {}

  canEditPet(): boolean {
    if (this.authService.isAdmin()) {
      return true;
    }

    const currentUser = this.authService.getCurrentUser();
    return currentUser ? this.pet.createdBy === currentUser.id : false;
  }

  canDeletePet(): boolean {
    return this.authService.isAdmin() || this.isCreatedByMe();
  }

  isOwnPet(): boolean {
    return this.isCreatedByMe();
  }

  isCreatedByMe(): boolean {
    const currentUser = this.authService.getCurrentUser();
    return currentUser ? this.pet.createdBy === currentUser?.id : false;
  }

  isOwnedByMe(): boolean {
    const currentUser = this.authService.getCurrentUser();
    const ownedByMe = (currentUser && this.pet.owner)
      ? this.pet.owner.id === currentUser.id
      : false;
    return ownedByMe;
  }

  getPetRelationshipIcon(): string {
    if (this.isCreatedByMe() && this.isOwnedByMe()) {
      return "favorite";
    } else if (this.isCreatedByMe()) {
      return "person";
    } else if (this.isOwnedByMe()) {
      return "shopping_bag";
    }
    return "";
  }

  getPetRelationshipTooltip(): string {
    if (this.isCreatedByMe() && this.isOwnedByMe()) {
      return "Created by you and owned by you";
    } else if (this.isCreatedByMe()) {
      return "Created by you";
    } else if (this.isOwnedByMe()) {
      return "Purchased by you";
    }
    return "";
  }

  onImageClick(): void {
    if (this.pet.photoUrls && this.pet.photoUrls.length > 0) {
      const dialogData: ImageModalData = {
        pet: this.pet,
        initialImageIndex: 0,
      };

      this.dialog.open(ImageModalComponent, {
        data: dialogData,
        maxWidth: "95vw",
        maxHeight: "95vh",
        width: "90vw",
        height: "85vh",
        panelClass: "image-modal-panel",
        hasBackdrop: true,
        disableClose: false,
      });
    }
  }

  onUpdateStatus(): void {
    this.statusUpdate.emit(this.pet);
  }

  onDeletePet(): void {
    this.petDelete.emit(this.pet);
  }

  onPurchasePet(): void {
    this.petPurchase.emit(this.pet);
  }

  onAddToCart() {
    this.petAddToCart.emit(this.pet);
  }

  getStatusClass(status: PetStatus): string {
    switch (status) {
      case "AVAILABLE":
        return "status-available";
      case "PENDING":
        return "status-pending";
      case "SOLD":
        return "status-sold";
      default:
        return "status-unknown";
    }
  }
}
