import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { LoginDTO } from '../model/dto/login-dto';
import { Observable } from 'rxjs';
import {SignUpDataDto} from '../model/dto/sign-up-data-dto';

@Injectable({
  providedIn: 'root',
})
export class AuthService {

  constructor(private httpClient: HttpClient) { }

  login(loginDTO: LoginDTO): Observable<Object> {
    return this.httpClient.post(`/login`, loginDTO, { withCredentials: true} );
  }

  signUp(signInData: SignUpDataDto) {
    return this.httpClient.post(`/register`, signInData);
  }
}
