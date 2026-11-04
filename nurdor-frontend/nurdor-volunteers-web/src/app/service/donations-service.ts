import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {DonationDto} from '../model/dto/donation-dto';

@Injectable({
  providedIn: 'root',
})
export class DonationsService {

  constructor(private httpClient: HttpClient) { }

  addDonation(donation: DonationDto): Observable<Object> {
    return this.httpClient.patch(`/volunteer/addDonation`, donation, { responseType: "text" });
  }

  getEventTakenStands(idEvent: Number): Observable<Object> {
    return this.httpClient.get(`/volunteer/standsOnEvent/${idEvent}`);
  }
}
