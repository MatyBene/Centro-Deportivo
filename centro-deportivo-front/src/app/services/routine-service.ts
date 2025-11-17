import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { Routine, TrainingHistory } from '../models/Routine';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators'; 
import { Routine, RoutineResponse } from '../models/Routine';
import { AuthService } from './auth-service';


@Injectable({
  providedIn: 'root'
})
export class RoutineService {
  private readonly URLroutine = "http://localhost:3000/routines"; 


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

  getCurrentUserUsername(): string {
    const decodedToken = this.authService.getDecodedToken();
    return decodedToken?.sub || ''; 
  }
  getTrainingHistory(){
    return this.http.get<TrainingHistory[]>(`${this.URLroutine}/trainingHistory`)
  }

  getUserRoutineAssignments(username: string) {
    return this.http.get<any[]>(`${this.URLroutine}/routineAssignments?memberUsername=${username}&active=true`);
  }
}
