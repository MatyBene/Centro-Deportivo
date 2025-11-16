import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RoutineFormPage } from './routine-form-page';

describe('RoutineFormPage', () => {
  let component: RoutineFormPage;
  let fixture: ComponentFixture<RoutineFormPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RoutineFormPage]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RoutineFormPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
