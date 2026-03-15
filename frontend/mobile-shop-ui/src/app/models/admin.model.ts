export interface AdminLoginRequest {
  email:    string;
  password: string;
}

export interface AdminLoginResponse {
  token: string;
  name:  string;
  email: string;
  role:  string;
}

export interface TenantStats {
  id:            number;
  tenantId:      string;
  shopName:      string;
  ownerName:     string;
  email:         string;
  phone:         string;
  city:          string;
  state:         string;
  gstNumber:     string;
  active:        boolean;
  totalUsers:    number;
  totalProducts: number;
  totalSales:    number;
}

export interface CreateTenantForm {
  shopName:   string;
  ownerName:  string;
  email:      string;
  password:   string;
  phone:      string;
  address:    string;
  city:       string;
  state:      string;
  pincode:    string;
  gstNumber:  string;
}

export interface TenantUser {
  id:       number;
  name:     string;
  email:    string;
  role:     string;
  tenantId: string;
  active:   boolean;
}
