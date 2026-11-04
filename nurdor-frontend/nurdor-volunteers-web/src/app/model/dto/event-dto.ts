export interface EventDto {
  id: number;
  eventName: string;
  description: string;
  isStarted: number;
  startTime: string;
  endTime: string;
  latitude: number;
  longitude: number;
  locationDesc: string;
  eventImg: string;
  totalDonations: number;
  city: string;
}
