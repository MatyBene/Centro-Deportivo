import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { LoginRequest, LoginResponse, TokenPayLoad } from '../models/Auth';
import { tap } from 'rxjs';
import { jwtDecode } from 'jwt-decode';

@Injectable({
  providedIn: 'root'
})

export class AuthService {
  private readonly URL = `${environment.apiUrl}/public/login`;

  constructor(private http: HttpClient){}

  login(username: string, password: string){
    const body: LoginRequest = {username, password};

    return this.http.post<LoginResponse>(this.URL, body).pipe(
      tap(response => {
        if(response.token){
          localStorage.setItem('token', response.token)
        }
      })
    )
  }

  isLoggedIn(): boolean {
    return !!localStorage.getItem('token');
  }

  getToken(): string | null{
    return localStorage.getItem('token');
  }

  getDecodedToken(): TokenPayLoad | null {
    const token = this.getToken();
    if(!token) return null;

    try {
      return jwtDecode<TokenPayLoad>(token);
    } catch (e) {
      console.error('Error al decodificar el token: ', e);
      return null;
    }
  }

  getUserRole(): 'MEMBER' | 'INSTRUCTOR' | 'ADMIN' | null {
    const decodedToken = this.getDecodedToken();

    const authority = decodedToken?.rol[0].authority;

    if (authority === 'ROLE_MEMBER') return 'MEMBER';
    if (authority === 'ROLE_INSTRUCTOR') return 'INSTRUCTOR';
    if (authority === 'ROLE_ADMIN') return 'ADMIN';

    return null;
  }

  getPermission(): string | null {
    const decodedToken = this.getDecodedToken();
    return decodedToken?.rol[1]?.authority ?? null;
  }

  logout(): void {
    localStorage.removeItem('token');
  }
}
