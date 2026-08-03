import { Component, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { RoutineService } from '../../services/routine-service';
import { Routine, Exercise } from '../../models/Routine';
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
  expandedDays = signal<Set<number>>(new Set());

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
      this.error.set('No se encontrÃ³ el ID de la rutina');
      this.isLoading.set(false);
    }
  }

  loadRoutine(id: string): void {
    this.isLoading.set(true);
    
    forkJoin({
      routine: this.routineService.getRoutine(Number(id)),
      trainingHistory: this.routineService.getTrainingHistory()
    }).subscribe({
      next: ({ routine, trainingHistory }) => {
        const routineData = routine as any;
        
        const days = routineData.routineDays ?? [];
        
        if (days && days.length > 0) {
          days.forEach((day: any) => {
            if (day.exercises && day.exercises.length > 0) {
              day.exercises.forEach((exercise: Exercise) => {
                exercise.history = trainingHistory.filter(
                  h => h.exerciseId === exercise.id && h.routineId === Number(id)
                );
              });
            }
          });
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

  toggleDay(dayId: number): void {
    const expanded = new Set(this.expandedDays());
    if (expanded.has(dayId)) {
      expanded.delete(dayId);
    } else {
      expanded.add(dayId);
    }
    this.expandedDays.set(expanded);
  }

  isDayExpanded(dayId: number): boolean {
    return this.expandedDays().has(dayId);
  }

  goBack(): void {
    this.router.navigate(['/routines']);
  }
}