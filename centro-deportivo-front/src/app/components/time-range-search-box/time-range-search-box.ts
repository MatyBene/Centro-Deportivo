import { Component, output, signal } from '@angular/core';

@Component({
  selector: 'app-time-range-search-box',
  imports: [],
  templateUrl: './time-range-search-box.html',
  styleUrl: './time-range-search-box.css'
})
export class TimeRangeSearchBox {
  searchEvent = output<{startTime: string, endTime: string}>();
  startTime = signal('07:00');
  endTime = signal('22:00');

  onSearch() {
    if(this.startTime() && this.endTime()) {
      this.searchEvent.emit({
        startTime: this.startTime(),
        endTime: this.endTime()
      })
    }
  }
}
