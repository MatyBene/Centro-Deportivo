import { Component, OnInit, signal } from '@angular/core';
import { RoutineService } from '../../services/routine-service';
import { ActivatedRoute, Router } from '@angular/router';
import { Exercise, Routine, Day, RoutineResponse, Warmup, Cooldown } from '../../models/Routine';
import { FormArray, FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';

@Component({
  selector: 'app-routine-form-page',
  imports: [ReactiveFormsModule],
  templateUrl: './routine-form-page.html',
  styleUrl: './routine-form-page.css'
})
export class RoutineFormPage implements OnInit {
  routineForm: FormGroup;
  isSaving = signal(false);
  isEditMode = signal(false);
  routineId: string | null = null;
  
  // Opciones para selectores
  levelOptions = ['Principiante', 'Intermedio', 'Avanzado'];
  goalOptions = ['Fuerza', 'Hipertrofia', 'Resistencia'];
  muscleGroupOptions = ['Pecho', 'Espalda', 'Piernas', 'Hombros', 'Brazos', 'Abdomen'];
  exerciseTypeOptions = ['Compuesto', 'Aislado'];

  constructor(
    private fb: FormBuilder,
    private routineService: RoutineService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.routineForm = this.fb.group({

      name: ['', Validators.required],
      description: ['', Validators.required],
      level: ['', Validators.required],
      goal: ['', Validators.required],
      durationWeeks: ['', [Validators.required, Validators.min(1)]],
      daysPerWeek: ['', [Validators.required, Validators.min(1), Validators.max(7)]],
      warmupDuration: [''],
      warmupActivities: this.fb.array<FormControl<string>>([]),
      cooldownDuration: [''],
      cooldownActivities: this.fb.array<FormControl<string>>([]),
      routineDays: this.fb.array<FormGroup>([]),
      generalNotes: this.fb.array<FormControl<string>>([]),
    });
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.routineId = params.get('id');
      if (this.routineId) {
        this.isEditMode.set(true);
        this.loadRoutine(this.routineId);
      }
    });
  }

  get warmupActivities(): FormArray {
    return this.routineForm.get('warmupActivities') as FormArray;
  }

  get cooldownActivities(): FormArray {
    return this.routineForm.get('cooldownActivities') as FormArray;
  }

  get routineDays(): FormArray {
    return this.routineForm.get('routineDays') as FormArray;
  }

  get generalNotes(): FormArray {
    return this.routineForm.get('generalNotes') as FormArray;
  }

  getExercises(dayIndex: number): FormArray {
    return this.routineDays.at(dayIndex).get('exercises') as FormArray;
  }


  loadRoutine(id: string): void {
  this.routineService.getRoutine(id).subscribe({
    next: (res: RoutineResponse) => {
      if (!res || !res.routine) {
        console.error('Error al cargar rutina: La rutina no se encontró en la respuesta.', res);
        alert('Error: La rutina solicitada no existe.');
        this.router.navigate(['/routines']);
        return;
      }
      
      const routineData = res.routine as any; 

      const normalizedRoutine: Routine = {
        ...routineData,
        cooldown: routineData.cooldown || routineData.coolDown,

        routineDays: routineData.routineDays || routineData.days,
        generalNotes: routineData.generalNotes || [],
      } as Routine; 

      this.clearFormArray(this.warmupActivities);
      this.clearFormArray(this.cooldownActivities);
      this.clearFormArray(this.routineDays);
      this.clearFormArray(this.generalNotes);

      this.routineForm.patchValue({
        name: normalizedRoutine.name,
        description: normalizedRoutine.description,
        level: normalizedRoutine.level,
        goal: normalizedRoutine.goal,
        durationWeeks: normalizedRoutine.durationWeeks,
        daysPerWeek: normalizedRoutine.daysPerWeek,
        warmupDuration: normalizedRoutine.warmup?.durationMinutes || 10,
        cooldownDuration: normalizedRoutine.cooldown?.durationMinutes || 10,
      });

      normalizedRoutine.warmup?.activities?.forEach(act => {
        this.warmupActivities.push(this.fb.control(act));
      });


      normalizedRoutine.cooldown?.activities?.forEach(act => {
        this.cooldownActivities.push(this.fb.control(act));
      });
      
      normalizedRoutine.routineDays?.forEach(day => {
        const dayGroup = this.fb.group({
          id: [day.id],
          dayNumber: [day.dayNumber],
          name: [day.name, Validators.required],
          description: [day.description], 
          order: [day.order],  
          exercises: this.fb.array<FormGroup>([]) 
        });

        if (day.exercises) { 
          const exercisesArray = dayGroup.get('exercises') as FormArray;
          day.exercises.forEach(ej => {
            const exerciseWithSeries = {
                ...ej,
                seriesRepetitions: (ej as any).seriesRepetitions || (ej as any).sets 
            };
            exercisesArray.push(this.createExerciseFormGroup(exerciseWithSeries));
          });
        }

        this.routineDays.push(dayGroup);
      });

      normalizedRoutine.generalNotes?.forEach(note => {
        this.generalNotes.push(this.fb.control(note));
      });

    },
    error: (err) => {
      console.error('Error al cargar rutina:', err);
      alert('Error al cargar rutina');
      // this.router.navigate(['/routines']);  
    }
  });
}

  createDayFormGroup(dayNumber: number): FormGroup {
    return this.fb.group({
      id: [`day${Date.now()}`],
      dayNumber: [dayNumber],
      name: ['', Validators.required],
      description: [''],
      order: [dayNumber],
      exercises: this.fb.array<FormGroup>([])
    });
  }

  createExerciseFormGroup(exercise?: Partial<Exercise>): FormGroup {
   const defaultSets = 1; 
    
    const initialRepetitions = exercise?.seriesRepetitions && exercise.seriesRepetitions.length > 0
        ? exercise.seriesRepetitions.map(s => this.createRepetitionsFormGroup(s.repetitions))
        : Array(defaultSets).fill(0).map(() => this.createRepetitionsFormGroup(''));

    return this.fb.group({
        id: [exercise?.id || `ej${Date.now()}`],
        name: [exercise?.name || '', Validators.required],
        muscleGroup: [exercise?.muscleGroup || '', Validators.required],
        type: [exercise?.type || 'Compound'], 
        sets: [exercise?.sets , [Validators.required, Validators.min(1)]],
        seriesRepetitions: this.fb.array(initialRepetitions),
        restSeconds: [exercise?.restSeconds , Validators.required],
        suggestedWeight: [exercise?.suggestedWeight || ''],
        notes: [exercise?.notes || ''],
    });
}

  addWarmupActivity(): void {
    this.warmupActivities.push(this.fb.control(''));
  }

  removeWarmupActivity(index: number): void {
    this.warmupActivities.removeAt(index);
  }

  addCooldownActivity(): void {
    this.cooldownActivities.push(this.fb.control(''));
  }

  removeCooldownActivity(index: number): void {
    this.cooldownActivities.removeAt(index);
  }

  addDay(): void {
    const newDayNumber = this.routineDays.length + 1;
    this.routineDays.push(this.createDayFormGroup(newDayNumber));
  }

  removeDay(index: number): void {
    this.routineDays.removeAt(index);
    this.routineDays.controls.forEach((control, i) => {
      control.get('dayNumber')?.setValue(i + 1);
      control.get('order')?.setValue(i + 1);
    });
  }

  addExercise(dayIndex: number): void {
    const exercises = this.getExercises(dayIndex);
    exercises.push(this.createExerciseFormGroup());
  }

  removeExercise(dayIndex: number, exerciseIndex: number): void {
    const exercises = this.getExercises(dayIndex);
    exercises.removeAt(exerciseIndex);
  }

  addGeneralNote(): void {
    this.generalNotes.push(this.fb.control(''));
  }

  removeGeneralNote(index: number): void {
    this.generalNotes.removeAt(index);
  }

  private createSeriesFormGroup(repetitions: string = ''): FormGroup {
        return this.fb.group({
            repetitions: [repetitions, Validators.required],
        });
    }

    getSets(dayIndex: number, exerciseIndex: number): FormArray {
        const exercisesArray = this.getExercises(dayIndex);
        const exerciseGroup = exercisesArray.at(exerciseIndex) as FormGroup;
        return exerciseGroup.get('sets') as FormArray; 
    }

    addSeries(dayIndex: number, exerciseIndex: number): void {
        this.getSets(dayIndex, exerciseIndex).push(this.createSeriesFormGroup());
    }

    removeSeries(dayIndex: number, exerciseIndex: number, seriesIndex: number): void {
        this.getSets(dayIndex, exerciseIndex).removeAt(seriesIndex);
    }

    private createRepetitionsFormGroup(repetitions: string = ''): FormGroup {
      return this.fb.group({
        repetitions: [repetitions, Validators.required],
      });
    }
    getSeriesRepetitions(dayIndex: number, exerciseIndex: number): FormArray {
      const exercisesArray = this.getExercises(dayIndex);
      const exerciseGroup = exercisesArray.at(exerciseIndex) as FormGroup;
      return exerciseGroup.get('seriesRepetitions') as FormArray; 
    }

    onSetsChange(dayIndex: number, exerciseIndex: number, event: Event): void {
    const target = event.target as HTMLInputElement;
    const newSetsCount = parseInt(target.value, 10) || 0;
    const seriesArray = this.getSeriesRepetitions(dayIndex, exerciseIndex);
    const currentSetsCount = seriesArray.length;

    if (newSetsCount > currentSetsCount) {
        const diff = newSetsCount - currentSetsCount;
        for (let i = 0; i < diff; i++) {
            seriesArray.push(this.createRepetitionsFormGroup());
        }
    } else if (newSetsCount < currentSetsCount) {
        const diff = currentSetsCount - newSetsCount;
        for (let i = 0; i < diff; i++) {
            seriesArray.removeAt(seriesArray.length - 1);
        }
    }
}

  saveRoutine(): void {
    if (this.routineForm.invalid) {
      alert('Completar todos los campos.');
      this.routineForm.markAllAsTouched(); 
      return;
    }

    this.isSaving.set(true);

    const formValue = this.routineForm.value;
    
    const warmup: Warmup = {
      durationMinutes: formValue.warmupDuration,
      activities: formValue.warmupActivities.filter((a: string) => a.trim() !== '')
    };
    
    const cooldown: Cooldown = {
      durationMinutes: formValue.cooldownDuration,
      activities: formValue.cooldownActivities.filter((a: string) => a.trim() !== '')
    };

    const routine: Routine = {
      id: this.routineId || Date.now().toString(),
      name: formValue.name,
      description: formValue.description,
      level: formValue.level,
      goal: formValue.goal,
      durationWeeks: formValue.durationWeeks,
      daysPerWeek: formValue.daysPerWeek,
      
      warmup: warmup,
      cooldown: cooldown,
      routineDays: formValue.routineDays, 
      generalNotes: formValue.generalNotes.filter((n: string) => n.trim() !== ''),
      createdBy: this.routineService.getCurrentUserUsername(), 
      createdAt: new Date().toISOString().split('T')[0],
      active: true,
      isTemplate: false,
    };

    const operation = this.isEditMode()  
      ? this.routineService.updateRoutine(this.routineId!, routine)
      : this.routineService.createRoutine(routine);

    operation.subscribe({
      next: () => {
        const message = this.isEditMode()  
          ? '✅ Rutina editada con éxito! '
          : '✅ Rutina creada con éxito! ';
        alert(message);
        this.router.navigate(['/routines']);
      },
      error: (err: unknown) => {
        console.error('Error al guardar rutina:', err);
        alert('Error al guardar rutina.');
        this.isSaving.set(false);
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/routines']);
  }

  private clearFormArray(formArray: FormArray): void {
    while (formArray.length !== 0) {
    formArray.removeAt(0);
  }
  }
}
