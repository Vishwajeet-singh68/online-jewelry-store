import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-footer',
  standalone: true,
  imports: [CommonModule],
  template: `
    <footer style="background:#161616;color:rgba(255,255,255,0.75);padding:5rem 0 2rem;">
      <div class="container">
        <div class="row g-5 mb-5">

          <!-- Brand -->
          <div class="col-lg-4">
            <div class="d-flex align-items-center gap-2 mb-4">
              <div style="width:40px;height:40px;background:linear-gradient(135deg,var(--primary-gold),#e0b84a);border-radius:12px;display:flex;align-items:center;justify-content:center;color:#fff;font-size:1.1rem;">
                <i class="fa fa-gem"></i>
              </div>
              <span style="font-family:'Cinzel',serif;font-size:1.2rem;font-weight:700;color:#e8d9b5;letter-spacing:2px;">LUXE JEWELS</span>
            </div>
            <p style="color:rgba(255,255,255,0.5);line-height:1.8;font-size:0.88rem;max-width:280px;">
              Handcrafted timeless elegance. Discover our exclusive high-end jewelry collections designed with unmatched purity, passion and perfection.
            </p>
            <div class="d-flex gap-3 mt-4">
              <a href="#" class="social-link"><i class="fab fa-instagram"></i></a>
              <a href="#" class="social-link"><i class="fab fa-pinterest"></i></a>
              <a href="#" class="social-link"><i class="fab fa-facebook-f"></i></a>
              <a href="#" class="social-link"><i class="fab fa-youtube"></i></a>
            </div>
          </div>

          <!-- Collections -->
          <div class="col-6 col-lg-2">
            <h6 class="footer-heading mb-4">Collections</h6>
            <ul class="list-unstyled footer-links">
              <li><a href="/products">Diamond Rings</a></li>
              <li><a href="/products">Gold Necklaces</a></li>
              <li><a href="/products">Bracelets</a></li>
              <li><a href="/products">Earrings</a></li>
              <li><a href="/products">Pendants</a></li>
            </ul>
          </div>

          <!-- Customer -->
          <div class="col-6 col-lg-3">
            <h6 class="footer-heading mb-4">Customer Care</h6>
            <ul class="list-unstyled footer-links">
              <li><span>Authenticity Certificate</span></li>
              <li><span>Secure Checkout</span></li>
              <li><span>Insured Shipping</span></li>
              <li><span>30-Day Returns</span></li>
              <li><span>Lifetime Warranty</span></li>
            </ul>
          </div>

          <!-- Contact -->
          <div class="col-lg-3">
            <h6 class="footer-heading mb-4">Get In Touch</h6>
            <div class="footer-contact">
              <div class="d-flex align-items-start gap-3 mb-3">
                <i class="fa fa-envelope mt-1" style="color:var(--primary-gold);"></i>
                <span>concierge&#64;luxejewels.com</span>
              </div>
              <div class="d-flex align-items-start gap-3 mb-3">
                <i class="fa fa-phone mt-1" style="color:var(--primary-gold);"></i>
                <span>+91 800 589 3539</span>
              </div>
              <div class="d-flex align-items-start gap-3">
                <i class="fa fa-map-marker-alt mt-1" style="color:var(--primary-gold);"></i>
                <span>Mumbai &bull; New Delhi &bull; London</span>
              </div>
            </div>
          </div>
        </div>

        <div style="height:1px;background:linear-gradient(90deg,transparent,rgba(201,162,39,0.3),transparent);margin-bottom:1.5rem;"></div>

        <div class="d-flex flex-column flex-md-row justify-content-between align-items-center gap-3">
          <span style="font-size:0.8rem;color:rgba(255,255,255,0.4);">
            &copy; 2026 LUXE JEWELS. All Rights Reserved.
          </span>
          <div class="d-flex gap-4" style="font-size:0.8rem;color:rgba(255,255,255,0.4);">
            <a href="#" style="color:rgba(255,255,255,0.4);text-decoration:none;">Privacy Policy</a>
            <a href="#" style="color:rgba(255,255,255,0.4);text-decoration:none;">Terms of Service</a>
            <a href="#" style="color:rgba(255,255,255,0.4);text-decoration:none;">Cookie Policy</a>
          </div>
        </div>
      </div>
    </footer>
  `,
  styles: [`
    .footer-heading {
      font-size: 0.72rem;
      font-weight: 700;
      letter-spacing: 0.15em;
      text-transform: uppercase;
      color: var(--champagne);
    }
    .footer-links { font-size: 0.85rem; }
    .footer-links li { margin-bottom: 0.6rem; }
    .footer-links a, .footer-links span {
      color: rgba(255,255,255,0.5);
      text-decoration: none;
      transition: color 0.2s;
    }
    .footer-links a:hover { color: var(--primary-gold); }
    .footer-contact { font-size: 0.85rem; color: rgba(255,255,255,0.5); }
    .social-link {
      width: 38px; height: 38px;
      background: rgba(255,255,255,0.06);
      border: 1px solid rgba(255,255,255,0.1);
      border-radius: 10px;
      display: flex; align-items: center; justify-content: center;
      color: rgba(255,255,255,0.5);
      text-decoration: none;
      transition: all 0.2s;
      font-size: 0.9rem;
    }
    .social-link:hover {
      background: var(--primary-gold);
      border-color: var(--primary-gold);
      color: #fff;
      transform: translateY(-2px);
    }
  `]
})
export class FooterComponent {}
