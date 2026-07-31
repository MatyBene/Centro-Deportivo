export interface Routine {
  id: number;
  createdBy: string;
  name: string;
  description: string;
  level: string;
  goal: string;
  durationWeeks: number;
  daysPerWeek: number;
  createdAt?: string;
  isTemplate: boolean;
  generalNotes: string[];
  warmup: Warmup | null;
  cooldown: Cooldown | null;
  routineDays: RoutineDay[];
}

export interface RoutineDay {
  id?: number;
  routineId?: number;
  dayOrder?: number;
  day?: string;
  dayNumber: number;
  name: string;
  description: string;
  order: number;
  exercises: Exercise[];
}

export interface Exercise {
  id?: number;
  name: string;
  muscleGroup: string;
  type: string;
  notes: string;
  suggestedWeight: string;
  restSeconds: number;
  exerciseOrder: number;
  seriesRepetitions: { repetitions: string }[];
  sets: number;
  history?: TrainingHistory[];
}

export interface RoutineAssignment {
  id: number;
  routineId: number;
  routineName: string;
  memberUsername: string;
  instructorUsername: string;
  active: boolean;
  assignedAt: string;
}

export interface TrainingHistory {
  id?: number;
  username: string;
  date: string;
  routineId: number;
  exerciseId: number;
  sets: SeriesRecord[];
  notes: string;
}

export interface Warmup { durationMinutes: number; activities: string[]; }
export interface Cooldown { durationMinutes: number; activities: string[]; }
export interface SeriesRecord { number: number; weight: number; repetitions: number; }