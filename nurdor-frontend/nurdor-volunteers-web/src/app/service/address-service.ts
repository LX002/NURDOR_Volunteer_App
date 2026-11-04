import { Injectable } from '@angular/core';
import {MessageBoxService} from './message-box-service';

@Injectable({
  providedIn: 'root',
})
export class AddressService {

  constructor(private mbService: MessageBoxService) { }

  async fetchAddress(url: string): Promise<string> {
    try {
      let res = await fetch(url);
      let data = await res.json();
      return this.formatAddress(data);
    } catch (err) {
      this.mbService.show({message: "Error during fetching of address" + err, title: "Error", redirectTo: ""});
      return "";
    }
  }

  private formatAddress(data: any): string {
    if(!data || !data.address) return data?.display_name;

    let a = data.address;
    let place = a.amenity || a.building || a.leisure || a.tourism || a.shop || a.historic;
    let street = a.road || a.pedestrian || a.footway || a.square || a.suburb;
    let houseNumber = a.house_number ? `${a.house_number}` : ``;
    let city = a.city || a.town || a.village || a.municipality

    if(place && city) { return `${place} ${city}`; }
    if(street) { return city? `${street} ${houseNumber}, ${city}` : `${street} ${houseNumber}`; }
    if(city) { return city; }

    return data.display_name.split(',').slice(0, 2).join(', ').trim();
  }
}
