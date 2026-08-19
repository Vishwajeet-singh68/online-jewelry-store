export interface User {
  id: number;
  name: string;
  email: string;
  role: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
  user: User;
}

export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RefreshTokenRequest {
  refreshToken: string;
}

export interface Product {
  id: number;
  sku: string;
  name: string;
  description: string;
  price: number;
  discountPrice?: number;
  finalPrice: number;
  categoryId: number;
  categoryName: string;
  primaryImageUrl?: string;
  imageUrls?: string[];
  status: string;
}

export interface Category {
  id: number;
  name: string;
  description?: string;
  imageUrl?: string;
  status?: string;
}

export interface Inventory {
  id?: number;
  productId: number;
  sku: string;
  availableQuantity: number;
  reservedQuantity: number;
  totalQuantity?: number;
  soldQuantity?: number;
  stockStatus: 'IN_STOCK' | 'LOW_STOCK' | 'OUT_OF_STOCK';
  isAvailable: boolean;
}

export interface CartItem {
  id: number;
  productId: number;
  productName: string;
  productSku: string;
  unitPrice: number;
  quantity: number;
  subtotal: number;
  primaryImageUrl?: string;
}

export interface Cart {
  id: number;
  userId: number;
  items: CartItem[];
  totalAmount: number;
  totalItems: number;
}

export interface AddCartItemRequest {
  productId: number;
  quantity: number;
}

export interface UpdateCartItemRequest {
  quantity: number;
}

export interface CartValidationError {
  productId: number;
  message: string;
}

export interface CartValidationResponse {
  valid: boolean;
  errors: CartValidationError[];
}

export interface ShippingAddress {
  fullName: string;
  phoneNumber: string;
  addressLine1: string;
  addressLine2?: string;
  city: string;
  state: string;
  postalCode: string;
  country: string;
}

export interface CreateOrderRequest {
  shippingAddress: ShippingAddress;
}

export interface OrderItem {
  id: number;
  productId: number;
  productName: string;
  productSku: string;
  unitPrice: number;
  quantity: number;
  subtotal: number;
}

export interface Order {
  id: number;
  orderNumber: string;
  userId: number;
  status: 'PENDING' | 'CONFIRMED' | 'PROCESSING' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED';
  paymentStatus: 'PENDING' | 'PAID' | 'FAILED' | 'REFUNDED';
  totalAmount: number;
  items: OrderItem[];
  shippingAddress: ShippingAddress;
  createdAt: string;
  updatedAt?: string;
}

export interface PageResponse<T> {
  content: T[];
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}

export interface ApiError {
  timestamp?: string;
  status?: number;
  error?: string;
  message?: string;
  path?: string;
}
