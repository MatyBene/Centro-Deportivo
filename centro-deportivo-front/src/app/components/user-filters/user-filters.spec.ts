import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserFilters } from './user-filters';

describe('UserFilters', () => {
  let component: UserFilters;
  let fixture: ComponentFixture<UserFilters>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserFilters]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UserFilters);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
