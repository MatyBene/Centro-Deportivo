import { Component, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { RoutineService } from '../../services/routine-service';
import { Routine, RoutineResponse } from '../../models/Routine';

@Component({
  selector: 'app-routine-detail-page',
  imports: [],
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
    this.routineService.getRoutine(id).subscribe({
      next: (res: RoutineResponse) => {
        if (!res || !res.routine) {
          this.error.set('No se encontró la rutina');
        } else {
          this.routine.set(res.routine);
        }
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
