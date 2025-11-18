import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TimeRangeSearchBox } from './time-range-search-box';

describe('TimeRangeSearchBox', () => {
  let component: TimeRangeSearchBox;
  let fixture: ComponentFixture<TimeRangeSearchBox>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TimeRangeSearchBox]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TimeRangeSearchBox);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
