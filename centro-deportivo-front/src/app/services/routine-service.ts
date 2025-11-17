import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { Routine, TrainingHistory } from '../models/Routine';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class RoutineService {
  private readonly URLroutine = "http://localhost:3000"

  constructor(private http: HttpClient){}

  getRoutines(){
    return this.http.get<Routine[]>(`${this.URLroutine}/routines`)
  }
  getRoutine(id : string){
    return this.http.get<Routine>(`${this.URLroutine}/routines/${id}`)
  }
  getTrainingHistory(){
    return this.http.get<TrainingHistory[]>(`${this.URLroutine}/trainingHistory`)
  }

  getUserRoutineAssignments(username: string) {
  return this.http.get<any[]>(
    `${this.URLroutine}/routineAssignments?memberUsername=${username}&active=true`
  );
}
}
