import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InstructorActivitiesPage } from './instructor-activities-page';

describe('InstructorActivitiesPage', () => {
  let component: InstructorActivitiesPage;
  let fixture: ComponentFixture<InstructorActivitiesPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InstructorActivitiesPage]
    })
    .compileComponents();

    fixture = TestBed.createComponent(InstructorActivitiesPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
