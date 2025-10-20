import { Injectable } from '@angular/core';
import { HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Pet, PetStatus } from '../models/pet.model';
import { BaseApiService } from './base-api.service';

@Injectable({
  providedIn: 'root'
})
export class PetService extends BaseApiService {
  private readonly endpoint = 'pets';

  getAllPets(limit?: number): Observable<Pet[]> {
    let params = new HttpParams();
    if (limit) {
      params = params.set('limit', limit.toString());
    }
    return this.http.get<Pet[]>(this.getApiUrl(this.endpoint), { params });
  }

  getPetById(id: number): Observable<Pet> {
    return this.http.get<Pet>(this.getApiUrl(`${this.endpoint}/${id}`));
  }

  findPetsByStatus(status: PetStatus): Observable<Pet[]> {
    const params = new HttpParams().set('status', status);
    return this.http.get<Pet[]>(this.getApiUrl(`${this.endpoint}/findByStatus`), { params });
  }

  searchPets(filters: { name?: string; categoryId?: number; status?: PetStatus }): Observable<Pet[]> {
    let params = new HttpParams();
    
    if (filters.name) {
      params = params.set('name', filters.name);
    }
    if (filters.categoryId) {
      params = params.set('categoryId', filters.categoryId.toString());
    }
    if (filters.status) {
      params = params.set('status', filters.status);
    }
    
    return this.http.get<Pet[]>(this.getApiUrl(this.endpoint), { params });
  }

  addPet(pet: Pet): Observable<Pet> {
    return this.http.post<Pet>(this.getApiUrl(this.endpoint), pet);
  }

  updatePet(id: number, pet: Pet): Observable<Pet> {
    return this.http.put<Pet>(this.getApiUrl(`${this.endpoint}/${id}`), pet);
  }

  deletePet(id: number): Observable<void> {
    return this.http.delete<void>(this.getApiUrl(`${this.endpoint}/${id}`));
  }

  updatePetStatus(id: number, status: PetStatus): Observable<Pet> {
    const params = new HttpParams().set('status', status);
    return this.http.post<Pet>(this.getApiUrl(`${this.endpoint}/${id}/status`), null, { params });
  }

  purchasePet(id: number): Observable<Pet> {
    return this.http.post<Pet>(this.getApiUrl(`${this.endpoint}/${id}/purchase`), {});
  }

  getMyPets(): Observable<Pet[]> {
    return this.http.get<Pet[]>(this.getApiUrl(`${this.endpoint}/my-pets`));
  }
}