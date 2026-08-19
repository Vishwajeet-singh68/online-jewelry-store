import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Product, Category, PageResponse } from '../models/models';

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private readonly apiUrl = `${environment.apiUrl}/api/v1`;

  constructor(private http: HttpClient) {}

  getProducts(params?: {
    page?: number;
    size?: number;
    sortBy?: string;
    sortDir?: string;
    categoryId?: number;
    search?: string;
    minPrice?: number;
    maxPrice?: number;
    inStockOnly?: boolean;
  }): Observable<PageResponse<Product>> {
    let httpParams = new HttpParams();
    if (params) {
      Object.keys(params).forEach(key => {
        const value = (params as any)[key];
        if (value !== undefined && value !== null && value !== '') {
          httpParams = httpParams.set(key, value.toString());
        }
      });
    }
    return this.http.get<PageResponse<Product>>(`${this.apiUrl}/products`, { params: httpParams });
  }

  getProductById(id: number): Observable<Product> {
    return this.http.get<Product>(`${this.apiUrl}/products/${id}`);
  }

  getCategories(): Observable<Category[]> {
    return this.http.get<Category[]>(`${this.apiUrl}/categories`);
  }

  // Admin APIs
  createProduct(productData: Partial<Product>): Observable<Product> {
    return this.http.post<Product>(`${this.apiUrl}/admin/products`, productData);
  }

  updateProduct(id: number, productData: Partial<Product>): Observable<Product> {
    return this.http.put<Product>(`${this.apiUrl}/admin/products/${id}`, productData);
  }

  updateProductStatus(id: number, status: string): Observable<Product> {
    return this.http.patch<Product>(`${this.apiUrl}/admin/products/${id}/status`, { status });
  }
}
