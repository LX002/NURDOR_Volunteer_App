import { Component, signal } from '@angular/core';
import {Router, RouterOutlet} from '@angular/router';
import { LoginForm } from './login-form/login-form';
import {TokenService} from './service/token-service';
import {HomePage} from './home-page/home-page';
import {NavBar} from './nav-bar/nav-bar';
import {authGuard} from './guards/auth-guard';
import {MessageBoxService} from './service/message-box-service';
import {MessageBox} from './message-box/message-box';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, NavBar, MessageBox],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App {

  protected readonly title = signal('nurdor-volunteers-web');
  messageBox!: MessageBoxService;

  constructor(private tokenService: TokenService, private mbService: MessageBoxService) {
    this.messageBox = mbService;
  }

  isLoggedIn(): boolean {
    return this.tokenService.tokenExistsSignal().valueOf();
  }
}
