import { Component, signal } from '@angular/core';
import { RoutineService } from '../../services/routine-service';
import { Routine, RoutineAssignment } from '../../models/Routine';
import { forkJoin } from 'rxjs';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth-service';

@Component({
  selector: 'app-routine-list-page',
  imports: [],
  templateUrl: './routine-list-page.html',
  styleUrl: './routine-list-page.css'
})
export class RoutineListPage {
  routines = signal<Routine[]>([]);
  errorMessage = signal<string>('');
  currentUsername = signal<string>('');

  constructor(
    private routineService: RoutineService,
    private authService: AuthService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.currentUsername.set(this.routineService.getCurrentUserUsername());
    this.loadRoutines();
  }

  loadRoutines(): void {
    forkJoin({
      routines: this.routineService.getRoutines(),
      assignments: this.routineService.getAllRoutineAssignments()
    }).subscribe({
      next: ({ routines, assignments }) => {
        const assignedRoutineIds = assignments
          .filter(assignment => assignment.memberUsername === this.currentUsername() && assignment.active)
          .map(assignment => assignment.routineId);

        const filteredRoutines = routines.filter(routine => 
          routine.createdBy === this.currentUsername() || 
          assignedRoutineIds.includes(routine.id)
        );
        
        this.routines.set(filteredRoutines);
        this.errorMessage.set('');
      },
      error: (error) => {
        this.errorMessage.set('Error al conectar o cargar las rutinas. Verifica el backend (JSON Server).');
        console.error('Error al obtener rutinas:', error);
        this.routines.set([]);
      }
    });
  }

  editRoutine(id: string): void {
    this.router.navigate(['/routines/edit', id]);
  }

  createRoutine(): void {
    this.router.navigate(['/routines/new']);
  }

  isCreator(routine: Routine): boolean {
    return routine.createdBy === this.currentUsername();
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
          this.errorMessage.set(`Error al eliminar la rutina: ${err.message || 'Error de conexión.'}`);
          console.error('Error al eliminar:', err);
        }
      });
    }
  }
}
