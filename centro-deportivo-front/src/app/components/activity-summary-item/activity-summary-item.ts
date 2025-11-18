import { Component, input } from '@angular/core';
import SportActivitySummary from '../../models/SportActivitySummary';
import { Router } from '@angular/router';

@Component({
  selector: 'app-activity-summary-item',
  imports: [],
  templateUrl: './activity-summary-item.html',
  styleUrl: './activity-summary-item.css'
})
export class ActivitySummaryItem {
  activity = input<SportActivitySummary>();

  constructor(private router: Router) {};

  goToDetail() {
    this.router.navigate(['/activity-list', this.activity()?.id]);
  }
}
