// LoginRequest DTO Interface
export interface ILoginRequest {
  email: string;
  password: string;
}

// LoginResponse DTO Interface
export interface ILoginResponse {
  token: string;
}