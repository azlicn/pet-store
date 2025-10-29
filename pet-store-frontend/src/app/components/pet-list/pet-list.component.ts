import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatSelectModule } from '@angular/material/select';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { Pet, PetStatus } from '../../models/pet.model';
import { Category } from '../../models/category.model';
import { PetService } from '../../services/pet.service';
import { CategoryService } from '../../services/category.service';
import { AuthService } from '../../services/auth.service';
import { PetCardComponent } from '../pet-card/pet-card.component';
import { PetListViewComponent } from '../pet-list-view/pet-list-view.component';
import { ConfirmDialogComponent, ConfirmDialogData } from '../confirm-dialog/confirm-dialog.component';
import { StoreService } from 'src/app/services/store.service';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-pet-list',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    FormsModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    MatChipsModule,
    MatSelectModule,
    MatInputModule,
    MatFormFieldModule,
    MatSnackBarModule,
    MatDialogModule,
    PetCardComponent,
    PetListViewComponent
  ],
  templateUrl: './pet-list.component.html',
  styleUrls: ['./pet-list.component.scss']
})
export class PetListComponent implements OnInit {
  pets: Pet[] = [];
  allPets: Pet[] = []; 
  allCategories: Category[] = [];
  showFilters: boolean = true;
  viewMode: 'card' | 'list' = 'card';
  showMyPetsOnly: boolean = false;
  searchFilters = {
    name: '',
    categoryId: '',
    status: ''
  };

  userId = this.authService.getCurrentUser()?.id;

  constructor(
    private petService: PetService,
    private categoryService: CategoryService,
    private storeService: StoreService,
    private snackBar: MatSnackBar,
    private dialog: MatDialog,
    public authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadPets();
    this.loadCategories();
  }

  loadPets(): void {
    this.petService.getAllPets().subscribe({
      next: (pets) => {
        this.allPets = pets;
        this.pets = [...pets];
        this.applyFilters();
      },
      error: (error) => {
        this.snackBar.open('Error loading pets', 'Close', { duration: 3000 });
        console.error('Error loading pets:', error);
      }
    });
  }

  loadCategories(): void {
    this.categoryService.getAllCategories().subscribe({
      next: (categories) => {
        this.allCategories = categories;
      },
      error: (error) => {
        console.error('Error loading categories:', error);
      }
    });
  }

  onFilterChange(): void {
    this.applyFilters();
  }

  applyFilters(): void {
    if (this.showMyPetsOnly) {
      if (!this.authService.isLoggedIn()) {
        this.snackBar.open('Please log in to view your pets', 'Close', { duration: 3000 });
        this.showMyPetsOnly = false; 
        return;
      }
      
      this.petService.getMyPets().subscribe({
        next: (pets) => {
          this.pets = pets;
        },
        error: (error) => {
          this.snackBar.open('Error loading your pets', 'Close', { duration: 3000 });
          console.error('Error loading user pets:', error);
          this.pets = [];
        }
      });
      return;
    }

    const hasFilters = this.searchFilters.name.trim() || 
                      this.searchFilters.categoryId || 
                      this.searchFilters.status;

    if (hasFilters) {
      const filters: { name?: string; categoryId?: number; status?: PetStatus } = {};
      
      if (this.searchFilters.name.trim()) {
        filters.name = this.searchFilters.name.trim();
      }
      
      if (this.searchFilters.categoryId) {
        filters.categoryId = +this.searchFilters.categoryId;
      }
      
      if (this.searchFilters.status) {
        filters.status = this.searchFilters.status as PetStatus;
      }

      this.petService.searchPets(filters).subscribe({
        next: (pets) => {
          this.pets = pets;
        },
        error: (error) => {
          this.snackBar.open('Error filtering pets', 'Close', { duration: 3000 });
          console.error('Error filtering pets:', error);
        }
      });
    } else {
      this.pets = [...this.allPets];
    }
  }

  toggleMyPets(): void {
    this.showMyPetsOnly = !this.showMyPetsOnly;
    this.applyFilters();
  }

  clearFilters(): void {
    this.searchFilters = {
      name: '',
      categoryId: '',
      status: ''
    };
    this.showMyPetsOnly = false;
    this.applyFilters();
  }

  toggleFilters(): void {
    this.showFilters = !this.showFilters;
  }

  toggleView(): void {
    this.viewMode = this.viewMode === 'card' ? 'list' : 'card';
  }

