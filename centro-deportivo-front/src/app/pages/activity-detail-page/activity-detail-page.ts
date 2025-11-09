import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ActivityService } from '../../services/activity-service';
import SportActivity from '../../models/SportActivity';
import { AuthService } from '../../services/auth-service';
import { MemberService } from '../../services/member-service';
import { AdminService } from '../../services/admin-service';
import { InstructorService } from '../../services/instructor-service';

@Component({
  selector: 'app-activity-detail-page',
  imports: [CommonModule, RouterLink],
  templateUrl: './activity-detail-page.html',
  styleUrl: './activity-detail-page.css'
})
export class ActivityDetailPage implements OnInit {
  activity! : SportActivity;
  isLoading: boolean = true;
  error: string | null = null;
  isEnrolled: boolean = false;
  activityId!: number;
  memberUsername: string = '';
  adminActionMessage: string | null = null;
  isAdminActionError: boolean = false;
  memberIdToEnroll: number | null = null;
  memberUsernameToEnroll: string = '';
  instructorActionMessage: string | null = null;
  isInstructorActionError: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private activityService : ActivityService,
    private router: Router, 
    public authService: AuthService,
    private memberService: MemberService,
    private adminService: AdminService,
    private instructorService: InstructorService
  ){}

  ngOnInit(): void {
    this.loadActivityDetail();
  }
  
  loadActivityDetail(): void{
    const idParam = this.route.snapshot.paramMap.get('id');
    const id = idParam ? +idParam : null;

    if (id){
      this.activityId = id;
      this.isLoading = true;
      this.activityService.getActivity(id).subscribe({
        next: (data) => {
          this.activity = data;
          this.checkIfEnrolled();
          this.isLoading = false;
        },
        error: (e) => {
          console.error('Error al obtener el detalle: ', e);
          this.error = 'No se pudo cargar el detalle de la actividad.';
          this.isLoading = false;
        }
      })
    } else {
        this.error = 'ID de actividad no válido.';
        this.isLoading = false;
    }
  }

  checkIfEnrolled(): void {
    if (this.authService.isLoggedIn() && this.authService.getUserRole() === 'MEMBER' && this.activityId) {
      this.memberService.getEnrolledActivities().subscribe({
        next: (activities) => {
          this.isEnrolled = activities.some(activity => activity.activityId === this.activityId);
        },
        error: (e) => {
          console.log('Error al verificar inscripción: ', e);
          this.isEnrolled = false;
        }
      });
    } else {
      this.isEnrolled = false;
    }
  }

getInstructor(instructorId: number | undefined): void { 
    if (instructorId === null || instructorId === undefined || isNaN(instructorId)) {
        console.warn('Intento de navegación a detalle de instructor con ID inválido/no cargado.');
        return; 
    }
    
    this.router.navigate(['/instructors', instructorId]).then(() => {
    }).catch(error => {
        console.error('Error en la navegación:', error);
    });
}

  enrollToActivity() {  
    this.memberService.subscribeToActivity(this.activityId).subscribe({
      next: () => {
        this.isEnrolled = true;
        this.loadActivityDetail();
      },
      error: (e) => {
        console.log('Error al inscribirse:', e);
      }
    });
  }

  unenrollToActivity() {
    this.memberService.unsubscribeFromActivity(this.activityId).subscribe({
      next: () => {
        this.isEnrolled = false;
        this.loadActivityDetail();
      },
      error: (e) => {
        console.log('Error al desuscribirse:', e)
      }
    })
  }

  enrollMemberAsAdmin(username: string) {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.adminService.enrollMemberToActivity(idParam, username).subscribe({
        next: () => {
          this.adminActionMessage = `Usuario "${username}" inscripto correctamente.`;
          this.isAdminActionError = false;
          this.memberUsername = '';
          this.loadActivityDetail();
        },
        error: (e) => {
          console.log('Error al inscribir miembro como admin:', e);
          
          let errorMessage = 'No se pudo inscribir al usuario. Verifique el nombre y vuelva a intentar.';
          
          if (e.error) {
            try {
              const errorObj = typeof e.error === 'string' ? JSON.parse(e.error) : e.error;
              
              if (errorObj.details && errorObj.details.message) {
                errorMessage = errorObj.details.message;
              } else if (errorObj.message) {
                errorMessage = errorObj.message;
              }
            } catch (parseError) {
              console.error('Error al parsear mensaje de error:', parseError);
            }
          }
          
          this.adminActionMessage = errorMessage;
          this.isAdminActionError = true;
        }
      })
    }
  }

  enrollMemberByInstructor(): void {
    if (!this.activityId || !this.memberIdToEnroll) {
      this.instructorActionMessage = 'Debe ingresar un ID de socio.';
      this.isInstructorActionError = true;
      return;
    }
  
    this.instructorService.enrollMemberToMyActivity(this.activityId, this.memberIdToEnroll).subscribe({
      next: () => {
        this.instructorActionMessage = `Socio ID ${this.memberIdToEnroll} inscrito correctamente.`;
        this.isInstructorActionError = false;
        this.memberIdToEnroll = null; 
        this.loadActivityDetail(); 
      },
      error: (e) => {
        console.error('Error al inscribir socio (Instructor):', e);
        let errorMessage = 'Error al inscribir. Verifique el ID o si ya está inscrito.';
        this.instructorActionMessage = errorMessage;
        this.isInstructorActionError = true;
      }
    });
  }

  enrollMemberByUsernameByInstructor(): void {
    if (!this.activityId) return;
    const usernameToEnroll = this.memberUsernameToEnroll.trim();

    if (!usernameToEnroll) {
        this.instructorActionMessage = 'Debe ingresar un nombre de usuario (username).';
        this.isInstructorActionError = true;
        return;
    }
    this.instructorService.enrollMemberByUsername(this.activityId, usernameToEnroll).subscribe({
        next: (response) => {
            this.instructorActionMessage = response; 
            this.isInstructorActionError = false;
            this.memberUsernameToEnroll = ''; 
            this.loadActivityDetail(); 
        },
        error: (e) => {
            console.error('Error al inscribir socio (Instructor):', e);
            let errorMessage = e.error || 'Error al inscribir. Verifique el username.';    
            this.instructorActionMessage = errorMessage;
            this.isInstructorActionError = true;
        }
    });
  }
}
