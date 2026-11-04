export interface AuthResponse<T> {
  message: string;
  status: string;
  data: T;
}
