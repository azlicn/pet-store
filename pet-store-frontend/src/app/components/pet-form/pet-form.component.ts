import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormArray } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { Pet, PetStatus } from '../../models/pet.model';
import { Category } from '../../models/category.model';
import { PetService } from '../../services/pet.service';
import { CategoryService } from '../../services/category.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-pet-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatCardModule,
    MatChipsModule,
    MatIconModule,
    MatSnackBarModule
  ],
  templateUrl: './pet-form.component.html',
  styleUrls: ['./pet-form.component.scss']
})
export class PetFormComponent implements OnInit {
  petForm: FormGroup;
  isEdit = false;
  petId: number | null = null;
  categories: Category[] = [];

  constructor(
    private fb: FormBuilder,
    private petService: PetService,
    private categoryService: CategoryService,
    private route: ActivatedRoute,
    private router: Router,
    private snackBar: MatSnackBar,
    private authService: AuthService
  ) {
    this.petForm = this.fb.group({
      name: ['', [Validators.required]],
      categoryId: ['', [Validators.required]],
      price: ['', [Validators.required, Validators.min(0)]],
      status: [PetStatus.AVAILABLE],
      photoUrlsText: [''],
      tagsText: ['']
    });
  }

  ngOnInit(): void {
    this.loadCategories();
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEdit = true;
      this.petId = +id;
      this.loadPet(this.petId);
    }
  }

  loadCategories(): void {
    this.categoryService.getAllCategories().subscribe({
      next: (categories) => {
        this.categories = categories;
      },
      error: (error: any) => {
        this.snackBar.open('Error loading categories', 'Close', { duration: 3000 });
        console.error('Error loading categories:', error);
      }
    });
  }

  loadPet(id: number): void {
    this.petService.getPetById(id).subscribe({
      next: (pet) => {
        if (!this.canEditPet(pet)) {
          this.snackBar.open('You do not have permission to edit this pet', 'Close', { duration: 3000 });
          this.router.navigate(['/pets']);
          return;
        }

        this.petForm.patchValue({
          name: pet.name,
          categoryId: pet.category?.id,
          price: pet.price,
          status: pet.status,
          photoUrlsText: pet.photoUrls ? pet.photoUrls.join('\n') : '',
          tagsText: pet.tags ? pet.tags.join(', ') : ''
        });
      },
      error: (error: any) => {
        this.snackBar.open('Error loading pet', 'Close', { duration: 3000 });
        console.error('Error loading pet:', error);
        this.router.navigate(['/pets']);
      }
    });
  }

  canEditPet(pet: Pet): boolean {
    if (this.authService.isAdmin()) {
      return true;
    }
    
    const currentUser = this.authService.getCurrentUser();
    return currentUser ? pet.createdBy === currentUser.id : false;
  }

  isAdmin(): boolean {
    return this.authService.isAdmin();
  }

  getStatusDisplayText(status: PetStatus): string {
    switch (status) {
      case PetStatus.AVAILABLE:
        return 'Available';
      case PetStatus.PENDING:
        return 'Pending';
      case PetStatus.SOLD:
        return 'Sold';
      default:
        return status;
    }
  }

  onSubmit(): void {
    if (this.petForm.valid) {
      const formValue = this.petForm.value;
      
      const selectedCategory = this.categories.find(c => c.id === formValue.categoryId);
      if (!selectedCategory) {
        this.snackBar.open('Please select a valid category', 'Close', { duration: 3000 });
        return;
      }
      
      const pet: Pet = {
        name: formValue.name,
        category: selectedCategory,
        price: +formValue.price,
        status: formValue.status,
        photoUrls: this.parsePhotoUrls(formValue.photoUrlsText),
        tags: this.parseTags(formValue.tagsText)
      };

      if (this.isEdit && this.petId) {
        console.log('Auth Debug Info:');
        console.log('Is logged in:', this.authService.isLoggedIn());
        console.log('Current user:', this.authService.getCurrentUser());
        console.log('Token exists:', !!this.authService.getToken());
        console.log('Token preview:', this.authService.getToken()?.substring(0, 50) + '...');
        console.log('Full token for verification:', this.authService.getToken());
        
        this.petService.updatePet(this.petId, pet).subscribe({
          next: () => {
            this.snackBar.open('Pet updated successfully', 'Close', { duration: 2000 });
            this.router.navigate(['/pets']);
          },
          error: (error: any) => {
            console.error('Full error object:', error);
            this.snackBar.open('Error updating pet', 'Close', { duration: 3000 });
            console.error('Error updating pet:', error);
          }
        });
      } else {
        this.petService.addPet(pet).subscribe({
          next: () => {
            this.snackBar.open('Pet added successfully', 'Close', { duration: 2000 });
            this.router.navigate(['/pets']);
          },
          error: (error: any) => {
            this.snackBar.open('Error adding pet', 'Close', { duration: 3000 });
            console.error('Error adding pet:', error);
          }
        });
      }
    }
  }

  onCancel(): void {
    this.router.navigate(['/pets']);
  }

  private parsePhotoUrls(text: string): string[] | undefined {
    if (!text.trim()) return undefined;
    return text.split('\n')
               .map(url => url.trim())
               .filter(url => url.length > 0);
  }

  private parseTags(text: string): string[] | undefined {
    if (!text.trim()) return undefined;
    return text.split(',')
               .map(tag => tag.trim())
               .filter(tag => tag.length > 0);
  }
}