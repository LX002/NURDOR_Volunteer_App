import {Component, OnInit, output} from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { LoginDTO } from '../model/dto/login-dto';
import { AuthService } from '../service/auth-service';
import { HttpErrorResponse } from '@angular/common/http';
import {LoginData} from '../model/dto/login-data';
import {TokenService} from '../service/token-service';
import {AuthResponse} from '../model/dto/auth-response';
import {Router, RouterLink} from '@angular/router';
import {MessageBoxService} from '../service/message-box-service';

@Component({
  selector: 'app-login-form',
  imports: [FormsModule, ReactiveFormsModule, RouterLink],
  templateUrl: './login-form.html',
  styleUrl: './login-form.css',
  standalone: true
})
export class LoginForm implements OnInit {
  loginFormGroup!: FormGroup;
  loginData!: LoginDTO;

  private VOLUNTEER_ID_KEY = "VOLUNTEER_ID";

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private tokenService: TokenService,
    private mbService: MessageBoxService,
    private router: Router
  ) { }

  ngOnInit() {
    this.loginFormGroup = this.fb.group({
      username: this.fb.control('', Validators.required),
      password: this.fb.control('', Validators.required)
    });
  }

  get username() {
    return this.loginFormGroup.value.username;
  }
  get password() {
    return this.loginFormGroup.value.password;
  }

  login() {
    this.loginData = new LoginDTO(this.username, this.password);
    if(this.loginFormGroup.valid) {
      this.authService.login(this.loginData).subscribe({
        error: (err: HttpErrorResponse) => {
          this.handleErrorResponse(err.error as AuthResponse<null>);
        },
        next: (result: any) => {
          this.handleSuccessfulResponse(result as AuthResponse<LoginData> | null);
        }
      });
    } else {
      this.handleErrorResponse({ message: "Form is invalid!" });
    }
  }

  private handleErrorResponse(err: any) {
    this.mbService.show({
      title: "Error",
      redirectTo: "",
      message: `${err.message}`
    })
  }

  private handleSuccessfulResponse(loginResponse: AuthResponse<LoginData> | null) {
    if (loginResponse?.data.accessToken !== null && loginResponse?.data.accessToken !== undefined) {
      let token = loginResponse?.data.accessToken;
      let id = loginResponse?.data.volunteerId
      this.tokenService.setToken(token);
      sessionStorage.setItem(this.VOLUNTEER_ID_KEY, id?.toString());
      this.mbService.show({ title: "Success", redirectTo: "/", message: loginResponse.message });
      //this.router.navigate(['/']);
    } else {
      this.handleErrorResponse({ message: "Login failed - jwt was not set!" });
    }
  }
}
