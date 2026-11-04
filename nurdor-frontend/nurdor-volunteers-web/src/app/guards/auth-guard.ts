import {inject} from '@angular/core';
import {Router} from '@angular/router';
import {TokenService} from '../service/token-service';

export const authGuard = () => {
  const tokenService = inject(TokenService);
  const router = inject(Router);

  if(tokenService.getToken()) {
    return true;
  } else {
    return router.navigate(['api/login']);
  }
}
