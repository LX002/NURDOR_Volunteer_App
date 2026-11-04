import {StandDto} from './stand-dto';

export interface StartEventResultDto {
  message: string,
  stands: Array<StandDto>
}
