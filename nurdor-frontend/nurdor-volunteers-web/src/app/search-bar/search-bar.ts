import {Component, computed, input, OnInit, output} from '@angular/core';
import {KeyValuePipe} from '@angular/common';
import {SearchFilter} from '../model/filters/search-filter';
import {FormBuilder, FormGroup, ReactiveFormsModule} from '@angular/forms';

@Component({
  selector: 'app-search-bar',
  imports: [
    KeyValuePipe,
    ReactiveFormsModule
  ],
  templateUrl: './search-bar.html',
  styleUrl: './search-bar.css',
})
export class SearchBar implements OnInit {

  searchFormGroup!: FormGroup;

  private searchFilter: SearchFilter = {
    searchBoxInput: "",
    sortBy: "default",
    sortDirection: "asc"
  };

  entityName = input<string>("");
  onSearch = output<SearchFilter>();

  sortByOptions = computed(() => {
    return this.entityName() === "Event"
      ? new Map<string, string>([
        ["default-asc", "Default"],
        ["name-asc", "Event name ascending"],
        ["name-desc", "Event name descending"],
        ["date-asc", "Event start date ascending"],
        ["date-desc", "Event start date descending"],
        ["name-asc", "Event name ascending"],
        ["name-desc", "Event name descending"],
        ["city-asc", "Event city (zip code) ascending"],
        ["city-desc", "Event city (zip code) descending"]
      ])
      : new Map<string, string>([
        ["default-asc", "Default"],
        ["name-asc", "Volunteer name ascending"],
        ["name-desc", "Volunteer name descending"],
        ["surname-asc", "Volunteer surname ascending"],
        ["surname-desc", "Volunteer surname descending"],
        ["username-asc", "Username ascending"],
        ["username-desc", "Username descending"],
      ]);
  });

  originalOrder = () => 0;

  constructor(private fb: FormBuilder) { }

  ngOnInit() {
    this.searchFormGroup = this.fb.group({
      searchInput: this.fb.control("")
    });
  }

  setSortType(type: string) {
    let sortBy = type.split("-")[0];
    let direction = type.split("-")[1];
    this.searchFilter.sortBy = sortBy as "name" | "date" | "city" | "surname" | "username" | "default";
    this.searchFilter.sortDirection = direction as "asc" | "desc";
  }

  applySearchFilter() {
    console.log(`Search filter applied: ${this.searchFilter.sortBy}`);
    this.searchFilter.searchBoxInput = this.searchFormGroup.value.searchInput.toString().toLowerCase();
    this.onSearch.emit({ ...this.searchFilter });
  }
}
