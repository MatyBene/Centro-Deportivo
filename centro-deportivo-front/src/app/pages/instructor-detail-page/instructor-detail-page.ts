import { Component, OnInit } from '@angular/core'; 
import { ActivatedRoute, RouterLink } from '@angular/router'; 
import { InstructorService } from '../../services/instructor-service'; 
import Instructor from '../../models/Instructor'; 
import { CommonModule } from '@angular/common'; 
import { HttpClientModule } from '@angular/common/http';

@Component({
  selector: 'app-instructor-detail-page',
  standalone: true, 
  imports: [CommonModule, RouterLink],
  providers: [InstructorService],
  templateUrl: './instructor-detail-page.html',
  styleUrl: './instructor-detail-page.css'
})
export class InstructorDetailPage implements OnInit { 
  
  instructor!: Instructor;
  isLoading: boolean = true;
  error: string | null = null;
  
  constructor(
    private route: ActivatedRoute,
    private instructorService: InstructorService
  ) { }
  
  ngOnInit(): void {
    this.loadInstructorDetail();
  }
  
  loadInstructorDetail(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    const id = idParam ? +idParam : null;

    if (id) {
      this.isLoading = true;
      this.instructorService.getInstructor(id).subscribe({
        next: (data) => {
          this.instructor = data;
          this.isLoading = false;
        },
        error: (e) => {
          console.error('Error al obtener el detalle del instructor:', e);
          this.error = 'No se pudo cargar el detalle del instructor.';
          this.isLoading = false;
        }
      });
    } else {
      console.error('ID inválido:', idParam);
      this.error = 'ID de instructor no válido.';
      this.isLoading = false;
    }
  }
}