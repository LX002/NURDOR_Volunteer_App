import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class VolunteerService {

  constructor(private httpClient: HttpClient) { }

  getCities() {
    return this.httpClient.get(`/cities`);
  }
}
