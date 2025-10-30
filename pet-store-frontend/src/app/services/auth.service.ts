import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable, BehaviorSubject } from "rxjs";
import { tap } from "rxjs/operators";
import { BaseApiService } from "./base-api.service";

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  role?: string;
}

export interface AuthResponse {
  token: string;
  type: string;
  user: {
    id: number;
    email: string;
    firstName: string;
    lastName: string;
    roles: string[];
  };
}

export interface User {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  roles: string[];
}

@Injectable({
  providedIn: "root",
})
export class AuthService extends BaseApiService {
  private readonly authUrl = this.getApiUrl("auth");
  private readonly TOKEN_KEY = "auth-token";
  private readonly USER_KEY = "auth-user";

  private currentUserSubject = new BehaviorSubject<User | null>(
    this.getCurrentUser()
  );
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(http: HttpClient) {
    super(http);
  }

  login(loginRequest: LoginRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${this.authUrl}/login`, loginRequest)
      .pipe(
        tap((response) => {
          this.saveToken(response.token);
          this.saveUser(response.user);
          this.currentUserSubject.next(response.user);
        })
      );
  }

  register(registerRequest: RegisterRequest): Observable<any> {
    return this.http.post(`${this.authUrl}/register`, registerRequest);
  }

  logout(): void {
    this.removeToken();
    this.removeUser();
    this.currentUserSubject.next(null);
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  getCurrentUser(): User | null {
    const userData = sessionStorage.getItem(this.USER_KEY);
    if (userData) {
      return JSON.parse(userData);
    }
    return null;
  }

  getToken(): string | null {
    return sessionStorage.getItem(this.TOKEN_KEY);
  }

  hasRole(role: string): boolean {
    const user = this.getCurrentUser();
    return user ? user.roles.includes(role) : false;
  }

  isAdmin(): boolean {
    return this.hasRole("ADMIN");
  }

  isUser(): boolean {
    return this.hasRole("USER");
  }

  updateCurrentUser(updatedUser: User): void {
    this.saveUser(updatedUser);
    this.currentUserSubject.next(updatedUser);
  }

  private saveToken(token: string): void {
    sessionStorage.setItem(this.TOKEN_KEY, token);
  }

  private saveUser(user: User): void {
    sessionStorage.setItem(this.USER_KEY, JSON.stringify(user));
  }

  private removeToken(): void {
    sessionStorage.removeItem(this.TOKEN_KEY);
  }

  private removeUser(): void {
    sessionStorage.removeItem(this.USER_KEY);
  }
}
