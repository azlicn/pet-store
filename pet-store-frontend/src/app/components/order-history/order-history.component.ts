import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Location } from '@angular/common';
import { StoreService } from '../../services/store.service';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatDividerModule } from '@angular/material/divider';

@Component({
  selector: 'app-order-history',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatIconModule, MatButtonModule, MatDividerModule],
  templateUrl: './order-history.component.html',
  styleUrls: ['./order-history.component.scss']
})
export class OrderHistoryComponent implements OnInit {
  orderId!: number;
  order: any;
  loading = true;
  error: string | null = null;
  totalItemsPrice: number = 0;
  discountedAmount: number = 0;

  constructor(
    private route: ActivatedRoute,
    private storeService: StoreService,
    private location: Location
  ) {}

  goBack(): void {
    this.location.back();
  }

  ngOnInit(): void {
    this.orderId = +this.route.snapshot.paramMap.get('orderId')!;
    this.storeService.getOrder(this.orderId).subscribe({
      next: (order) => {
        this.order = order;
        this.loading = false;
        // Calculate total items price
        this.totalItemsPrice = Array.isArray(order.items)
          ? order.items.reduce((sum: number, item: any) => sum + (item.pet?.price || 0), 0)
          : 0;
        // Calculate discounted amount if discount exists
        if (order.discount && order.discount.percentage) {
          this.discountedAmount = this.totalItemsPrice * (order.discount.percentage / 100);
        } else {
          this.discountedAmount = 0;
        }
      },
      error: (err) => {
        this.error = 'Failed to load order details.';
        this.loading = false;
      }
    });
  }
}
