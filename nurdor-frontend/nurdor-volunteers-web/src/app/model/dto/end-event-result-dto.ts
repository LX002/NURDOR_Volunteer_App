import {StandDto} from './stand-dto';

export interface EndEventResultDto {
  message: string;
  totalDonations: number;
  stands: Array<StandDto>;
}
