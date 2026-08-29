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
  timeError = signal(false);

  onStartTimeChange(value: string) {
    this.startTime.set(value);
    this.timeError.set(false);
  }

  onEndTimeChange(value: string) {
    this.endTime.set(value);
    this.timeError.set(false);
  }

  onSearch() {
    if(!this.startTime() || !this.endTime()) {
      return;
    }
    if(this.startTime() >= this.endTime()) {
      this.timeError.set(true);
      return;
    }
    this.timeError.set(false);
    this.searchEvent.emit({
      startTime: this.startTime(),
      endTime: this.endTime()
    });
  }
}
