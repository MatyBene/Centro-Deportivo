import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InstructorDetailPage } from './instructor-detail-page';

describe('InstructorDetailPage', () => {
  let component: InstructorDetailPage;
  let fixture: ComponentFixture<InstructorDetailPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InstructorDetailPage]
    })
    .compileComponents();

    fixture = TestBed.createComponent(InstructorDetailPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
