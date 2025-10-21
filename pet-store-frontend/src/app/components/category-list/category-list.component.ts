import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatSnackBarModule, MatSnackBar } from '@angular/material/snack-bar';

import { Category } from '../../models/category.model';
import { HttpErrorResponse } from '../../models/error-response.model';
import { CategoryService } from '../../services/category.service';
import { ErrorHandlerService } from '../../services/error-handler.service';
import { ConfirmDialogComponent, ConfirmDialogData } from '../confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-category-list',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatTableModule,
    MatDialogModule,
    MatProgressSpinnerModule,
    MatTooltipModule,
    MatSnackBarModule
  ],
  templateUrl: './category-list.component.html',
  styleUrls: ['./category-list.component.scss']
})
export class CategoryListComponent implements OnInit {
  private categoryService = inject(CategoryService);
  private errorHandler = inject(ErrorHandlerService);
  private dialog = inject(MatDialog);
  private router = inject(Router);
  private snackBar = inject(MatSnackBar);

  categories: Category[] = [];
  loading = false;
  displayedColumns: string[] = ['id', 'name', 'actions'];

  ngOnInit(): void {
    this.loadCategories();
  }

  loadCategories(): void {
    this.loading = true;
    this.categoryService.getAllCategories().subscribe({
      next: (categories) => {
        this.categories = categories;
        this.loading = false;
      },
      error: (error: HttpErrorResponse) => {
        console.error('Error loading categories:', error);
        
        const errorMessage = this.errorHandler.extractErrorMessage(error, 'Failed to load categories');
        
        this.snackBar.open(errorMessage, 'Close', {
          duration: 3000,
          panelClass: ['error-snackbar']
        });
        this.loading = false;
      }
    });
  }

  onDeleteCategory(category: Category): void {
    if (!category.id) {
      return;
    }

    const dialogData: ConfirmDialogData = {
          title: 'Delete Category',
          message: `Are you sure you want to delete category "<strong>${category.name}</strong>"?<br>
                   <strong>Warning:</strong> This category cannot be deleted if it is currently being used by any pets. 
                   You will need to reassign or remove those pets first.<br>
                   This action cannot be undone and will permanently remove this category from the system.`,
          confirmText: 'Delete Category',
          cancelText: 'Keep Category',
          icon: 'delete_forever'
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
      if (result && category.id) {
        this.deleteCategory(category.id);
      }
    });
  }

  private deleteCategory(categoryId: number): void {
    this.categoryService.deleteCategory(categoryId).subscribe({
      next: () => {
        this.snackBar.open('Category deleted successfully', 'Close', {
          duration: 3000,
          panelClass: ['success-snackbar']
        });
        this.loadCategories(); 
      },
      error: (error: HttpErrorResponse) => {
        console.error('Error deleting category:', error);
        
        const errorMessage = this.errorHandler.extractErrorMessage(error, 'Failed to delete category');
        const duration = this.errorHandler.getErrorDuration(error);
        
        this.snackBar.open(errorMessage, 'Close', {
          duration: duration,
          panelClass: ['error-snackbar']
        });
      }
    });
  }
}