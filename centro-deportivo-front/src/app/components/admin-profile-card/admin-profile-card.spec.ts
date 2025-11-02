import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminProfileCard } from './admin-profile-card';

describe('AdminProfileCard', () => {
  let component: AdminProfileCard;
  let fixture: ComponentFixture<AdminProfileCard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminProfileCard]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminProfileCard);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
