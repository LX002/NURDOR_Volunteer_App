import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {HttpClient, HttpParams} from '@angular/common/http';
import {EventsLogDto} from '../model/dto/events-log-dto';

@Injectable({
  providedIn: 'root',
})
export class EventsLogService {

  constructor(private httpClient: HttpClient) { }

  getAllEventsLogs(): Observable<Object> {
    return this.httpClient.get(`volunteer/allEventsLogs`);
  }

  insertLog(log: EventsLogDto): Observable<Object> {
    return this.httpClient.post(`volunteer/insertLog`, log);
  }

  deleteLog(idVolunteer: number, idEvent: number): Observable<Object> {
    let params = new HttpParams()
      .set("idEvent", idEvent)
      .set("idVolunteer", idVolunteer);
    return this.httpClient.delete(`volunteer/deleteLog`, { params, responseType: 'text' });
  }

  updatePresence(log: EventsLogDto): Observable<Object> {
    return this.httpClient.patch(`volunteer/updatePresence`, log)
  }
}
