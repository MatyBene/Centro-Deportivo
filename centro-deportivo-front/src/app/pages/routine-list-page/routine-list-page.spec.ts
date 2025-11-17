import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RoutineListPage } from './routine-list-page';

describe('RoutineListPage', () => {
  let component: RoutineListPage;
  let fixture: ComponentFixture<RoutineListPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RoutineListPage]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RoutineListPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
