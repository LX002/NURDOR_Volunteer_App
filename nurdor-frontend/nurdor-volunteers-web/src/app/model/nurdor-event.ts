export class NurdorEvent {
  private _id: number;
  private _eventName: string;
  private _description: string;
  private _isStarted: boolean;
  private _startTime: string;
  private _endTime: string;
  private _latitude: number;
  private _longitude: number;
  private _locationDesc: string;
  private _eventImg: string;
  private _totalDonations: number;
  private _city: string; // zipCode fk

  constructor(id: number, eventName: string, description: string, isStarted: boolean, startTime: string, endTime: string, latitude: number, longitude: number, locationDesc: string, eventImg: string, totalDonations: number, city: string) {
    this._id = id;
    this._eventName = eventName;
    this._description = description;
    this._isStarted = isStarted;
    this._startTime = startTime;
    this._endTime = endTime;
    this._latitude = latitude;
    this._longitude = longitude;
    this._locationDesc = locationDesc;
    this._eventImg = eventImg;
    this._totalDonations = totalDonations;
    this._city = city;
  }

  get id(): number { return this._id; }
  get eventName(): string { return this._eventName; }
  get description(): string { return this._description; }
  get isStarted(): boolean { return this._isStarted; }
  get startTime(): string { return this._startTime; }
  get endTime(): string { return this._endTime; }
  get latitude(): number { return this._latitude; }
  get longitude(): number { return this._longitude; }
  get locationDesc(): string { return this._locationDesc; }
  get eventImg(): string { return this._eventImg; }
  get totalDonations(): number { return this._totalDonations; }
  get city(): string { return this._city; }
}
