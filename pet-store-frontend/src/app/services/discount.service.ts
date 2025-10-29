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

  getAllDiscounts(): Observable<Discount[]> {
    return this.http.get<Discount[]>(this.discountsUrl);
  }

  getAvailableActiveDiscounts(): Observable<Discount[]> {
    return this.http.get<Discount[]>(`${this.discountsUrl}/active`);
  }

  getDiscountById(id: number): Observable<Discount> {
    return this.http.get<Discount>(`${this.discountsUrl}/${id}`);
  }

  createDiscount(discount: Discount): Observable<Discount> {
    return this.http.post<Discount>(this.discountsUrl, discount);
  }

  updateDiscount(id: number, discount: Discount): Observable<Discount> {
    return this.http.put<Discount>(`${this.discountsUrl}/${id}`, discount);
  }

  deleteDiscount(id: number): Observable<void> {
    return this.http.delete<void>(`${this.discountsUrl}/${id}`);
  }
}
