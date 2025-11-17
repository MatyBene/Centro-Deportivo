import { Component, input, computed } from '@angular/core';
import { Exercise } from '../../models/Routine';

@Component({
  selector: 'app-exercise-table',
  imports: [],
  templateUrl: './exercise-table.html',
  styleUrl: './exercise-table.css'
})
export class ExerciseTable {
  exercise = input.required<Exercise>();

  getPreviousWeight(serieNumber: number): number | null {
    const ex = this.exercise();
    if (!ex.history || ex.history.length === 0) {
      return null;
    }

    const sortedHistory = [...ex.history].sort((a, b) => 
      new Date(b.date).getTime() - new Date(a.date).getTime()
    );

    const lastTraining = sortedHistory[0];
    const serie = lastTraining.sets.find(s => s.number === serieNumber);
    
    return serie ? serie.weight : null;
  }

  getLastTrainingDate(): string | null {
    const ex = this.exercise();
    if (!ex.history || ex.history.length === 0) {
      return null;
    }

    const sortedHistory = [...ex.history].sort((a, b) => 
      new Date(b.date).getTime() - new Date(a.date).getTime()
    );

    return sortedHistory[0].date;
  }
}
