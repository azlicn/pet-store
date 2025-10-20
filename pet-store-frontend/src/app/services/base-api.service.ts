import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class BaseApiService {
  protected readonly baseUrl = environment.apiUrl;

  constructor(protected http: HttpClient) { }

  protected getApiUrl(endpoint: string): string {
    return `${this.baseUrl}/${endpoint}`;
  }
}