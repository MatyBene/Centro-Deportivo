import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { map, catchError, switchMap } from 'rxjs/operators';
import { of } from 'rxjs';
import { RoutineService } from '../services/routine-service';
import { AuthService } from '../services/auth-service';

export const routineOwnerGuard: CanActivateFn = (route, state) => {
  const routineService = inject(RoutineService);
  const authService = inject(AuthService);
  const router = inject(Router);
  
  const routineId = route.paramMap.get('id');
  const decodedToken = authService.getDecodedToken();
  const currentUsername = decodedToken?.sub || '';

  if (!routineId || !currentUsername) {
    router.navigate(['/']);
    return false;
  }

  return routineService.getRoutine(routineId).pipe(
  switchMap(routine => {
    if (routine.routine.createdBy === currentUsername) {
      return of(true);
    }
    
    return routineService.getUserRoutineAssignments(currentUsername).pipe(
      map(assignments => {
        const hasAccess = assignments.some(
          assignment => assignment.routineId === routineId
        );
        
        if (!hasAccess) {
          router.navigate(['/']);
        }
        
        return hasAccess;
      })
    );
  }),
  catchError(() => {
    router.navigate(['/']);
    return of(false);
  })
  );
};