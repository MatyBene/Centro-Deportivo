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

  const routineIdParam = route.paramMap.get('id');
  const decodedToken = authService.getDecodedToken();
  const currentUsername = decodedToken?.sub || '';

  if (!routineIdParam || !currentUsername) {
    router.navigate(['/']);
    return false;
  }

  const routineId = Number(routineIdParam);

  return routineService.getRoutine(routineId).pipe(
  switchMap(routine => {
    if (routine.createdBy === currentUsername) {
      return of(true);
    }

    return routineService.getRoutineAssignments(currentUsername).pipe(
      map((assignments: { routineId: number }[]) => {
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