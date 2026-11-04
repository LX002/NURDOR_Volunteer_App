import {Component, computed, effect, input, OnInit, signal} from '@angular/core';
import {NurdorEvent} from '../../model/nurdor-event';
import {EventsLogService} from '../../service/events-log-service';
import {HttpErrorResponse} from '@angular/common/http';
import {Router} from '@angular/router';
import {EventsService} from '../../service/events-service';
import {EventDto} from '../../model/dto/event-dto';
import {StatisticsService} from '../../service/statistics-service';
import {forkJoin} from 'rxjs';
import {VolunteersOnEventDto} from '../../model/dto/volunteers-on-event-dto';
import {VolunteerDto} from '../../model/dto/volunteer-dto';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {DonationsService} from '../../service/donations-service';
import {StandDto} from '../../model/dto/stand-dto';
import {DonateForm} from '../../donate-form/donate-form';
import {MessageBoxService} from '../../service/message-box-service';
import {LoadingScreen} from '../../loading-screen/loading-screen';

@Component({
  selector: 'app-started-event-page',
  imports: [
    FormsModule,
    ReactiveFormsModule,
    DonateForm,
    LoadingScreen
  ],
  templateUrl: './started-event-page.html',
  styleUrl: './started-event-page.css',
})
export class StartedEventPage {

  private volunteerId = sessionStorage.getItem("VOLUNTEER_ID");
  id = input.required<string>();
  event = signal<NurdorEvent | null>(null);
  volunteers = signal<Array<VolunteerDto> | null>(null);
  standIds = signal<number[]>([]);

  loadedData = computed(() => {
    return this.event() !== null && this.volunteers() !== null && this.standIds().length !== 0;
  });

  constructor(
    private eventsLogService: EventsLogService,
    private eventsService: EventsService,
    private statisticsService: StatisticsService,
    private donationsService: DonationsService,
    private mbService: MessageBoxService,
    private router: Router
  ) {
    effect(() => {
      let eventId = parseInt(this.id());
      if(!isNaN(eventId)) {
        this.updatePresence(true);
        this.loadContent();
      }
    });
  }

  // ngOnInit() {
  //   this.updatePresence(true);
  //   this.loadContent();
  // }

  private loadContent() {
    let eventId = parseInt(this.id());
    forkJoin({
      eventResponse: this.eventsService.getEvent(eventId),
      volunteersOnEventResponse: this.statisticsService.getVolunteersOnEvent(eventId),
      standsResponse: this.donationsService.getEventTakenStands(eventId)
    }).subscribe({
      next: ({eventResponse, volunteersOnEventResponse, standsResponse})=> {
        let eventDto = eventResponse as EventDto;
        let volunteersOnEventDto = volunteersOnEventResponse as VolunteersOnEventDto;
        let standsDtos = standsResponse as Array<StandDto>;
        this.event.set(new NurdorEvent(
          eventDto.id, eventDto.eventName, eventDto.description, eventDto.isStarted == 1,
          eventDto.startTime, eventDto.endTime, eventDto.latitude, eventDto.longitude,
          eventDto.locationDesc, eventDto.eventImg, eventDto.totalDonations, eventDto.city
        ));
        this.volunteers.set(volunteersOnEventDto.volunteers);
        this.standIds.set(standsDtos.map(dto => dto.id));
        console.log(standsDtos);
      }, error: (err: HttpErrorResponse) => {
        console.error(`Error during loading event => ${err.message}`);
      }
    });
  }

  updatePresence(present: boolean) {
    let volId = this.volunteerId;
    if(volId) {
      this.eventsLogService.updatePresence({
        event: parseInt(this.id()),
        isPresent: present ? 1 : 0,
        note: 'initLog',
        volunteer: parseInt(volId)
      }).subscribe({
        next: () => {
          //if (!present) this.router.navigate(['/']);
          if(!present) {
            this.handleResponse("Success", `Left event [ID: ${this.id()}]`, "/");
          }
        },
        error: (err: HttpErrorResponse) => {
          this.handleResponse("Error", `Error during leaving event [ID: ${this.id()}] - ${err.message}`, "");
        }
      })
    }
  }

  private handleResponse(type: "Success" | "Error" | "Confirmation", message: string, redirectTo: string) {
    this.mbService.show({
      message: message,
      redirectTo: redirectTo,
      title: type
    });
  }

  protected readonly parseInt = parseInt;
}
