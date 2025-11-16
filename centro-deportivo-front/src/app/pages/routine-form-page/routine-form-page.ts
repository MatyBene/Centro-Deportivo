import { Component, OnInit } from '@angular/core';
import { RoutineService } from '../../services/routine-service';
import { ActivatedRoute, Router } from '@angular/router';
import { Routine } from '../../models/Routine';

@Component({
  selector: 'app-routine-form-page',
  imports: [],
  templateUrl: './routine-form-page.html',
  styleUrl: './routine-form-page.css'
})
export class RoutineFormPage implements OnInit {
    routines!: Routine[];
    routine!: Routine;
  
    constructor(
      public routineService: RoutineService, 
    ) {}
  
    ngOnInit(): void {
      // this.loadRoutine();
      this.loadRoutineId();
    }
  
    loadRoutine() {
      this.routineService.getRoutines().subscribe({
        next: (data) => { this.routines = data;
          console.log(data)
        },
        error: (e) => {console.error(e)}  
        }
      )
    }

    loadRoutineId() {
      this.routineService.getRoutine("rut001").subscribe({
        next: (data) => {this.routine = data; console.log(data)},
        error: (e) => {console.error(e)}
      })
    }
}
