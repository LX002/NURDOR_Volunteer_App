import {Component, input, Input, OnInit, signal} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {DonationsService} from '../service/donations-service';
import {HttpErrorResponse} from '@angular/common/http';
import {MessageBoxData} from '../model/message-box-data';
import {MessageBoxService} from '../service/message-box-service';

@Component({
  selector: 'app-donate-form',
  imports: [
    ReactiveFormsModule,
  ],
  templateUrl: './donate-form.html',
  styleUrl: './donate-form.css',
})
export class DonateForm implements OnInit {

  donateFormGroup!: FormGroup
  eventId = input.required<number>()
  standId = input.required<number>();

  messageBoxData!: MessageBoxData;

  constructor(
    private donationsService: DonationsService,
    private mbService: MessageBoxService,
    private formBuilder: FormBuilder
  ) {}

  ngOnInit() {
    this.donateFormGroup = this.formBuilder.group({
      donation: this.formBuilder.control("", Validators.required)
    });
  }

  addDonation() {
    this.donationsService.addDonation({
      amount: this.donateFormGroup.value.donation,
      idEvent: this.eventId(),
      idStand: this.standId()
    }).subscribe({
      next: (response: any) => {
        this.messageBoxData = {
          message: response as string,
          redirectTo: "",
          title: "Success"
        }
        this.mbService.show(this.messageBoxData);
      },
      error: (err: HttpErrorResponse) => {
        this.messageBoxData = {
          message: `Error during adding donation: ${err.message}`,
          redirectTo: "",
          title: "Error"
        }
        this.mbService.show(this.messageBoxData);
      }
    });
  }

  protected readonly parseFloat = parseFloat;
  protected readonly isNaN = isNaN;
}
