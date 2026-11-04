import {Component, effect, ElementRef, OnInit, signal, viewChild} from '@angular/core';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {RouterLink} from "@angular/router";
import {CityDto} from '../model/dto/city-dto';
import {EventsService} from '../service/events-service';
import {MessageBoxService} from '../service/message-box-service';
import {HttpErrorResponse} from '@angular/common/http';
import {VolunteerService} from '../service/volunteer-service';
import {CreateEventDto} from '../model/dto/create-event-dto';
import * as L from 'leaflet';
import {Icon, IconOptions, LeafletMouseEvent} from 'leaflet';
import {AddressService} from '../service/address-service';

interface LocationData {
  lat: number,
  lng: number,
  dsc: string
}

@Component({
  selector: 'app-create-event-form',
    imports: [
        FormsModule,
        ReactiveFormsModule,
    ],
  templateUrl: './create-event-form.html',
  styleUrl: './create-event-form.css',
})
export class CreateEventForm implements OnInit {

  createEventFormGroup!: FormGroup;
  eventPicture = signal<string>("");
  cities = signal<Array<CityDto>>([]);
  zipCode = signal<string>("");
  mapContainer = viewChild<ElementRef<HTMLDivElement>>("pickLocationMap");
  location = signal<LocationData>({
    lat: 45.267136,
    lng: 19.833549,
    dsc: "Novi Sad"
  });
  map!: L.Map;
  pin!: L.Marker;

  constructor(
    private eventService: EventsService,
    private volunteerService: VolunteerService,
    private formBuilder: FormBuilder,
    private mbService: MessageBoxService,
    private adressService: AddressService
  ) {
    effect(() => {
      let mc = this.mapContainer()?.nativeElement;
      if(mc && !this.map) {
        this.setUpTheMap(mc);
      }
    });
  }

  ngOnInit() {
    this.createForm();
    this.fetchCities();
  }

  private fetchCities() {
    this.volunteerService.getCities().subscribe({
      next: (response: any) => {
        let cities = response as Array<CityDto>;
        this.cities.set(cities);
      },
      error: (err: HttpErrorResponse) => {
        console.error(`Error during fetching cities: ${err.message}`)
      }
    });
  }

  private createForm() {
    this.createEventFormGroup = this.formBuilder.group({
      eventName: this.formBuilder.control("", [Validators.required]),
      description: this.formBuilder.control("", [Validators.required]),
      startTime: this.formBuilder.control("", [Validators.required]),
      endTime: this.formBuilder.control("", [Validators.required]),
    });
  }

  private setUpTheMap(mc: HTMLDivElement) {
    let customIcon = this.getCustomIcon();
    this.map = L.map(mc).setView([45.267136, 19.833549], 10);
    L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {attribution: "@ OpenStreetMap contributors"})
      .addTo(this.map);
    this.map.on("click", (e: LeafletMouseEvent) => this.placePin(e, customIcon));
  }

  private async placePin(e: LeafletMouseEvent, customIcon: Icon<IconOptions>) {
    let lat = parseFloat(e.latlng.lat.toFixed(7));
    let lng = parseFloat(e.latlng.lng.toFixed(7));
    if (this.pin) {
      this.pin.setLatLng([lat, lng]);
    } else {
      this.pin = L.marker([lat, lng], {icon: customIcon}).addTo(this.map);
    }

    let url = `https://nominatim.openstreetmap.org/reverse?format=jsonv2&lat=${lat}&lon=${lng}`;
    let dsc = await this.adressService.fetchAddress(url);
    this.location.set({lat, lng, dsc});
  }

  private getCustomIcon() {
    return L.icon({
      iconUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png',
      iconRetinaUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png',
      shadowUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png',
      iconSize: [25, 41],
      iconAnchor: [12, 41],
      popupAnchor: [1, -34],
      shadowSize: [41, 41]
    });
  }



  private fileToBase64(file: File): Promise<string> {
    return new Promise((resolve, reject) => {
      let reader = new FileReader();
      reader.readAsDataURL(file);
      reader.onload = () => resolve(reader.result as string);
      reader.onerror = (error) => reject(error);
    })
  }

  private getFormData(): CreateEventDto | null {
    let f = this.createEventFormGroup.value;
    return {
      eventName: f.eventName,
      description: f.description,
      isStarted: 0,
      startTime: f.startTime + " ANGULAR_SOURCE",
      endTime: f.endTime + " ANGULAR_SOURCE",
      latitude: this.location().lat,
      longitude: this.location().lng,
      eventImg: this.eventPicture(),
      locationDesc: this.location().dsc,
      totalDonations: 0,
      city: this.zipCode()
    }
  }

  onCitySelectionChange(event: Event) {
    let citySelection = event.target as HTMLSelectElement;
    this.zipCode.set(citySelection.value);
  }

  async onEventPhotoChange(event: Event) {
    let validTypes = ["image/png", "image/jpeg"];
    let fileSelection = event.target as HTMLInputElement;
    if(fileSelection.files && fileSelection.files.length > 0) {
      let picture = fileSelection.files[0];
      if(validTypes.includes(picture.type)) {
        let base64Picture = await this.fileToBase64(picture);
        this.eventPicture.set(base64Picture.split(",")[1]);
      } else {
        console.error("Error during picking of image: invalid file type!")
      }
    }
  }

  createEvent() {
    let data = this.getFormData();
    if(data) {
      this.eventService.createEvent(data).subscribe({
        next: () => {
          this.mbService.show({ message: "Event created successfully!", redirectTo: "", title: "Success" });
        }, error: (err: HttpErrorResponse) => {
          this.mbService.show({ message: err.error.message, redirectTo: "", title: "Error" });
        }
      })
    }
  }

}

