import { Component, OnInit, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { RoutineService } from '../../services/routine-service';
import { AuthService } from '../../services/auth-service';
import { RoutineProgressChart } from '../../components/routine-progress-chart/routine-progress-chart';
import { Exercise, TrainingHistory } from '../../models/Routine';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-progress-chart-page',
  imports: [RoutineProgressChart],
  templateUrl: './progress-chart-page.html',
  styleUrl: './progress-chart-page.css'
})
export class ProgressChartPage implements OnInit {
  routineId = signal<string>('');
  routineName = signal<string>('');
  exercises = signal<Exercise[]>([]);
  
  selectedExerciseId = signal<string>('');
  selectedExerciseName = signal<string>('');
  exerciseHistory = signal<TrainingHistory[]>([]);
  
  loading = signal<boolean>(true);
  showChart = signal<boolean>(false);

  constructor(
    private route: ActivatedRoute,
    private routineService: RoutineService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      const id = params['id'];
      if (id) {
        this.routineId.set(id);
        this.loadRoutineData(id);
      }
    });
  }

  loadRoutineData(routineId: string) {
    this.loading.set(true);
    
    forkJoin({
      routine: this.routineService.getRoutine(routineId),
      trainingHistory: this.routineService.getTrainingHistory()
    }).subscribe({
      next: ({ routine, trainingHistory }) => {
        this.routineName.set(routine.routine.name);
        
        const allExercises: Exercise[] = [];
        
        // Normalizar: puede venir como routineDays o days
        const days = routine.routine.routineDays || (routine.routine as any).days || [];
        
        if (days && days.length > 0) {
          days.forEach((day: any) => {
            if (day.exercises && day.exercises.length > 0) {
              day.exercises.forEach((exercise: Exercise) => {
                exercise.history = trainingHistory.filter(
                  h => h.exerciseId === exercise.id && h.routineId === routineId
                );
              });
              allExercises.push(...day.exercises);
            }
          })
        }
        
        this.exercises.set(allExercises);
        this.loading.set(false);
      },
      error: (error) => {
        console.error('Error loading routine:', error);
        this.loading.set(false);
      }
    })
  }

  selectExercise(exerciseId: string, exerciseName: string) {
    this.selectedExerciseId.set(exerciseId);
    this.selectedExerciseName.set(exerciseName);
    
    const decodedToken = this.authService.getDecodedToken();
    const currentUsername = decodedToken?.sub || '';
    
    const selectedExercise = this.exercises().find(e => e.id === exerciseId);
    
    if (selectedExercise && selectedExercise.history) {
      const userHistory = selectedExercise.history.filter(
        h => h.username === currentUsername
      );
      
      this.exerciseHistory.set(userHistory);
      
      console.log('Exercise selected:', exerciseName);
      console.log('History:', userHistory);
    } else {
      this.exerciseHistory.set([]);
    }
    
    this.showChart.set(true);
  }

  onExerciseChange(event: Event): void {
    const selectElement = event.target as HTMLSelectElement;
    const exerciseId = selectElement.value;
    
    const exercise = this.exercises().find(e => e.id === exerciseId);
    if (exercise) {
      this.selectExercise(exercise.id, exercise.name);
    }
  }
}
