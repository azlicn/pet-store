import { Injectable } from "@angular/core";
import { HttpParams } from "@angular/common/http";
import { Observable } from "rxjs";
import { Pet, PetStatus } from "../models/pet.model";

export interface PetPageResponse {
  pets: Pet[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}
import { BaseApiService } from "./base-api.service";

@Injectable({
  providedIn: "root",
})
export class PetService extends BaseApiService {
  private readonly endpoint = "pets";

  /**
   * Fetches all pets with pagination and optional filters.
   * GET /pets?page={page}&size={size}&name={name}&categoryId={categoryId}&status={status}
   */
  getAllPets(
    page: number = 0,
    size: number = 10,
    filters?: { name?: string; categoryId?: number; status?: PetStatus }
  ): Observable<PetPageResponse> {
    let params = new HttpParams()
      .set("page", page.toString())
      .set("size", size.toString());
    if (filters?.name) params = params.set("name", filters.name);
    if (filters?.categoryId)
      params = params.set("categoryId", filters.categoryId.toString());
    if (filters?.status) params = params.set("status", filters.status);
    return this.http.get<PetPageResponse>(this.getApiUrl(this.endpoint), {
      params,
    });
  }

  getLatestPets(limit?: number): Observable<Pet[]> {
    const params = limit ? new HttpParams().set("limit", limit.toString()) : undefined;
    return this.http.get<Pet[]>(this.getApiUrl(`${this.endpoint}/latest`), { params });
  }

  /**
   * Fetches a single pet by its ID.
   * GET /pets/{id}
   */
  getPetById(id: number): Observable<Pet> {
    return this.http.get<Pet>(this.getApiUrl(`${this.endpoint}/${id}`));
  }

  /**
   * Searches pets by name, category, or status.
   * GET /pets?name={name}&categoryId={categoryId}&status={status}
   */
  searchPets(filters: {
    name?: string;
    categoryId?: number;
    status?: PetStatus;
  }): Observable<Pet[]> {
    let params = new HttpParams();

    if (filters.name) {
      params = params.set("name", filters.name);
    }
    if (filters.categoryId) {
      params = params.set("categoryId", filters.categoryId.toString());
    }
    if (filters.status) {
      params = params.set("status", filters.status);
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
    const params = new HttpParams().set("status", status);
    return this.http.post<Pet>(
      this.getApiUrl(`${this.endpoint}/${id}/status`),
      null,
      { params }
    );
  }

  /**
   * Fetches pets owned by the current user with pagination and optional filters.
   * GET /pets/my-pets?page={page}&size={size}&name={name}&categoryId={categoryId}&status={status}
   */
  getMyPets(
    page: number = 0,
    size: number = 10,
    filters?: { name?: string; categoryId?: number; status?: PetStatus }
  ): Observable<PetPageResponse> {
    let params = new HttpParams()
      .set("page", page.toString())
      .set("size", size.toString());
    if (filters?.name) params = params.set("name", filters.name);
    if (filters?.categoryId)
      params = params.set("categoryId", filters.categoryId.toString());
    if (filters?.status) params = params.set("status", filters.status);
    return this.http.get<PetPageResponse>(this.getApiUrl(`${this.endpoint}/my-pets`), {
      params,
    });
  }
}
