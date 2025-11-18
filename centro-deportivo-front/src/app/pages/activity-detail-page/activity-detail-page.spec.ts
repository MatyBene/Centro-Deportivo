import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ActivityDetailPage } from './activity-detail-page';

describe('ActivityDetailPage', () => {
  let component: ActivityDetailPage;
  let fixture: ComponentFixture<ActivityDetailPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ActivityDetailPage]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ActivityDetailPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
