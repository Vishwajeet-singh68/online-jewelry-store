import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap, catchError, throwError } from 'rxjs';
import { environment } from '../../../environments/environment';
import { TokenService } from './token.service';
import { AuthResponse, LoginRequest, RegisterRequest, User } from '../models/models';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly apiUrl = `${environment.apiUrl}/api/v1/auth`;

  currentUser = signal<User | null>(this.tokenService.getUser());
  isAuthenticated = signal<boolean>(!!this.tokenService.getAccessToken());

  constructor(
    private http: HttpClient,
    private tokenService: TokenService
  ) {}

  register(request: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, request).pipe(
      tap(res => this.handleAuthSuccess(res))
    );
  }

  login(request: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, request).pipe(
      tap(res => this.handleAuthSuccess(res))
    );
  }

  refreshToken(): Observable<AuthResponse> {
    const refreshToken = this.tokenService.getRefreshToken();
    if (!refreshToken) {
      return throwError(() => new Error('No refresh token available'));
    }
    return this.http.post<AuthResponse>(`${this.apiUrl}/refresh`, { refreshToken }).pipe(
      tap(res => this.handleAuthSuccess(res)),
      catchError(err => {
        this.logout();
        return throwError(() => err);
      })
    );
  }

  logout(): void {
    const refreshToken = this.tokenService.getRefreshToken();
    if (refreshToken) {
      this.http.post(`${this.apiUrl}/logout`, { refreshToken }).subscribe({
        error: () => {}
      });
    }
    this.tokenService.clear();
    this.currentUser.set(null);
    this.isAuthenticated.set(false);
  }

  private handleAuthSuccess(response: AuthResponse): void {
    this.tokenService.setAccessToken(response.accessToken);
    this.tokenService.setRefreshToken(response.refreshToken);
    this.tokenService.setUser(response.user);
    this.currentUser.set(response.user);
    this.isAuthenticated.set(true);
  }
}
