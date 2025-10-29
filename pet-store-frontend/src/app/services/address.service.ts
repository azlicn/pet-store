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

  getAddresses(): Observable<Address[]> {
    return this.http.get<Address[]>(`${this.addressesUrl}/addresses`);
  }

  getAddressById(id: number): Observable<Address> {
    return this.http.get<Address>(`${this.addressesUrl}/addresses/${id}`);
  }

  createAddress(address: Address): Observable<Address> {
    return this.http.post<Address>(`${this.addressesUrl}/addresses`, address);
  }

  updateAddress(id: number, address: Address): Observable<Address> {
    return this.http.put<Address>(`${this.addressesUrl}/addresses/${id}`, address);
  }

  deleteAddress(id: number): Observable<void> {
    return this.http.delete<void>(`${this.addressesUrl}/addresses/${id}`);
  }
}
