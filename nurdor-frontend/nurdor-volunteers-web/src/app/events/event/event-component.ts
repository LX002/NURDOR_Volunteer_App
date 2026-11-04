import {Component, computed, effect, ElementRef, input, linkedSignal, output, signal, viewChild} from '@angular/core';
import { NurdorEvent } from '../../model/nurdor-event';
import {TokenService} from '../../service/token-service';
import {RouterLink} from '@angular/router';
import {EventsService} from '../../service/events-service';
import {HttpErrorResponse} from '@angular/common/http';
import {StartEventResultDto} from '../../model/dto/start-event-result-dto';
import {EventsLogService} from '../../service/events-log-service';
import {MessageBoxService} from '../../service/message-box-service';
import {DecimalPipe} from '@angular/common';
import * as L from 'leaflet';
import {EndEventResultDto} from '../../model/dto/end-event-result-dto';

@Component({
  selector: 'app-event-component',
  imports: [
    RouterLink,
    DecimalPipe
  ],
  templateUrl: './event-component.html',
  styleUrl: './event-component.css',
  standalone: true
})
export class EventComponent {

  event = input.required<NurdorEvent>();
  currentEvent = linkedSignal(() => this.event())
  isOnHomePage = input<boolean>();
  onExpand = output<boolean>();
  removeEventOutput = output<NurdorEvent>();
  isExpanded = false;
  mapContainer = viewChild<ElementRef<HTMLDivElement>>("mapContainer");
  map: L.Map | undefined;

  displayedName = computed(() => {
    return this.isEnded()
      ? this.event().eventName.replace("END_EVENT", "")
      : this.event().eventName;
  })

  constructor(
    private tokenService: TokenService,
    private eventsService: EventsService,
    private eventsLogService: EventsLogService,
    private mbService: MessageBoxService
  ) {
    effect(() => {
      let mc = this.mapContainer()?.nativeElement;
      if(mc && !this.map) {
        this.setUpTheMap(mc);
      }
      if(!mc && this.map) {
        this.map.remove();
        this.map = undefined;
      }
    });
  }

  private setUpTheMap(mc: HTMLDivElement) {
    const customIcon = L.icon({
      iconUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png',
      iconRetinaUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png',
      shadowUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png',
      iconSize: [25, 41],
      iconAnchor: [12, 41],
      popupAnchor: [1, -34],
      shadowSize: [41, 41]
    });
    this.map = L.map(mc).setView([this.currentEvent().latitude, this.currentEvent().longitude], 10);
    L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {attribution: "@ OpenStreetMap contributors"})
      .addTo(this.map);
    L.marker([this.currentEvent().latitude, this.currentEvent().longitude]).setIcon(customIcon).addTo(this.map);
  }

  isAdmin() {
    return this.tokenService.extractRole() === "ROLE_ADMIN";
  }

  expandInfo() {
    console.log(`Clicked on event: ${this.event().eventName}`);
    this.isExpanded = !this.isExpanded;
    this.onExpand.emit(this.isExpanded);
  }

  setStartedStatus(isStarted: boolean) {
    let eventId = this.event().id;
    let observer = isStarted
        ? this.eventsService.startEvent({ idEvent: eventId, numberOfStands: 3 })
        : this.eventsService.endEvent(eventId);
    observer.subscribe({
      next: (response: any) => {
        this.currentEvent.update((event: NurdorEvent) => new NurdorEvent(
          event.id, event.eventName + (isStarted? '' : ' END_EVENT'), event.description, isStarted,
          event.startTime, event.endTime, event.latitude, event.longitude,
          event.locationDesc, event.eventImg, event.totalDonations, event.city
        ));

        this.handleResponse("Info", `Event ${this.currentEvent().eventName} is ${isStarted ? 'started' : 'ended'} ${!isStarted? '. Total donations: ' + (response as EndEventResultDto).totalDonations : ''}`, "");
      },
      error: (err: HttpErrorResponse) => {
        this.handleResponse("Error", `Error during ${isStarted ? 'starting' : 'ending'} of an event - ${err.error.message}`, "");
      }
    });
  }

  pick() {
    console.log("pick called");
    let volunteerId = sessionStorage?.getItem("VOLUNTEER_ID");
    if(volunteerId) {
      this.eventsLogService.insertLog({
        event: this.event().id,
        isPresent: 0,
        note: 'initialLog',
        volunteer: parseInt(volunteerId)
      }).subscribe({
        next: ()=> {
          this.handleResponse("Info", `Picked an event - ${this.event().eventName} [ID:${this.event().id}]`, "");
          this.removeEventOutput.emit(this.currentEvent())
        },
        error: (err: HttpErrorResponse) => {
          this.handleResponse("Error", `Error during picking - ${err.error.message}`, "");
        }
      });
    }
  }

  unpick() {
    console.log("unpick called");
    let volunteerId = sessionStorage?.getItem("VOLUNTEER_ID");
    if(volunteerId) {
      this.eventsLogService.deleteLog(parseInt(volunteerId), this.event().id).subscribe({
        next: (response: any)=> {
          this.handleResponse("Info", `Unpicked an event - ${this.event().eventName} [ID:${this.event().id}]`, "");
          this.removeEventOutput.emit(this.currentEvent())
        },
        error: (err: HttpErrorResponse) => {
          this.handleResponse("Error", `Error during unpicking - ${err.error.message}`, "");
        }
      });
    }
  }

  isStartDisabled(): boolean {
    return (Date.now() - new Date(this.currentEvent().startTime).getTime()) < 0
      || (Date.now() - new Date(this.currentEvent().endTime).getTime()) > 0;
  }

  isEnded(): boolean {
    return !this.currentEvent().isStarted && this.currentEvent().eventName.trim().includes("END_EVENT");
  }

  private handleResponse(type: "Success" | "Error" | "Info", message: string, redirectTo: string) {
    this.mbService.show({
      message: message,
      redirectTo: redirectTo,
      title: type
    });
  }
}
