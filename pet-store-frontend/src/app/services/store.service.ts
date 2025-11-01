import { Injectable } from '@angular/core';
import { BaseApiService } from './base-api.service';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, tap, switchMap } from 'rxjs';
import { PaymentOrderRequest } from '../models/paymentOrder.model';

@Injectable({
  providedIn: 'root'
})
export class StoreService extends BaseApiService {

  private readonly storesUrl = this.getApiUrl('stores');
  private cartItemCountSubject = new BehaviorSubject<number>(0);
  cartItemCount$ = this.cartItemCountSubject.asObservable();
  private cartSubject = new BehaviorSubject<any>(null);
  cart$ = this.cartSubject.asObservable();

  constructor(http: HttpClient) {
    super(http);
  }

  /**
   * Get all orders for the user
   * GET /api/stores/orders
   */
  getAllOrders(): Observable<any> {
    return this.http.get(`${this.storesUrl}/orders`);
  }

  /**
   * Add a pet to the user's cart
   * POST /api/stores/cart/add/{petId}
   */
  addToCart(petId: number, userId: number): Observable<any> {
    return this.http.post(`${this.storesUrl}/cart/add/${petId}`, {}).pipe(
      switchMap(() => this.getCart(userId))
    );
  }

  /**
   * Get the user's cart
   * GET /api/stores/cart/{userId}
   * Note: Backend now returns an empty cart instead of 404 if cart doesn't exist
   */
  getCart(userId: number): Observable<any> {
    return this.http.get(`${this.storesUrl}/cart/${userId}`, {}).pipe(
      tap(cart => {
        this.cartSubject.next(cart);
        const count = (cart as any)?.items?.length || 0;
        this.cartItemCountSubject.next(count);
      })
    );
  }

  /**
   * Get the user's order by order ID
   * GET /api/stores/order/{orderId}
   */
  getOrder(orderId: number): Observable<any> {
    return this.http.get(`${this.storesUrl}/order/${orderId}`, {});
  }

  /**
   * Update the cart item count from backend
   * GET /api/stores/cart/{userId}
   */
  updateCartItemCount(userId: number) {
    this.getCart(userId).subscribe({
      next: (cart) => {
        const count = (cart as any)?.items?.length || 0;
        this.cartItemCountSubject.next(count);
      },
      error: () => {
        this.cartItemCountSubject.next(0);
      }
    });
  }

  /**
   * Validate a discount code for the cart
   * GET /api/stores/cart/discount/validate?code={code}&total={total}
   */
  validateDiscountCode(code: string, total: number): Observable<any> {
    return this.http.get(`${this.storesUrl}/cart/discount/validate`, { params: { code, total } });
  }

  /**
   * Checkout a user's cart into an order
   * POST /api/store/checkout?addressId={addressId}&discountCode={discountCode}
   */
  checkout(discountCode?: string) {
    let params: any = {};
    if (discountCode) params.discountCode = discountCode;
    return this.http.post(`${this.storesUrl}/checkout`, null, { params });
  }

  /**
   * Remove an item from the user's cart
   * DELETE /api/stores/cart/item/{cartItemId}
   */
  removeItem(cartItemId: number, userId: number): Observable<any> {
    return this.http.delete(`${this.storesUrl}/cart/item/${cartItemId}`, {}).pipe(
      switchMap(() => this.getCart(userId))
    );
  }

  /**
   * Make payment for a specific order
   * POST /api/store/order/{orderId}/pay
   */
  makePayment(orderId: number, paymentOrderRequest: PaymentOrderRequest) {
    return this.http.post(`${this.storesUrl}/order/${orderId}/pay`, paymentOrderRequest);
  }

  /**
   * Cancel a specific order
   * DELETE /api/stores/order/{orderId}
   */
  cancelOrder(orderId: number): Observable<any> {
    return this.http.delete(`${this.storesUrl}/order/${orderId}`);
  }

  /**
   * Update delivery status of a specific order
   * PATCH /api/stores/order/{orderId}/delivery-status
   */
  updateDeliveryStatus(orderId: number, status: string, date?: string): Observable<any> {
    return this.http.patch(`${this.storesUrl}/order/${orderId}/delivery-status`, { status, date });
  }

  public clearCartItemCount() {
    this.cartItemCountSubject.next(0);
  }

}
