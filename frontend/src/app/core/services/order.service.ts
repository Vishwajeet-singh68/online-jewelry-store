import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Order, CreateOrderRequest, PageResponse } from '../models/models';

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private readonly apiUrl = `${environment.apiUrl}/api/v1`;

  constructor(private http: HttpClient) {}

  createOrder(request: CreateOrderRequest, idempotencyKey?: string): Observable<Order> {
    let headers = new HttpHeaders();
    if (idempotencyKey) {
      headers = headers.set('Idempotency-Key', idempotencyKey);
    }
    return this.http.post<Order>(`${this.apiUrl}/orders`, request, { headers });
  }

  getUserOrders(page: number = 0, size: number = 20, status?: string): Observable<PageResponse<Order>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    if (status) {
      params = params.set('status', status);
    }
    return this.http.get<PageResponse<Order>>(`${this.apiUrl}/orders`, { params });
  }

  getOrderById(id: number): Observable<Order> {
    return this.http.get<Order>(`${this.apiUrl}/orders/${id}`);
  }

  cancelOrder(id: number): Observable<Order> {
    return this.http.post<Order>(`${this.apiUrl}/orders/${id}/cancel`, {});
  }

  // Admin APIs
  getAdminOrders(page: number = 0, size: number = 20, status?: string): Observable<PageResponse<Order>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    if (status) {
      params = params.set('status', status);
    }
    return this.http.get<PageResponse<Order>>(`${this.apiUrl}/admin/orders`, { params });
  }

  updateOrderStatus(orderId: number, status: string): Observable<Order> {
    return this.http.patch<Order>(`${this.apiUrl}/admin/orders/${orderId}/status`, { status });
  }
}
