import { Component, input, output } from '@angular/core';

@Component({
  selector: 'app-pagination',
  imports: [],
  templateUrl: './pagination.html',
  styleUrl: './pagination.css'
})
export class Pagination {
  currentPage = input<number>(0);
  totalPages = input<number>();
  hasPreviousPage = input<boolean>();
  hasNextPage = input<boolean>();

  previousPageEvent = output<void>();
  nextPageEvent = output<void>();
  onPreviousPage() {
    this.previousPageEvent.emit();
  }

  onNextPage() {
    this.nextPageEvent.emit();
  }

}
