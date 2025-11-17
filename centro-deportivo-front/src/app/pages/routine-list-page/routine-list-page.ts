import { Component } from '@angular/core';
import { RoutineService } from '../../services/routine-service';
import { Routine } from '../../models/Routine';
import { Observable, catchError, of } from 'rxjs';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-routine-list-page',
  imports: [CommonModule, RouterLink],
  templateUrl: './routine-list-page.html',
  styleUrl: './routine-list-page.css'
})
export class RoutineListPage {
  routines$!: Observable<Routine[]>; 
  errorMessage = '';

  constructor(
    private routineService: RoutineService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.loadRoutines();
  }

  loadRoutines(): void {
    this.routines$ = this.routineService.getRoutines().pipe(
      catchError(error => {
        this.errorMessage = 'Error al conectar o cargar las rutinas. Verifica el backend (JSON Server).';
        console.error('Error al obtener rutinas:', error);
        return of([]); 
      })
    );
  }

  editRoutine(id: string): void {
    this.router.navigate(['/routines/edit', id]);
  }

  createRoutine(): void {
    this.router.navigate(['/routines/new']);
  }

  deleteRoutine(id: string, name: string): void {
    const confirmed = window.confirm(`¿Estás seguro de que deseas eliminar la rutina "${name}" (ID: ${id})? Esta acción es irreversible.`);

    if (confirmed) {
      this.routineService.deleteRoutine(id).subscribe({
        next: () => {
          console.log(`Rutina con ID ${id} eliminada correctamente.`);
          this.loadRoutines(); 
        },
        error: (err) => {
          this.errorMessage = `Error al eliminar la rutina: ${err.message || 'Error de conexión.'}`;
          console.error('Error al eliminar:', err);
        }
      });
    }
  }
}
