import {Component, signal} from '@angular/core';
import {EventList} from '../events/event-list/event-list';
import {SearchBar} from '../search-bar/search-bar';
import {SearchFilter} from '../model/filters/search-filter';

@Component({
  selector: 'app-home-page',
  imports: [
    EventList,
    SearchBar
  ],
  templateUrl: './home-page.html',
  styleUrl: './home-page.css',
  standalone: true
})
export class HomePage {

  searchFilter = signal<SearchFilter>({
    searchBoxInput: "",
    sortBy: "default",
    sortDirection: "asc"
  });

  onSearchFilterChange(sf: SearchFilter) {
    this.searchFilter.set(sf);
  }
}
