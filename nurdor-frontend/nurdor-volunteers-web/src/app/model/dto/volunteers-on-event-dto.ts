import {EventDto} from './event-dto';
import {VolunteerDto} from './volunteer-dto';

export interface VolunteersOnEventDto {
  eventDto: EventDto;
  volunteers: Array<VolunteerDto>;
}
