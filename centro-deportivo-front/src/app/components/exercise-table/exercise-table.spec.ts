import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExerciseTable } from './exercise-table';

describe('ExerciseTable', () => {
  let component: ExerciseTable;
  let fixture: ComponentFixture<ExerciseTable>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExerciseTable]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ExerciseTable);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
