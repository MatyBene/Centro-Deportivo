import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Admin } from '../models/Admin';
import { Member } from '../models/Member';
import Instructor from '../models/Instructor';
import { PageableResponse } from '../models/Pageable';

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private readonly URL = `${environment.apiUrl}/admin`;

  constructor(private http: HttpClient) { }

  getAdmin() {
    return this.http.get<Admin>(`${this.URL}/profile`);
  }

  registerMember(member: Member) {
    return this.http.post(`${this.URL}/create-member`, member, {responseType: 'text'});
  }

  registerInstructor(instructor: Instructor) {
    return this.http.post(`${this.URL}/create-instructor`, instructor, {responseType: 'text'});
  }

  registerAdmin(admin: Admin) {
    return this.http.post(`${this.URL}/create-admin`, admin, {responseType: 'text'});
  }

  enrollMemberToActivity(activityId: string, username: string) {
    return this.http.post(`${this.URL}/enroll-member`, {username, activityId}, {responseType: 'text'})
  }

  unenrollMemberToActivity(activityId: string, username: string) {
    return this.http.delete(`${this.URL}/activity/${activityId}/member/${username}`, {responseType: 'text'});
  }

  getUsers(page: number, size: number, role?: string, status?: string, permission?: string) {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    
    if (role) {
      params = params.set('role', role);
    }
    if (status) {
      params = params.set('status', status);
    }
    if (permission) {
      params = params.set('permission', permission);
    }
    
    return this.http.get<PageableResponse<Member | Instructor | Admin>>(`${this.URL}/users`, {params});
  }

  getUserDetail(username: string) {
    return this.http.get<Member | Instructor | Admin>(`${this.URL}/users/${username}`);
  }

  deleteUser(username: string) {
    return this.http.delete(`${this.URL}/users/username/${username}`, {responseType: 'text'});
  }
}
