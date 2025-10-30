import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Category } from '../models/category.model';
import { BaseApiService } from './base-api.service';

@Injectable({
  providedIn: 'root'
})
export class CategoryService extends BaseApiService {
  
  private readonly categoriesUrl = this.getApiUrl('categories');

  constructor(http: HttpClient) {
    super(http);
  }

    /**
     * Fetches all categories.
     * GET /categories
     */
  getAllCategories(): Observable<Category[]> {
    return this.http.get<Category[]>(this.categoriesUrl);
  }

    /**
     * Fetches a single category by its ID.
     * GET /categories/{id}
     */
  getCategoryById(id: number): Observable<Category> {
    return this.http.get<Category>(`${this.categoriesUrl}/${id}`);
  }

    /**
     * Creates a new category.
     * POST /categories
     */
  createCategory(category: Category): Observable<Category> {
    return this.http.post<Category>(this.categoriesUrl, category);
  }

    /**
     * Updates an existing category by its ID.
     * PUT /categories/{id}
     */
  updateCategory(id: number, category: Category): Observable<Category> {
    return this.http.put<Category>(`${this.categoriesUrl}/${id}`, category);
  }

    /**
     * Deletes a category by its ID.
     * DELETE /categories/{id}
     */
  deleteCategory(id: number): Observable<void> {
    return this.http.delete<void>(`${this.categoriesUrl}/${id}`);
  }
}