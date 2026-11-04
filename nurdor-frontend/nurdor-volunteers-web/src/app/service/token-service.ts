import {Injectable, signal} from '@angular/core';
import {jwtDecode} from 'jwt-decode';

interface TokenData {
  roles: Array<string>,
  sub: string,
  iat: number,
  exp: number
}

@Injectable({
  providedIn: 'root',
})
export class TokenService {

  private TOKEN_KEY = "accessToken";
  public tokenExistsSignal = signal(false);

  constructor() {
    this.tokenExistsSignal.set(this.getToken() !== null);
  }

  public setToken(token: string) {
    console.log("setting token: " + token);
    sessionStorage.setItem(this.TOKEN_KEY, token);
    this.tokenExistsSignal.set(true);
  }

  public getToken(): string | null {
    return sessionStorage.getItem(this.TOKEN_KEY);
  }

  public removeToken() {
    sessionStorage.removeItem(this.TOKEN_KEY);
    this.tokenExistsSignal.set(false);
  }

  public extractRole(): string | null {
    let token = this.getToken();
    if(token) {
      try {
        let decodedToken = jwtDecode<TokenData>(token);
        return decodedToken.roles[0];
      } catch (e) {
        console.log(`Error during role extraction: ${e}`);
      }
    }
    return null;
  }
}
