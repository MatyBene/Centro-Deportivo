import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InstructorProfileCard } from './instructor-profile-card';

describe('InstructorProfileCard', () => {
  let component: InstructorProfileCard;
  let fixture: ComponentFixture<InstructorProfileCard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InstructorProfileCard]
    })
    .compileComponents();

    fixture = TestBed.createComponent(InstructorProfileCard);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
