import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserSummaryItem } from './user-summary-item';

describe('UserSummaryItem', () => {
  let component: UserSummaryItem;
  let fixture: ComponentFixture<UserSummaryItem>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserSummaryItem]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UserSummaryItem);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
