import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Inventory } from '../models/models';

@Injectable({
  providedIn: 'root'
})
export class InventoryService {
  private readonly apiUrl = `${environment.apiUrl}/api/v1`;

  constructor(private http: HttpClient) {}

  getInventoryByProductId(productId: number): Observable<Inventory> {
    return this.http.get<Inventory>(`${this.apiUrl}/inventory/${productId}`);
  }

  // Admin APIs
  getAdminInventory(): Observable<Inventory[]> {
    return this.http.get<Inventory[]>(`${this.apiUrl}/admin/inventory`);
  }

  adjustStock(productId: number, adjustment: { quantity: number; reason: string }): Observable<Inventory> {
    return this.http.patch<Inventory>(`${this.apiUrl}/admin/inventory/${productId}/adjust`, adjustment);
  }
}
