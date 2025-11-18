import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { Member } from '../models/Member';
import { Observable } from 'rxjs';
import EnrolledActivitySummary from '../models/EnrolledActivitySummary';
import { LoginResponse } from '../models/Auth';

@Injectable({
  providedIn: 'root'
})
export class MemberService {
  private readonly URL = environment.apiUrl;

  constructor(private http: HttpClient){}

  register(member: Member){
    return this.http.post(`${this.URL}/public/register`, member, {responseType: 'text'});
  }

  getMember() {
    return this.http.get<Member>(`${this.URL}/members/profile`);
  }

  deleteMember() {
    return this.http.delete(`${this.URL}/members/me`, {responseType: 'text'});
  }

  getEnrolledActivities(): Observable<EnrolledActivitySummary[]> {
    return this.http.get<EnrolledActivitySummary[]>(`${this.URL}/members/activities`);
  }

  unsubscribeFromActivity(activityId: number): Observable<void> {
    return this.http.delete<void>(`${this.URL}/members/activities/${activityId}`, {responseType: 'text' as 'json'});
  }

  subscribeToActivity(activityId: number) {
    return this.http.post<void>(`${this.URL}/members/enroll/${activityId}`, {}, {responseType: 'text' as 'json'});
  }

  putMember(member: Member) {
    return this.http.put(`${this.URL}/members/profile`, member, {responseType: 'text'});
  }
}
