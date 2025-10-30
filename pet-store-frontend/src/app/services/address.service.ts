import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Address } from '../models/address.model';
import { BaseApiService } from './base-api.service';

@Injectable({
  providedIn: 'root'
})
export class AddressService extends BaseApiService {

  private readonly addressesUrl = this.getApiUrl('users');

  constructor(http: HttpClient) {
    super(http);
  }

    /**
     * Fetches all addresses for the current user.
     * GET /users/addresses
     */
  getAddresses(): Observable<Address[]> {
    return this.http.get<Address[]>(`${this.addressesUrl}/addresses`);
  }

    /**
     * Fetches a single address by its ID.
     * GET /users/addresses/{id}
     */
  getAddressById(id: number): Observable<Address> {
    return this.http.get<Address>(`${this.addressesUrl}/addresses/${id}`);
  }

    /**
     * Creates a new address for the current user.
     * POST /users/addresses
     */
  createAddress(address: Address): Observable<Address> {
    return this.http.post<Address>(`${this.addressesUrl}/addresses`, address);
  }

    /**
     * Updates an existing address by its ID.
     * PUT /users/addresses/{id}
     */
  updateAddress(id: number, address: Address): Observable<Address> {
    return this.http.put<Address>(`${this.addressesUrl}/addresses/${id}`, address);
  }

    /**
     * Deletes an address by its ID.
     * DELETE /users/addresses/{id}
     */
  deleteAddress(id: number): Observable<void> {
    return this.http.delete<void>(`${this.addressesUrl}/addresses/${id}`);
  }
}
