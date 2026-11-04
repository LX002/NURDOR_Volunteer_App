export interface CreateEventDto {
  eventName: string;
  description: string;
  isStarted: number;
  startTime: string;
  endTime: string;
  latitude: number;
  longitude: number;
  eventImg: string;
  locationDesc: string;
  totalDonations: number;
  city: string;
}
