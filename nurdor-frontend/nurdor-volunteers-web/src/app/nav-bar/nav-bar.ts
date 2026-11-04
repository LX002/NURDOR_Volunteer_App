import { Component } from '@angular/core';
import {TokenService} from '../service/token-service';
import {Router, RouterLink} from '@angular/router';

@Component({
  selector: 'app-nav-bar',
  imports: [
    RouterLink
  ],
  templateUrl: './nav-bar.html',
  styleUrl: './nav-bar.css',
})
export class NavBar {

  isAdmin = false;

  constructor(
    private tokenService: TokenService,
    private router: Router,
  ) {
    this.isAdmin = tokenService.extractRole() == "ROLE_ADMIN";
  }

  logout() {
    this.tokenService.removeToken();
    this.router.navigate(['api/login']);
  }
}
