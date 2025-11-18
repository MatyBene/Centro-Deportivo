import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MemberActivitiesPage } from './member-activities-page';

describe('MemberActivitiesPage', () => {
  let component: MemberActivitiesPage;
  let fixture: ComponentFixture<MemberActivitiesPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MemberActivitiesPage]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MemberActivitiesPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
