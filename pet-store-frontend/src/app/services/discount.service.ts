import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Discount } from '../models/discount.model';
import { BaseApiService } from './base-api.service';

@Injectable({ providedIn: 'root' })
export class DiscountService extends BaseApiService {

  private readonly discountsUrl = this.getApiUrl('discounts');

  constructor(http: HttpClient) {
    super(http);
  }

    /**
     * Fetches all discounts.
     * GET /discounts
     */
  getAllDiscounts(): Observable<Discount[]> {
    return this.http.get<Discount[]>(this.discountsUrl);
  }

    /**
     * Fetches all available and active discounts.
     * GET /discounts/active
     */
  getAvailableActiveDiscounts(): Observable<Discount[]> {
    return this.http.get<Discount[]>(`${this.discountsUrl}/active`);
  }

    /**
     * Fetches a single discount by its ID.
     * GET /discounts/{id}
     */
  getDiscountById(id: number): Observable<Discount> {
    return this.http.get<Discount>(`${this.discountsUrl}/${id}`);
  }

    /**
     * Creates a new discount.
     * POST /discounts
     */
  createDiscount(discount: Discount): Observable<Discount> {
    return this.http.post<Discount>(this.discountsUrl, discount);
  }

    /**
     * Updates an existing discount by its ID.
     * PUT /discounts/{id}
     */
  updateDiscount(id: number, discount: Discount): Observable<Discount> {
    return this.http.put<Discount>(`${this.discountsUrl}/${id}`, discount);
  }

    /**
     * Deletes a discount by its ID.
     * DELETE /discounts/{id}
     */
  deleteDiscount(id: number): Observable<void> {
    return this.http.delete<void>(`${this.discountsUrl}/${id}`);
  }
}
