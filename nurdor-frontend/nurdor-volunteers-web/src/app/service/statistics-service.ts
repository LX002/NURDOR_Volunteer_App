import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class StatisticsService {

  constructor(private httpClient: HttpClient) { }

  getVolunteersOnEvent(idEvent: number): Observable<Object> {
    let params = new HttpParams().set("idEvent", idEvent);
    return this.httpClient.get(`/volunteer/volunteersOnEvent`, { params });
  }
}
