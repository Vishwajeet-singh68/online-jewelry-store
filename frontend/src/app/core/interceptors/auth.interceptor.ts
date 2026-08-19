import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError, BehaviorSubject } from 'rxjs';
import { catchError, switchMap, filter, take } from 'rxjs/operators';
import { TokenService } from '../services/token.service';
import { AuthService } from '../services/auth.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  private isRefreshing = false;
  private refreshTokenSubject: BehaviorSubject<any> = new BehaviorSubject<any>(null);

  constructor(
    private tokenService: TokenService,
    private authService: AuthService
  ) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    let authReq = req;
    const token = this.tokenService.getAccessToken();

    // Do not attach token for public auth paths
    const isAuthEndpoint = req.url.includes('/api/v1/auth/login') ||
                           req.url.includes('/api/v1/auth/register') ||
                           req.url.includes('/api/v1/auth/refresh');

    if (token && !isAuthEndpoint) {
      authReq = this.addTokenHeader(req, token);
    }

    return next.handle(authReq).pipe(
      catchError((error: any) => {
        if (error instanceof HttpErrorResponse && error.status === 401 && !isAuthEndpoint) {
          return this.handle401Error(authReq, next);
        }
        return throwError(() => error);
      })
    );
  }

  private addTokenHeader(request: HttpRequest<any>, token: string): HttpRequest<any> {
    return request.clone({
      headers: request.headers.set('Authorization', `Bearer ${token}`)
    });
  }

  private handle401Error(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if (!this.isRefreshing) {
      this.isRefreshing = true;
      this.refreshTokenSubject.next(null);

      return this.authService.refreshToken().pipe(
        switchMap((res: any) => {
          this.isRefreshing = false;
          this.refreshTokenSubject.next(res.accessToken);
          return next.handle(this.addTokenHeader(request, res.accessToken));
        }),
        catchError((err) => {
          this.isRefreshing = false;
          this.authService.logout();
          return throwError(() => err);
        })
      );
    } else {
      return this.refreshTokenSubject.pipe(
        filter(token => token != null),
        take(1),
        switchMap(jwt => {
          return next.handle(this.addTokenHeader(request, jwt));
        })
      );
    }
  }
}
