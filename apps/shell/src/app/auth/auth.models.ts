export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  email: string;
  role: string;
  expiresIn: number;
}

export interface AuthUser {
  email: string;
  role: string;
}
