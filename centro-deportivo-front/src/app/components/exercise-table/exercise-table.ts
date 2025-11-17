import { Component, input, signal, computed } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Exercise, TrainingHistory } from '../../models/Routine';
import { RoutineService } from '../../services/routine-service';
import { AuthService } from '../../services/auth-service';

@Component({
  selector: 'app-exercise-table',
  imports: [FormsModule],
  templateUrl: './exercise-table.html',
  styleUrl: './exercise-table.css'
})
export class ExerciseTable {
  exercise = input.required<Exercise>();
  routineId = input.required<string>();

  currentWeights = signal<{ [key: number]: number | null }>({});
  trainingNotes = signal<string>('');
  isSaving = signal(false);
  saveSuccess = signal(false);
  saveError = signal<string | null>(null);

  constructor(
    private routineService: RoutineService,
    private authService: AuthService
  ) {}

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

  updateWeight(serieNumber: number, weight: number | null): void {
    const weights = { ...this.currentWeights() };
    weights[serieNumber] = weight;
    this.currentWeights.set(weights);
  }

  saveTraining(): void {
    const weights = this.currentWeights();
    const ex = this.exercise();

    this.isSaving.set(true);
    this.saveError.set(null);

    const decodedToken = this.authService.getDecodedToken();
    const username = decodedToken?.sub || '';

    const sets = ex.seriesRepetitions.map((series, index) => ({
      number: index + 1,
      weight: weights[index + 1] || 0,
      repetitions: parseInt(series.repetitions) || 0
    }));

    const trainingHistory: TrainingHistory = {
      id: `hist${Date.now()}`,
      date: new Date().toISOString().split('T')[0],
      username: username,
      routineId: this.routineId(),
      exerciseId: ex.id,
      sets: sets,
      notes: this.trainingNotes()
    };

    this.routineService.createTrainingHistory(trainingHistory).subscribe({
      next: () => {
        this.isSaving.set(false);
        this.saveSuccess.set(true);
        
        this.currentWeights.set({});
        this.trainingNotes.set('');
        
        setTimeout(() => this.saveSuccess.set(false), 3000);

        if (!ex.history) {
          ex.history = [];
        }
        ex.history.push(trainingHistory);
      },
      error: (err) => {
        console.error('Error al guardar entrenamiento:', err);
        this.saveError.set('Error al guardar el entrenamiento');
        this.isSaving.set(false);
        setTimeout(() => this.saveError.set(null), 3000);
      }
    });
  }

  hasAllWeights(): boolean {
    const weights = this.currentWeights();
    const totalSeries = this.exercise().seriesRepetitions.length;
    
    for (let i = 1; i <= totalSeries; i++) {
      if (!weights[i] || weights[i]! <= 0) {
        return false;
      }
    }
    return true;
  }

  wasTrainedToday(): boolean {
    const ex = this.exercise();
    if (!ex.history || ex.history.length === 0) {
      return false;
    }

    const today = new Date().toISOString().split('T')[0];
    return ex.history.some(training => training.date === today);
  }
}
