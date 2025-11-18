import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { InstructorService } from '../../services/instructor-service';
import { CommonModule } from '@angular/common'; 
import { RouterLink } from '@angular/router'; 
import SportActivitySummary from '../../models/SportActivitySummary';

@Component({
  selector: 'app-instructor-activities-page',
  standalone: true,
  imports: [CommonModule, RouterLink], 
  templateUrl: './instructor-activities-page.html',
  styleUrls: ['./instructor-activities-page.css']
})
export class InstructorActivitiesPage implements OnInit {
  
  activities: SportActivitySummary[] = []; 
  isLoading: boolean = true;
  error: string | null = null;
  
  constructor(
    private instructorService: InstructorService
  ) { } 
  
  ngOnInit(): void {
    this.loadInstructorActivities();
  }
  
  loadInstructorActivities(): void {
    this.isLoading = true;
    this.instructorService.getActivitiesByInstructor().subscribe({ 
      next: (data) => {
        this.activities = data;
        this.isLoading = false;
      },
      error: (e) => {
        console.error('Error al obtener actividades del instructor:', e);
        this.error = 'No se pudieron cargar sus actividades. (Error: ' + e.status + ')';
        this.isLoading = false;
      }
    });
  }
}