  refreshPets(): void {
    this.loadPets();
    this.snackBar.open('Pets refreshed', 'Close', { duration: 2000 });
  }

  updateStatus(pet: Pet): void {
    if (!pet.id) {
      return;
    }

    const dialogData: ConfirmDialogData = {
      title: 'Mark Pet as Sold',
      message: `Are you sure you want to mark <strong>"${pet.name}"</strong> as sold?\nThis action will update the pet's status and make it unavailable for purchase.`,
      confirmText: 'Mark as Sold',
      cancelText: 'Cancel',
      icon: 'shopping_cart',
      danger: false
    };

    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '450px',
      maxWidth: '90vw',
      data: dialogData,
      disableClose: true,
      panelClass: 'confirm-dialog-container',
      hasBackdrop: true,
      backdropClass: 'confirm-dialog-backdrop'
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result && pet.id) {
        this.petService.updatePetStatus(pet.id, PetStatus.SOLD).subscribe({
          next: (updatedPet) => {
            const index = this.pets.findIndex(p => p.id === pet.id);
            if (index !== -1) {
              this.pets[index] = updatedPet;
            }
            this.snackBar.open('Pet status updated', 'Close', { duration: 2000 });
          },
          error: (error) => {
            this.snackBar.open('Error updating pet status', 'Close', { duration: 3000 });
            console.error('Error updating pet status:', error);
          }
        });
      }
    });
  }

  deletePet(pet: Pet): void {
    if (!pet.id) {
      return;
    }

    const dialogData: ConfirmDialogData = {
      title: 'Delete Pet',
      message: `Are you sure you want to delete <strong>"${pet.name}"</strong>?\nThis action cannot be undone and will permanently remove this pet from the system.`,
      confirmText: 'Delete Pet',
      cancelText: 'Keep Pet',
      icon: 'delete_forever',
    };

    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '450px',
      maxWidth: '90vw',
      data: dialogData,
      disableClose: true,
      panelClass: 'confirm-dialog-container',
      hasBackdrop: true,
      backdropClass: 'confirm-dialog-backdrop'
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result && pet.id) {
        this.petService.deletePet(pet.id).subscribe({
          next: () => {
            this.pets = this.pets.filter(p => p.id !== pet.id);
            this.snackBar.open('Pet deleted successfully', 'Close', { duration: 2000 });
          },
          error: (error) => {
            this.snackBar.open('Error deleting pet', 'Close', { duration: 3000 });
            console.error('Error deleting pet:', error);
          }
        });
      }
    });
  }

  purchasePet(pet: Pet): void {
    if (!pet.id) {
      return;
    }

    const dialogData: ConfirmDialogData = {
      title: 'Purchase Pet',
      message: `Are you sure you want to purchase <strong>"${pet.name}"</strong> for $${pet.price}?\nThis will mark the pet as sold and update its status.`,
      confirmText: 'Purchase Now',
      cancelText: 'Not Now',
      icon: 'pets',
      danger: false
    };

    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '450px',
      maxWidth: '90vw',
      data: dialogData,
      disableClose: true,
      panelClass: 'confirm-dialog-container',
      hasBackdrop: true,
      backdropClass: 'confirm-dialog-backdrop'
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result && pet.id) {
        this.petService.purchasePet(pet.id).subscribe({
          next: (updatedPet: Pet) => {
            const index = this.pets.findIndex(p => p.id === pet.id);
            if (index !== -1) {
              this.pets[index] = updatedPet;
            }
            this.snackBar.open('Pet purchased successfully!', 'Close', { duration: 3000 });
          },
          error: (error: any) => {
            this.snackBar.open('Error purchasing pet', 'Close', { duration: 3000 });
            console.error('Error purchasing pet:', error);
          }
        });
      }
    });
  }

  addToCart(pet: Pet): void {
    if (!pet.id) {
      return;
    }

    this.storeService.addToCart(pet.id, this.userId!).subscribe({
      next: () => {
        this.snackBar.open(`"${pet.name}" added to cart!`, 'Close', { duration: 3000 });
        if (this.userId) {
          this.storeService.updateCartItemCount(this.userId);
        }
      },
      error: (error) => {
        const err = error as HttpErrorResponse;
        this.snackBar.open(err.error.message || 'Error adding pet to cart', 'Close', { duration: 3000 });
        //console.error('Error adding pet to cart:', error);
      }
    });
  }
}