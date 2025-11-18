import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ActivitySummaryItem } from './activity-summary-item';

describe('ActivitySummaryItem', () => {
  let component: ActivitySummaryItem;
  let fixture: ComponentFixture<ActivitySummaryItem>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ActivitySummaryItem]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ActivitySummaryItem);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
