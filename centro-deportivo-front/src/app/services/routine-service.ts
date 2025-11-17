import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators'; 
import { Routine, RoutineResponse, RoutineAssignment } from '../models/Routine';
import { AuthService } from './auth-service';


@Injectable({
  providedIn: 'root'
})
export class RoutineService {
  private readonly URLroutine = "http://localhost:3000/routines";
  private readonly URLassignments = "http://localhost:3000/routineAssignments"; 


  constructor(private http: HttpClient, private authService: AuthService){}

  getRoutines(): Observable<Routine[]> {
    return this.http.get<Routine[]>(this.URLroutine);
  }
  getRoutine(id: string): Observable<RoutineResponse> {   
    return this.http.get<Routine>(`${this.URLroutine}/${id}`).pipe(
      map(routine => {
        return { routine: routine } as RoutineResponse;
      })
    );
  }

  createRoutine(routine: Routine): Observable<Routine> {
    return this.http.post<Routine>(this.URLroutine, routine);
  }

  updateRoutine(id: string, routine: Routine): Observable<Routine> {
    return this.http.put<Routine>(`${this.URLroutine}/${id}`, routine);
  }

  deleteRoutine(id: string): Observable<void> {
    return this.http.delete<void>(`${this.URLroutine}/${id}`);
  }

  getAllRoutineAssignments(): Observable<RoutineAssignment[]> {
    return this.http.get<RoutineAssignment[]>(this.URLassignments);
  }

  getCurrentUserUsername(): string {
    const decodedToken = this.authService.getDecodedToken();
    return decodedToken?.sub || ''; 
  }
}