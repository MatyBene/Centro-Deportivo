import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProgressChartPage } from './progress-chart-page';

describe('ProgressChartPage', () => {
  let component: ProgressChartPage;
  let fixture: ComponentFixture<ProgressChartPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProgressChartPage]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProgressChartPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
