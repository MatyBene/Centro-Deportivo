import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Routine, RoutineAssignment, TrainingHistory } from '../models/Routine';
import { TokenPayLoad } from '../models/Auth';
import { jwtDecode } from 'jwt-decode';

@Injectable({ providedIn: 'root' })
export class RoutineService {
  constructor(private http: HttpClient) {}

  getRoutines(): Observable<Routine[]> {
    return this.http.get<Routine[]>(`${environment.apiUrl}/routines`);
  }
  getRoutine(id: number): Observable<Routine> {
    return this.http.get<Routine>(`${environment.apiUrl}/routines/${id}`);
  }
  createRoutine(routine: Routine): Observable<Routine> {
    return this.http.post<Routine>(`${environment.apiUrl}/routines`, routine);
  }
  updateRoutine(id: number, routine: Routine): Observable<Routine> {
    return this.http.put<Routine>(`${environment.apiUrl}/routines/${id}`, routine);
  }
  deleteRoutine(id: number): Observable<void> {
    return this.http.delete<void>(`${environment.apiUrl}/routines/${id}`);
  }
  getRoutineAssignments(memberUsername?: string, active = true): Observable<RoutineAssignment[]> {
    const params: string[] = [];
    if (memberUsername) params.push(`memberUsername=${memberUsername}`);
    params.push(`active=${active}`);
    const qs = params.length ? `?${params.join('&')}` : '';
    return this.http.get<RoutineAssignment[]>(`${environment.apiUrl}/routineAssignments${qs}`);
  }
  assign(routineId: number, memberUsername: string): Observable<RoutineAssignment> {
    return this.http.post<RoutineAssignment>(`${environment.apiUrl}/routineAssignments`, { routineId, memberUsername });
  }
  deactivateAssignment(id: number): Observable<void> {
    return this.http.put<void>(`${environment.apiUrl}/routineAssignments/${id}/deactivate`, {});
  }
  getTrainingHistory(username?: string): Observable<TrainingHistory[]> {
    const qs = username ? `?username=${username}` : '';
    return this.http.get<TrainingHistory[]>(`${environment.apiUrl}/trainingHistory${qs}`);
  }
  createTrainingHistory(dto: TrainingHistory): Observable<TrainingHistory> {
    return this.http.post<TrainingHistory>(`${environment.apiUrl}/trainingHistory`, dto);
  }
  getCurrentUserUsername(): string {
    const token = localStorage.getItem('token');
    if (!token) return '';
    try {
      const decoded = jwtDecode<TokenPayLoad>(token);
      return decoded.sub;
    } catch {
      return '';
    }
  }
}