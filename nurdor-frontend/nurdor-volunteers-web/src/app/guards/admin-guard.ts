import {inject} from '@angular/core';
import {TokenService} from '../service/token-service';
import {Router} from '@angular/router';

export const adminGuard = () => {
  const tokenService = inject(TokenService);
  const router = inject(Router);
  if(tokenService.extractRole() === "ROLE_ADMIN") {
    return true;
  } else {
    return router.navigate(['/']);
  }
}
