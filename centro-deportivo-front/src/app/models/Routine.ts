export interface SeriesRecord {
  number: number;
  repetitions: string;
  previousWeight: number | null;
  previousDate: string | null;
  currentWeight: number | null;
  alreadyLoggedToday?: boolean;
}

export interface TrainingHistory {
  id: string;
  date: string;
  username: string;
  routineId: string;
  exerciseId: string;
  sets: {
    number: number;
    weight: number;
    repetitions: number;
  }[];
  notes?: string;
}


export interface Exercise {
  id: string;
  routineDayId: string;
  name: string;
  order: number;
  muscleGroup: string;
  type: string;
  sets: number;
  repetitions: string;
  restSeconds: number;
  suggestedWeight?: string;
  notes?: string;
  seriesRecord?: SeriesRecord[];
  history?: TrainingHistory[];
  completedToday?: boolean;
}

export interface RoutineDay {
  id: string;
  routineId: string;
  dayNumber: number;
  name: string;
  description?: string;
  order: number;
}


export interface Day {
  day: number;
  name: string;
  exercises: Exercise[];
}


export interface Warmup {
  durationMinutes: number;
  activities: string[];
}


export interface Cooldown {
  durationMinutes: number;
  activities: string[];
}


export interface Routine {
  id: string;
  name: string;
  description: string;
  level: string;
  durationWeeks: number;
  daysPerWeek: number;
  goal: string;
  createdBy: string;
  createdAt: string;
  active: boolean;

  days?: Day[];
  routineDays?: RoutineDay[];
  tags?: string[];
  generalNotes?: string[];
  warmup?: Warmup;
  cooldown?: Cooldown;
  isTemplate?: boolean;
}

export interface RoutineAssignment {
  id: string;
  routineId: string;
  memberUsername: string;
  instructorUsername: string;
  assignedDate: string;
  active: boolean;
  notes?: string;
  startDate: string;
  endDate: string;
}

export interface RoutineResponse {
  routine: Routine;
}