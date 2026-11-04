import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {StartEventDto} from '../model/dto/start-event-dto';
import {CreateEventDto} from '../model/dto/create-event-dto';

@Injectable({
  providedIn: 'root',
})
export class EventsService {

  constructor(private httpClient: HttpClient) { }

  getAllEvents(): Observable<Object> {
    return this.httpClient.get(`/volunteer/getEvents`);
  }

  startEvent(startEventDto: StartEventDto): Observable<Object> {
    return this.httpClient.post(`/admin/start`, startEventDto)
  }

  endEvent(idEvent: number): Observable<Object> {
    return this.httpClient.post(`/admin/end/${idEvent}`, { responseType: "json" } );
  }

  getEvent(idEvent: number): Observable<Object> {
    return this.httpClient.get(`/volunteer/event/${idEvent}`);
  }

  createEvent(eventData: CreateEventDto): Observable<Object> {
    return this.httpClient.post(`/admin/newEvent`, eventData);
  }
}
