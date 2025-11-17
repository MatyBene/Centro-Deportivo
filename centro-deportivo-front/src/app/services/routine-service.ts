import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators'; 
import { Routine, RoutineResponse, TrainingHistory } from '../models/Routine';
import { AuthService } from './auth-service';


@Injectable({
  providedIn: 'root'
})
export class RoutineService {
  private readonly URLroutine = "http://localhost:3000/routines"; 
  private readonly URLtrainingHistory = "http://localhost:3000/trainingHistory"; 


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
    return this.http.get<TrainingHistory[]>(this.URLtrainingHistory)
  }

  createTrainingHistory(trainingHistory: TrainingHistory): Observable<TrainingHistory> {
    return this.http.post<TrainingHistory>(this.URLtrainingHistory, trainingHistory);
  }

  getUserRoutineAssignments(username: string) {
    return this.http.get<any[]>(`${this.URLroutine}/routineAssignments?memberUsername=${username}&active=true`);
  }
}
