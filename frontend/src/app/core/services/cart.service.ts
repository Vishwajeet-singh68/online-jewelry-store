import { Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Cart, AddCartItemRequest, UpdateCartItemRequest, CartValidationResponse } from '../models/models';

@Injectable({
  providedIn: 'root'
})
export class CartService {
  private readonly apiUrl = `${environment.apiUrl}/api/v1/cart`;

  // Signals for global reactive state
  cart = signal<Cart | null>(null);
  totalItems = computed(() => {
    const currentCart = this.cart();
    if (!currentCart || !currentCart.items) return 0;
    return currentCart.items.reduce((acc, item) => acc + item.quantity, 0);
  });
  totalAmount = computed(() => this.cart()?.totalAmount || 0);

  constructor(private http: HttpClient) {}

  loadCart(): Observable<Cart> {
    return this.http.get<Cart>(this.apiUrl).pipe(
      tap(c => this.cart.set(c))
    );
  }

  addItem(productId: number, quantity: number = 1): Observable<Cart> {
    const payload: AddCartItemRequest = { productId, quantity };
    return this.http.post<Cart>(`${this.apiUrl}/items`, payload).pipe(
      tap(c => this.cart.set(c))
    );
  }

  updateItem(itemId: number, quantity: number): Observable<Cart> {
    const payload: UpdateCartItemRequest = { quantity };
    return this.http.put<Cart>(`${this.apiUrl}/items/${itemId}`, payload).pipe(
      tap(c => this.cart.set(c))
    );
  }

  removeItem(itemId: number): Observable<Cart> {
    return this.http.delete<Cart>(`${this.apiUrl}/items/${itemId}`).pipe(
      tap(c => this.cart.set(c))
    );
  }

  clearCart(): Observable<void> {
    return this.http.delete<void>(this.apiUrl).pipe(
      tap(() => this.cart.set(null))
    );
  }

  validateCart(): Observable<CartValidationResponse> {
    return this.http.get<CartValidationResponse>(`${this.apiUrl}/validate`);
  }
}
