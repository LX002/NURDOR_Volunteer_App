import {Component, effect, inject, Injector, OnInit, Signal, signal} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {AuthService} from '../service/auth-service';
import {Router, RouterLink} from '@angular/router';
import {SignUpDataDto} from '../model/dto/sign-up-data-dto';
import {HttpErrorResponse} from '@angular/common/http';
import {AuthResponse} from '../model/dto/auth-response';
import {SignUpResponse} from '../model/dto/sign-up-response';
import {CityDto} from '../model/dto/city-dto';
import {VolunteerService} from '../service/volunteer-service';
import {MessageBoxService} from '../service/message-box-service';
import {toSignal} from '@angular/core/rxjs-interop';
import {map} from 'rxjs';

@Component({
  selector: 'app-sign-in-form',
  imports: [
    ReactiveFormsModule,
    RouterLink
  ],
  templateUrl: './sign-up-form.html',
  styleUrl: './sign-up-form.css',
})
export class SignUpForm implements OnInit {

  signInFormGroup!: FormGroup
  profilePicture = signal<string>("");
  cities = signal<Array<CityDto>>([]);
  zipCode = signal<string>("");
  passwordsMatching!: Signal<boolean>;

  constructor(
    private authService: AuthService,
    private volunteerService: VolunteerService,
    private mbService: MessageBoxService,
    private formBuilder: FormBuilder
  ) {
    this.loadForm();
    this.passwordsMatching = toSignal(
      this.signInFormGroup.valueChanges.pipe(
        map(val => val.password && val.password === val.repeatPassword)
      ),
      { initialValue: false }
    );
  }

  private loadForm() {
    let addressReg = "^(?<street>[A-Za-z\\s]+)\\s+(?<number>\\d+[a-zA-Z]?),\\s*(?<postalCode>\\d{5})\\s+(?<city>[A-Za-z\\s]+)$";
    this.signInFormGroup = this.formBuilder.group({
      name: this.formBuilder.control("", [Validators.required, Validators.pattern("^[A-Za-z ]*$")]),
      surname: this.formBuilder.control("", [Validators.required, Validators.pattern("^[A-Za-z ]*$")]),
      address: this.formBuilder.control("", [Validators.required, Validators.pattern(addressReg)]),
      nearestCity: this.formBuilder.control("", [Validators.required]),
      phoneNumber: this.formBuilder.control("", [Validators.required, Validators.pattern("^(\\+[0-9]{1,3}[- ]?)?[0-9][0-9 -]{5,13}[0-9]$")]),
      email: this.formBuilder.control("", [Validators.required, Validators.email]),
      username: this.formBuilder.control("", [Validators.required]),
      password: this.formBuilder.control("", [Validators.required]),
      repeatPassword: this.formBuilder.control("", [Validators.required])
    });
  }

  ngOnInit() {
    this.volunteerService.getCities().subscribe({
      next: (response: any) => {
        let cities = response as Array<CityDto>;
        this.cities.set(cities);
      },
      error: (err: HttpErrorResponse) => { console.error(`Error during fetching cities: ${err.message}`) }
    })
  }

  async onProfilePhotoChange(event: Event) {
    let validTypes = ["image/png", "image/jpeg"];
    let fileSelection = event.target as HTMLInputElement;
    if(fileSelection.files && fileSelection.files.length > 0) {
      let picture = fileSelection.files[0];
      if(validTypes.includes(picture.type)) {
        let base64Picture = await this.fileToBase64(picture);
        this.profilePicture.set(base64Picture.split(",")[1]);
      } else {
        console.error("Error during picking of image: invalid file type!")
      }
    }
  }

  onCitySelectionChange(event: Event) {
    let citySelection = event.target as HTMLSelectElement;
    this.zipCode.set(citySelection.value);
  }

  fileToBase64(file: File): Promise<string> {
    return new Promise((resolve, reject) => {
      let reader = new FileReader();
      reader.readAsDataURL(file);
      reader.onload = () => resolve(reader.result as string);
      reader.onerror = (error) => reject(error);
    })
  }

  signUp() {
    let data = this.getFormData();
    if(data) {
      this.authService.signUp(data).subscribe({
        next: (result: any) => {
          let signUpResponse = result as AuthResponse<SignUpResponse>;
          this.handleResponse("Success", signUpResponse.message, "/api/login");
          //this.router.navigate(['/api/login']);
        },
        error: (err: HttpErrorResponse) => {
          this.handleResponse("Error", `Error during signing in: ${err.error.message}`, "");
        }
      });
    }
  }

  private getFormData(): SignUpDataDto | null {
    const sif = this.signInFormGroup.value;
    let password = sif.password;
    let repeatPassword = sif.repeatPassword;
    if(password !== repeatPassword) return null;

    return {
      address: sif.address,
      email: sif.email,
      name: sif.name,
      password: sif.password,
      phoneNumber: sif.phoneNumber,
      profilePicture: this.profilePicture(),
      surname: sif.surname,
      username: sif.username,
      volunteerRole: 2,
      zipCode: sif.nearestCity
    }
  }

  private handleResponse(type: "Success" | "Error" | "Confirmation", message: string, redirectTo: string) {
    this.mbService.show({
      message: message,
      redirectTo: redirectTo,
      title: type
    });
  }
}
