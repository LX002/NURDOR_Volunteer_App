import {Component, effect, input, OnInit, signal, viewChild} from '@angular/core';
import {NurdorEvent} from '../../model/nurdor-event';
import {EventComponent} from '../event/event-component';
import {EventsService} from '../../service/events-service';
import {EventDto} from '../../model/dto/event-dto';
import {HttpErrorResponse} from '@angular/common/http';
import {EventsLogService} from '../../service/events-log-service';
import {EventsLogDto} from '../../model/dto/events-log-dto';
import {forkJoin} from 'rxjs';
import {LoadingScreen} from '../../loading-screen/loading-screen';
import {SearchFilter} from '../../model/filters/search-filter';
import {NgClass} from '@angular/common';
import {MessageBoxService} from '../../service/message-box-service';
import {TokenService} from '../../service/token-service';



@Component({
  selector: 'app-event-list',
  imports: [EventComponent, LoadingScreen, NgClass],
  templateUrl: './event-list.html',
  styleUrl: './event-list.css',
  standalone: true
})
export class EventList implements OnInit {

  private volunteerId!: number;
  events = signal<Array<NurdorEvent>>([]);
  areEventsLoaded = signal(false);

  showPickedOnes = input(false);
  searchFilter = input<SearchFilter>({
    searchBoxInput: "",
    sortBy: "default",
    sortDirection: "asc"
  });

  normalState = "col-lg-3 col-md-6 col-sm-12";
  expandedState = "col-12"
  expansionFlags!: boolean[];

  constructor(
    private eventService: EventsService,
    private eventsLogService: EventsLogService,
    private tokenService: TokenService,
    private mbService: MessageBoxService
  ) {
    effect(() => {
      const filter = this.searchFilter();
      if (!filter) {
        return;
      }
      this.loadEvents();
      console.log(`filter: [input] - ${filter.searchBoxInput} | ${filter.sortBy} ${filter.sortDirection} effect loading events...`);
    });
  }

  ngOnInit(): void {
    this.volunteerId = Number(sessionStorage.getItem("VOLUNTEER_ID")); // create a constants file?
  }

  removeFromList(event: NurdorEvent) {
    this.events.set(this.events().filter(e => e.id !== event.id));
  }

  expandOrCollapse(index: number, isExpanding: boolean) {
    this.expansionFlags[index] = isExpanding;
  }

  isEnded(event: NurdorEvent): boolean {
    return !event.isStarted && event.eventName.trim().includes("END_EVENT");
  }

  isAdmin() {
    return this.tokenService.extractRole() === "ROLE_ADMIN";
  }

  private loadEvents() {
    // shows that fork join is deprecated, but it's just because of Observable<Object> return type
    forkJoin({
      eventsResult: this.eventService.getAllEvents(),
      logsResult: this.eventsLogService.getAllEventsLogs()
    }).subscribe({
      next: ({eventsResult, logsResult}) => { this.filterResults(eventsResult, logsResult) },
      error: (err: HttpErrorResponse)=> { this.handleResponse("Error", err.error.message); }
    })
  }

  private handleResponse(title: string, message: string) {
    this.mbService.show({
      title: title as "Success" | "Error",
      redirectTo: "",
      message: message
    });
  }

  private filterResults(eventsResult: any, logsResult: any) {
    let eventsDtos = eventsResult as Array<EventDto> | null;
    let logsDtos = logsResult as Array<EventsLogDto> | null;
    if(eventsDtos && logsDtos) {
        this.filterEvents(eventsDtos, logsDtos);
    }
    this.areEventsLoaded.set(true);
    this.loadClassValues();
  }

  private loadClassValues() {
    this.expansionFlags = new Array<boolean>(this.events().length);
    for(let i = 0; i < this.expansionFlags.length; i++) {
      this.expansionFlags[i] = false;
    }
  }

  private filterEvents(eventsDtos: Array<EventDto>, logsDtos: Array<EventsLogDto>) {
    let eventIds = this.extractEventsIds(eventsDtos, logsDtos);
    console.log(`logged volunteer id: ${this.volunteerId}\nevent ids filtered: ${eventIds}`);
    this.events.set(this.filterAndMapEventsDtos(eventsDtos, eventIds));
  }

  private extractEventsIds(eventsDtos: Array<EventDto>, logsDtos: Array<EventsLogDto>): number[] {
    let pickedEventsIds = logsDtos
      .filter(dto => dto?.volunteer === this.volunteerId)
      .map(dto => dto?.event);

    if(this.showPickedOnes()) {
      return pickedEventsIds;
    } else {
      return eventsDtos.filter(e => !pickedEventsIds.includes(e.id)).map(e => e.id);
    }
  }

  private filterAndMapEventsDtos(eventsDtos: Array<EventDto>, eventIds: Array<number>): NurdorEvent[] {
    return eventsDtos.filter(e => {
      return this.searchFilter().searchBoxInput === ""
        ? eventIds.includes(e.id)
        : eventIds.includes(e.id) && this.matchExists(e);
      })
      .map(e => new NurdorEvent(
        e.id, e.eventName, e.description, e.isStarted == 1,
        e.startTime, e.endTime, e.latitude, e.longitude,
        e.locationDesc, e.eventImg, e.totalDonations, e.city
      ))
      .sort((a, b) => this.compareEvents(a, b));
  }

  private matchExists(e: EventDto){
    let input = this.searchFilter().searchBoxInput.toLowerCase();
    return e.eventName.toLowerCase().includes(input)
      || e.startTime.includes(input)
      || e.locationDesc.includes(input);
  }

  private compareEvents(e1: NurdorEvent, e2: NurdorEvent): number {
    let sortBy = this.searchFilter().sortBy;
    let ascending = this.searchFilter().sortDirection === "asc";
    if(sortBy === "city") {
      return ascending? e1.city.localeCompare(e2.city) : e2.city.localeCompare(e1.city);
    } else if (sortBy === "name") {
      return ascending? e1.eventName.localeCompare(e2.eventName) : e2.eventName.localeCompare(e1.eventName);
    } else if (sortBy === "date") {
      return ascending
        ? new Date(e1.startTime).getTime() - new Date(e2.startTime).getTime()
        : new Date(e2.startTime).getTime() - new Date(e1.startTime).getTime();
    } else {
      return ascending? e1.id - e2.id : e2.id - e1.id;
    }
  }
}
