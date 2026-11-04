export interface SearchFilter {
  searchBoxInput: string,
  sortBy: "name" | "date" | "city" | "surname" | "username" | "default",
  sortDirection: "asc" | "desc"
}
