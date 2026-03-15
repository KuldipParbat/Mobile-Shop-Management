export interface LoginRequest {
  email:    string;
  password: string;
}

export interface LoginResponse {
  token:       string;
  name:        string;
  email:       string;
  role:        string;
  tenantId:    string;
  shopName:    string;
  shopAddress: string;
  shopPhone:   string;
}

export interface AuthUser {
  token:       string;
  name:        string;
  email:       string;
  role:        string;
  tenantId:    string;
  shopName:    string;
  shopAddress: string;
  shopPhone:   string;
}
