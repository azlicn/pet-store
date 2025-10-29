import { Component, Inject } from "@angular/core";
import { CommonModule } from "@angular/common";
import {
  MAT_DIALOG_DATA,
  MatDialogRef,
  MatDialogModule,
} from "@angular/material/dialog";
import { MatButtonModule } from "@angular/material/button";
import { MatIconModule } from "@angular/material/icon";
import { Pet, PetStatus } from "../../models/pet.model";
import { MatChipsModule } from "@angular/material/chips";

export interface ImageModalData {
  pet: Pet;
  initialImageIndex?: number;
}

@Component({
  selector: "app-image-modal",
  standalone: true,
  imports: [
    CommonModule,
    MatDialogModule,
    MatButtonModule,
    MatIconModule,
    MatChipsModule,
  ],
  templateUrl: "./image-modal.component.html",
  styleUrl: "./image-modal.component.scss",
})
export class ImageModalComponent {
  currentImageIndex = 0;

  descriptionWidth: number = 0;

  constructor(
    public dialogRef: MatDialogRef<ImageModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ImageModalData
  ) {
    this.currentImageIndex = data.initialImageIndex || 0;
  }

  get currentImage(): string {
    return this.data.pet.photoUrls?.[this.currentImageIndex] || "";
  }

  get hasMultipleImages(): boolean {
    return (this.data.pet.photoUrls?.length || 0) > 1;
  }

  get canGoPrevious(): boolean {
    return this.currentImageIndex > 0;
  }

  get canGoNext(): boolean {
    return this.currentImageIndex < (this.data.pet.photoUrls?.length || 0) - 1;
  }

  updateDescriptionWidth(img: HTMLImageElement) {
    if (img && img.clientWidth) {
      this.descriptionWidth = img.clientWidth - 50;
    }
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

  previousImage(): void {
    if (this.canGoPrevious) {
      this.currentImageIndex--;
    }
  }

  nextImage(): void {
    if (this.canGoNext) {
      this.currentImageIndex++;
    }
  }

  closeModal(): void {
    this.dialogRef.close();
  }
}
