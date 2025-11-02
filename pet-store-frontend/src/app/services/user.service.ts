import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BaseApiService } from './base-api.service';

export interface User {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  phoneNumber?: string;
  roles: string[];
  createdAt: string;
  updatedAt: string;
}

export interface UserUpdateRequest {
  firstName?: string;
  lastName?: string;
  email?: string;
  phoneNumber?: string;
  password?: string;
  roles?: string[];
}

@Injectable({
  providedIn: 'root'
})
export class UserService extends BaseApiService {

  constructor(http: HttpClient) {
    super(http);
  }

  /**
   * Get all users (ADMIN only)
   */
  getUsers(): Observable<User[]> {
    return this.http.get<User[]>(`${this.baseUrl}/users`);
  }

  /**
   * Get user by ID
   */
  getUserById(id: number): Observable<User> {
    return this.http.get<User>(`${this.baseUrl}/users/${id}`);
  }

  /**
   * Update user
   */
  updateUser(id: number, userUpdate: UserUpdateRequest): Observable<{message: string, user: User}> {
    return this.http.put<{message: string, user: User}>(`${this.baseUrl}/users/${id}`, userUpdate);
  }

  /**
   * Delete user (ADMIN only)
   */
  deleteUser(id: number): Observable<{message: string}> {
    return this.http.delete<{message: string}>(`${this.baseUrl}/users/${id}`);
  }
}