import {Component, signal} from '@angular/core';
import {EventList} from "../events/event-list/event-list";
import {SearchBar} from '../search-bar/search-bar';
import {SearchFilter} from '../model/filters/search-filter';

@Component({
  selector: 'app-explore-page',
  imports: [
    EventList,
    SearchBar
  ],
  templateUrl: './explore-page.html',
  styleUrl: './explore-page.css',
})
export class ExplorePage {

  searchFilter = signal<SearchFilter>({
    searchBoxInput: "",
    sortBy: "default",
    sortDirection: "asc"
  });

  onSearchFilterChange(sf: SearchFilter) {
    this.searchFilter.set(sf);
  }
}
