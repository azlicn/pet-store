import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatChipsModule } from '@angular/material/chips';
import { PetService } from '../../services/pet.service';
import { Pet } from '../../models/pet.model';
import { LatestPetCardComponent } from '../latest-pet-card/latest-pet-card.component';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatGridListModule,
    MatChipsModule,
    LatestPetCardComponent
  ],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
  latestPets: Pet[] = [];
  loading = true;
  LIMIT_LATEST_PETS = 6;

  constructor(private petService: PetService) {}

  ngOnInit(): void {
    this.loadLatestPets();
  }

  loadLatestPets(): void {
    this.petService.getAllPets(this.LIMIT_LATEST_PETS).subscribe({
      next: (pets) => {
        this.latestPets = pets;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading latest pets:', error);
        this.loading = false;
      }
    });
  }

  getPetImageUrl(pet: Pet): string | null {
    if (pet.photoUrls && pet.photoUrls.length > 0) {
      return pet.photoUrls[0];
    }
    return null;
  }

  onImageError(event: any): void {
    event.target.src = 'assets/images/pet-placeholder-default.jpg';
  }

  trackByPetId(index: number, pet: Pet): number {
    return pet.id || index;
  }
}