import { Component, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { RoutineService } from '../../services/routine-service';
import { Routine, RoutineResponse, Exercise } from '../../models/Routine';
import { ExerciseTable } from '../../components/exercise-table/exercise-table';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-routine-detail-page',
  imports: [ExerciseTable],
  templateUrl: './routine-detail-page.html',
  styleUrl: './routine-detail-page.css'
})
export class RoutineDetailPage implements OnInit{
  routine = signal<Routine | null>(null);
  isLoading = signal(true);
  error = signal<string | null>(null);
  expandedDays = signal<Set<string>>(new Set());

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private routineService: RoutineService
  ) {}

  ngOnInit(): void {
    const routineId = this.route.snapshot.paramMap.get('id');
    if (routineId) {
      this.loadRoutine(routineId);
    } else {
      this.error.set('No se encontró el ID de la rutina');
      this.isLoading.set(false);
    }
  }

  loadRoutine(id: string): void {
    this.isLoading.set(true);
    
    forkJoin({
      routine: this.routineService.getRoutine(id),
      trainingHistory: this.routineService.getTrainingHistory()
    }).subscribe({
      next: ({ routine, trainingHistory }) => {
        if (!routine || !routine.routine) {
          this.error.set('No se encontró la rutina');
          this.isLoading.set(false);
          return;
        }

        const routineData = routine.routine;
        
        const days = routineData.routineDays || (routineData as any).days || [];
        
        if (days && days.length > 0) {
          days.forEach((day: any) => {
            if (day.exercises && day.exercises.length > 0) {
              day.exercises.forEach((exercise: Exercise) => {
                exercise.history = trainingHistory.filter(
                  h => h.exerciseId === exercise.id && h.routineId === id
                );
              });
            }
          });
        }

        if (routineData.routineDays) {
          routineData.routineDays = days;
        } else {
          (routineData as any).routineDays = days;
        }

        this.routine.set(routineData);
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Error al cargar rutina:', err);
        this.error.set('Error al cargar la rutina');
        this.isLoading.set(false);
      }
    });
  }

  toggleDay(dayId: string): void {
    const expanded = new Set(this.expandedDays());
    if (expanded.has(dayId)) {
      expanded.delete(dayId);
    } else {
      expanded.add(dayId);
    }
    this.expandedDays.set(expanded);
  }

  isDayExpanded(dayId: string): boolean {
    return this.expandedDays().has(dayId);
  }

  goBack(): void {
    this.router.navigate(['/profile']);
  }
}
