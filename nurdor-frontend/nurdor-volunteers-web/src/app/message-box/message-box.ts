import {Component, input, output, signal} from '@angular/core';
import {NgClass} from '@angular/common';
import {MessageBoxService} from '../service/message-box-service';

@Component({
  selector: 'app-message-box',
  imports: [
    NgClass
  ],
  templateUrl: './message-box.html',
  styleUrl: './message-box.css',
})
export class MessageBox {

  title = input.required<string>();
  message = input.required<string>();

  constructor(private mbService: MessageBoxService) { }

  close() { this.mbService.close(); }

  closeWithoutRedir() { this.mbService.closeWithoutRedir(); }
}
