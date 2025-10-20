import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';

import { Pet } from '../../models/pet.model';

@Component({
  selector: 'app-latest-pet-card',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatCardModule,
    MatChipsModule,
    MatIconModule,
    MatButtonModule
  ],
  templateUrl: './latest-pet-card.component.html',
  styleUrl: './latest-pet-card.component.scss'
})
export class LatestPetCardComponent {
  @Input() pet!: Pet;
  @Output() imageError = new EventEmitter<Event>();
  @Output() viewDetails = new EventEmitter<Pet>();
  @Output() quickAction = new EventEmitter<Pet>();

  getPetImageUrl(pet: Pet): string | null {
    return pet.photoUrls && pet.photoUrls.length > 0 ? pet.photoUrls[0] : null;
  }

  onImageError(event: Event): void {
    this.imageError.emit(event);
  }

  onViewDetails(event: Event): void {
    event.stopPropagation();
    this.viewDetails.emit(this.pet);
  }

  onQuickAction(event: Event): void {
    event.stopPropagation();
    this.quickAction.emit(this.pet);
  }
}