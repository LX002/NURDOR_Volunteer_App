import { Routes } from '@angular/router';
import { HomePage } from './home-page/home-page';
import { LoginForm } from './login-form/login-form';
import { authGuard } from './guards/auth-guard';
import {ExplorePage} from './explore-page/explore-page';
import {StartedEventPage} from './events/started-event-page/started-event-page';
import {SignUpForm} from './sign-up-form/sign-up-form';
import {CreateEventForm} from './create-event-form/create-event-form';
import {adminGuard} from './guards/admin-guard';

export const routes: Routes = [
  { path: 'api/login', component: LoginForm, pathMatch: 'full' },
  { path: 'api/sign-up', component: SignUpForm, pathMatch: 'full' },
  { path: 'api/explore-page', component: ExplorePage, pathMatch: 'full', canActivate: [authGuard] },
  { path: 'api/started-event/:id', component: StartedEventPage, pathMatch: 'full', canActivate: [authGuard]},
  { path: 'api/create-event', component: CreateEventForm, pathMatch: 'full', canActivate: [authGuard, adminGuard] },
  { path: '', component: HomePage, pathMatch: 'full', canActivate: [authGuard] }
  // add wildcard for page not found, sign in component
];
