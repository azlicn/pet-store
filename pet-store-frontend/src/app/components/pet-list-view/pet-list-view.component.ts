import { Component, Input, Output, EventEmitter } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";
import { MatTableModule } from "@angular/material/table";
import { MatButtonModule } from "@angular/material/button";
import { MatIconModule } from "@angular/material/icon";
import { MatChipsModule } from "@angular/material/chips";
import { MatTooltipModule } from "@angular/material/tooltip";
import { MatDialog } from "@angular/material/dialog";
import { Pet } from "../../models/pet.model";
import { AuthService } from "../../services/auth.service";
import {
  ImageModalComponent,
  ImageModalData,
} from "../image-modal/image-modal.component";

@Component({
  selector: "app-pet-list-view",
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatChipsModule,
    MatTooltipModule,
  ],
  templateUrl: "./pet-list-view.component.html",
  styleUrls: ["./pet-list-view.component.scss"],
})
export class PetListViewComponent {
  @Input() pets: Pet[] = [];
  @Output() statusUpdate = new EventEmitter<Pet>();
  @Output() petDelete = new EventEmitter<Pet>();
  @Output() petAddToCart = new EventEmitter<Pet>();

  displayedColumns: string[] = [
    "photo",
    "name",
    "category",
    "status",
    "price",
    "actions",
  ];

  constructor(public authService: AuthService, private dialog: MatDialog) {}

  getStatusColor(status: string): string {
    switch (status) {
      case "AVAILABLE":
        return "primary";
      case "PENDING":
        return "accent";
      case "SOLD":
        return "warn";
      default:
        return "";
    }
  }

  getStatusClass(status: string): string {
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

  onStatusUpdate(pet: Pet): void {
    this.statusUpdate.emit(pet);
  }

  onDelete(pet: Pet): void {
    this.petDelete.emit(pet);
  }

  onImageClick(pet: Pet): void {
    if (pet.photoUrls && pet.photoUrls.length > 0) {
      const dialogData: ImageModalData = {
        pet: pet,
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

  canEditPet(pet: Pet): boolean {
    if (this.authService.isAdmin()) {
      return true;
    }

    const currentUser = this.authService.getCurrentUser();
    return currentUser ? pet.createdBy === currentUser.id : false;
  }

  isOwnPet(pet: Pet): boolean {
    const currentUser = this.authService.getCurrentUser();
    return currentUser ? pet.createdBy === currentUser.id : false;
  }

  isCreatedByMe(pet: Pet): boolean {
    const currentUser = this.authService.getCurrentUser();
    return currentUser ? pet.createdBy === currentUser.id : false;
  }

  isOwnedByMe(pet: Pet): boolean {
    const currentUser = this.authService.getCurrentUser();
    return currentUser && pet.owner ? pet.owner.id === currentUser.id : false;
  }

   onAddToCart(pet: Pet): void{
    this.petAddToCart.emit(pet);
  }

  getPetRelationshipIcon(pet: Pet): string {
    if (this.isCreatedByMe(pet) && this.isOwnedByMe(pet)) {
      return "favorite";
    } else if (this.isCreatedByMe(pet)) {
      return "sell";
    } else if (this.isOwnedByMe(pet)) {
      return "shopping_bag";
    }
    return "";
  }

  getPetRelationshipTooltip(pet: Pet): string {
    if (this.isCreatedByMe(pet) && this.isOwnedByMe(pet)) {
      return "Listed for sale by you and owned by you";
    } else if (this.isCreatedByMe(pet)) {
      return "Listed for sale by you";
    } else if (this.isOwnedByMe(pet)) {
      return "Purchased by you";
    }
    return "";
  }
}
