import { Component, OnInit } from '@angular/core';
import { MemberService } from '../../services/member-service';
import { RouterLink } from '@angular/router';
import EnrolledActivitySummary from '../../models/EnrolledActivitySummary';

@Component({
  selector: 'app-member-activities-page',
  imports: [RouterLink],
  templateUrl: './member-activities-page.html',
  styleUrl: './member-activities-page.css'
})
export class MemberActivitiesPageComponent implements OnInit {

  activities: EnrolledActivitySummary[] = [];
  isLoading: boolean = true;
  error: string | null = null;
  unenrollmentMessage: string | null = null;

  constructor(private memberService: MemberService) { }

  ngOnInit(): void {
    this.loadActivities();
  }

  loadActivities(): void {
    this.isLoading = true;
    this.memberService.getEnrolledActivities().subscribe({
      next: (data) => {
        this.activities = data;
        this.isLoading = false;
        this.error = null;
      },
      error: (e) => {
        console.error('Error al cargar actividades:', e);
        this.error = 'No pudimos cargar tus actividades inscritas. Por favor, intenta más tarde.'; 
        this.isLoading = false;
      }
    });
  }

  unsubscribe(activityId: number, activityName: string): void {
    if (!confirm(`¿Estás seguro de que quieres darte de baja de ${activityName}? Esta acción es irreversible.`)) {
      return;
    }

    this.memberService.unsubscribeFromActivity(activityId).subscribe({
      next: (response) => {
        this.unenrollmentMessage = `¡Te has dado de baja de ${activityName} con éxito!`; 
        this.loadActivities(); 
        setTimeout(() => {
           this.unenrollmentMessage = null;
        }, 5000);
      },
      error: (e) => {
        console.error('Error al darse de baja:', e);
        this.unenrollmentMessage = `Error: No se pudo completar la baja. Por favor, intenta más tarde.`;
      }
    });
  }
}