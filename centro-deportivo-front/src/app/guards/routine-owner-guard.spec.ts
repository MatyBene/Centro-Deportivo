import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { routineOwnerGuard } from './routine-owner-guard';

describe('routineOwnerGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) => 
      TestBed.runInInjectionContext(() => routineOwnerGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
