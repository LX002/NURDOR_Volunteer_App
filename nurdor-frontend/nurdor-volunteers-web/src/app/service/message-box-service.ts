import {Injectable, signal} from '@angular/core';
import {MessageBoxData} from '../model/message-box-data';
import {Router} from '@angular/router';

@Injectable({
  providedIn: 'root',
})
export class MessageBoxService {

  readonly data = signal<MessageBoxData | null>(null);

  constructor(private router: Router) { }

  show(mbData: MessageBoxData) { this.data.set(mbData); }

  close() {
    let redirectTo = this.data()?.redirectTo;
    this.data.set(null);
    if(redirectTo && redirectTo !== "") this.router.navigate([redirectTo]);
  }
  closeWithoutRedir() {
    this.data.set(null);
  }
}
