import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth-service';
import { Member } from '../../models/Member';
import { Admin } from '../../models/Admin';
import { MemberService } from '../../services/member-service';
import { AdminService } from '../../services/admin-service';
import { InstructorService } from '../../services/instructor-service';
import Instructor from '../../models/Instructor';
import { MemberProfileCard } from '../../components/member-profile-card/member-profile-card';
import { AdminProfileCard } from '../../components/admin-profile-card/admin-profile-card';
import { InstructorProfileCard } from '../../components/instructor-profile-card/instructor-profile-card';
import { AppFooter } from '../../components/app-footer/app-footer';
import {
  IonContent,
  IonButton,
  IonHeader,
  IonToolbar,
  IonTitle,
  IonSpinner,
  IonText,
} from '@ionic/angular/standalone';

@Component({
  selector: 'app-profile-page',
  templateUrl: './profile-page.html',
  styleUrls: ['./profile-page.scss'],
  imports: [
    IonContent,
    IonButton,
    IonHeader,
    IonToolbar,
    IonTitle,
    IonSpinner,
    IonText,
    MemberProfileCard,
    AdminProfileCard,
    InstructorProfileCard,
    AppFooter,
  ],
})
export class ProfilePage implements OnInit {
  member: Member | null = null;
  admin: Admin | null = null;
  instructor: Instructor | null = null;
  isLoading: boolean = true;
  errorMessage: string = '';

  constructor(
    public authService: AuthService,
    private memberService: MemberService,
    private adminService: AdminService,
    private instructorService: InstructorService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadUser();
  }

  loadUser() {
    const role = this.authService.getUserRole();

    if (role === 'MEMBER') {
      this.memberService.getMember().subscribe({
        next: (data) => {
          this.member = data;
          this.isLoading = false;
        },
        error: (e) => {
          this.errorMessage = 'No se pudo cargar el perfil';
          this.isLoading = false;
          console.error('Error:', e);
        },
      });
    } else if (role === 'ADMIN') {
      this.adminService.getAdmin().subscribe({
        next: (data) => {
          this.admin = data;
          this.isLoading = false;
        },
        error: (e) => {
          this.errorMessage = 'No se pudo cargar el perfil';
          this.isLoading = false;
          console.error('Error:', e);
        },
      });
    } else if (role === 'INSTRUCTOR') {
      this.instructorService.getProfile().subscribe({
        next: (data) => {
          this.instructor = data;
          this.isLoading = false;
        },
        error: (e) => {
          this.errorMessage = 'No se pudo cargar el perfil';
          this.isLoading = false;
          console.error('Error:', e);
        },
      });
    } else {
      this.errorMessage = 'Rol no reconocido';
      this.isLoading = false;
    }
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
