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

    /**
     * Fetches all pets, optionally limited by count.
     * GET /pets?limit={limit}
     */
  getAllPets(limit?: number): Observable<Pet[]> {
    let params = new HttpParams();
    if (limit) {
      params = params.set('limit', limit.toString());
    }
    return this.http.get<Pet[]>(this.getApiUrl(this.endpoint), { params });
  }

    /**
     * Fetches a single pet by its ID.
     * GET /pets/{id}
     */
  getPetById(id: number): Observable<Pet> {
    return this.http.get<Pet>(this.getApiUrl(`${this.endpoint}/${id}`));
  }

    /**
     * Finds pets by their status.
     * GET /pets/findByStatus?status={status}
     */
  findPetsByStatus(status: PetStatus): Observable<Pet[]> {
    const params = new HttpParams().set('status', status);
    return this.http.get<Pet[]>(this.getApiUrl(`${this.endpoint}/findByStatus`), { params });
  }

    /**
     * Searches pets by name, category, or status.
     * GET /pets?name={name}&categoryId={categoryId}&status={status}
     */
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

    /**
     * Adds a new pet.
     * POST /pets
     */
  addPet(pet: Pet): Observable<Pet> {
    return this.http.post<Pet>(this.getApiUrl(this.endpoint), pet);
  }

    /**
     * Updates an existing pet by its ID.
     * PUT /pets/{id}
     */
  updatePet(id: number, pet: Pet): Observable<Pet> {
    return this.http.put<Pet>(this.getApiUrl(`${this.endpoint}/${id}`), pet);
  }

    /**
     * Deletes a pet by its ID.
     * DELETE /pets/{id}
     */
  deletePet(id: number): Observable<void> {
    return this.http.delete<void>(this.getApiUrl(`${this.endpoint}/${id}`));
  }

    /**
     * Updates the status of a pet by its ID.
     * POST /pets/{id}/status?status={status}
     */
  updatePetStatus(id: number, status: PetStatus): Observable<Pet> {
    const params = new HttpParams().set('status', status);
    return this.http.post<Pet>(this.getApiUrl(`${this.endpoint}/${id}/status`), null, { params });
  }

    /**
     * Purchases a pet by its ID.
     * POST /pets/{id}/purchase
     */
  purchasePet(id: number): Observable<Pet> {
    return this.http.post<Pet>(this.getApiUrl(`${this.endpoint}/${id}/purchase`), {});
  }

    /**
     * Fetches pets owned by the current user.
     * GET /pets/my-pets
     */
  getMyPets(): Observable<Pet[]> {
    return this.http.get<Pet[]>(this.getApiUrl(`${this.endpoint}/my-pets`));
  }
}