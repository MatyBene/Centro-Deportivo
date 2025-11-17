import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RoutineProgressChart } from './routine-progress-chart';

describe('RoutineProgressChart', () => {
  let component: RoutineProgressChart;
  let fixture: ComponentFixture<RoutineProgressChart>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RoutineProgressChart]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RoutineProgressChart);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
