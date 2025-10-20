import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatChipsModule } from '@angular/material/chips';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

import { UserService, User } from '../../services/user.service';
import { AuthService } from '../../services/auth.service';
import { ErrorHandlerService } from '../../services/error-handler.service';
import { HttpErrorResponse } from '../../models/error-response.model';
import { ConfirmDialogComponent, ConfirmDialogData } from '../confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-user-list',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatTableModule,
    MatChipsModule,
    MatDialogModule,
    MatProgressSpinnerModule,
    MatTooltipModule,
    MatSnackBarModule
  ],
  templateUrl: './user-list.component.html',
  styleUrl: './user-list.component.scss'
})
export class UserListComponent implements OnInit {
  private userService = inject(UserService);
  private authService = inject(AuthService);
  private dialog = inject(MatDialog);
  private router = inject(Router);
  private snackBar = inject(MatSnackBar);
  private errorHandler = inject(ErrorHandlerService);

  users: User[] = [];
  loading = false;
  displayedColumns: string[] = ['id', 'name', 'email', 'roles', 'createdAt', 'actions'];

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.loading = true;
    this.userService.getUsers().subscribe({
      next: (users) => {
        this.users = users;
        this.loading = false;
      },
      error: (error: HttpErrorResponse) => {
        console.error('Error loading users:', error);
        
        const errorMessage = this.errorHandler.extractErrorMessage(error, 'Failed to load users');
        const duration = this.errorHandler.getErrorDuration(error);
        
        this.snackBar.open(errorMessage, 'Close', {
          duration: duration,
          panelClass: ['error-snackbar']
        });
        
        this.loading = false;
      }
    });
  }

  onDeleteUser(user: User): void {
    if (!user.id) {
      return;
    }

    const dialogData: ConfirmDialogData = {
          title: 'Delete User',
          message: `Are you sure you want to delete user "<strong>${user.firstName} ${user.lastName}</strong>"?\n\n<strong>Important:</strong> Users who own pets or have created pets cannot be deleted until those pets are removed or transferred to another user.\n\nThis action cannot be undone and will permanently remove this user from the system.`,
          confirmText: 'Delete User',
          cancelText: 'Keep User',
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
      if (result) {
        this.deleteUser(user.id);
      }
    });
  }

  private deleteUser(userId: number): void {
    this.userService.deleteUser(userId).subscribe({
      next: (response: any) => {
        console.log('User deleted successfully:', response);
        
        this.snackBar.open(response.message || 'User deleted successfully', 'Close', {
          duration: 3000,
          panelClass: ['success-snackbar']
        });
        
        this.loadUsers(); 
      },
      error: (error: HttpErrorResponse) => {
        console.error('Error deleting user:', error);
        
        const errorMessage = this.errorHandler.extractErrorMessage(error, 'Failed to delete user');
        const duration = this.errorHandler.getErrorDuration(error);
        
        this.snackBar.open(errorMessage, 'Close', {
          duration: duration,
          panelClass: ['error-snackbar']
        });
      }
    });
  }

  isCurrentUser(userId: number): boolean {
    const currentUser = this.authService.getCurrentUser();
    return currentUser?.id === userId;
  }

  getRoleColor(role: string): string {
    return role === 'ADMIN' ? 'warn' : 'primary';
  }

  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleDateString();
  }
}