import { Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { tap } from 'rxjs/operators';
import { Observable } from 'rxjs';
import { AuthResponse, AuthUser, LoginRequest, RegisterRequest } from './auth.models';
import { environment } from '../../environments/environment';

const TOKEN_KEY = 'ts-auth-token';
const USER_KEY  = 'ts-auth-user';

@Injectable({ providedIn: 'root' })
export class AuthService {

  private readonly authApi = `${environment.apiUrl}/api/auth`;

  private readonly _token = signal<string | null>(localStorage.getItem(TOKEN_KEY));
  private readonly _user  = signal<AuthUser | null>(this.loadUser());

  readonly isLoggedIn = computed(() => !!this._token());
  readonly currentUser = computed(() => this._user());
  readonly token = computed(() => this._token());

  constructor(private http: HttpClient, private router: Router) {}

  login(request: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.authApi}/login`, request).pipe(
      tap(res => this.persist(res))
    );
  }

  register(request: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.authApi}/register`, request).pipe(
      tap(res => this.persist(res))
    );
  }

  logout(): void {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    this._token.set(null);
    this._user.set(null);
    this.router.navigate(['/']);
  }

  private persist(res: AuthResponse): void {
    const user: AuthUser = { email: res.email, role: res.role };
    localStorage.setItem(TOKEN_KEY, res.token);
    localStorage.setItem(USER_KEY, JSON.stringify(user));
    this._token.set(res.token);
    this._user.set(user);
  }

  private loadUser(): AuthUser | null {
    try {
      const raw = localStorage.getItem(USER_KEY);
      return raw ? JSON.parse(raw) : null;
    } catch {
      return null;
    }
  }
}
