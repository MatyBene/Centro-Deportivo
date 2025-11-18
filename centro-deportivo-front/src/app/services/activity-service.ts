import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { HttpClient, HttpParams } from '@angular/common/http';
import { PageableResponse } from '../models/Pageable';
import SportActivitySummary from '../models/SportActivitySummary';
import SportActivity from '../models/SportActivity';

@Injectable({
  providedIn: 'root'
})
export class ActivityService {
  private readonly URL = `${environment.apiUrl}/activities`;

  constructor(private http: HttpClient) {}

  getActivities(page: number, size: number) {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<PageableResponse<SportActivitySummary>>(this.URL, {params});
  }

  getByName(name: string, page: number, size: number) {
    const params = new HttpParams()
      .set('name', name)
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<PageableResponse<SportActivitySummary>>(`${this.URL}/search`, {params});
  }

  getByTimeRange(startTime: string, endTime: string, page: number, size: number) {
    const params = new HttpParams()
      .set('startTime', startTime)
      .set('endTime', endTime)
      .set('page', page.toString())
      .set('size', size.toString());
    
      return this.http.get<PageableResponse<SportActivitySummary>>(`${this.URL}/search-by-time`, {params});
  }
  
  getActivity(id: number){
    return this.http.get<SportActivity>(`${this.URL}/${id}`)
  }
}
