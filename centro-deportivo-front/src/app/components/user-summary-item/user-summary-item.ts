import { Component, input } from '@angular/core';
import { Member } from '../../models/Member';
import Instructor from '../../models/Instructor';
import { Admin } from '../../models/Admin';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth-service';

@Component({
  selector: 'app-user-summary-item',
  imports: [],
  templateUrl: './user-summary-item.html',
  styleUrl: './user-summary-item.css'
})
export class UserSummaryItem {
  user = input<Member | Instructor | Admin>();

  constructor(private router: Router, private authService: AuthService) {};

  isAdmin(user: Member | Instructor | Admin | undefined): user is Admin {
    if (!user) return false;
    return (user as any).role === 'ADMIN';
  }

  isInstructor(user: Member | Instructor | Admin | undefined): user is Instructor {
    if (!user) return false;
    return (user as any).role === 'INSTRUCTOR';
  }

  isMember(user: Member | Instructor | Admin | undefined): user is Member {
    if (!user) return false;
    return (user as any).role === 'MEMBER' || (!(user as any).role && (user as any).status !== null);
  }

  getAdmin(): Admin | undefined {
    const currentUser = this.user();
    return this.isAdmin(currentUser) ? currentUser : undefined;
  }

  getInstructor(): Instructor | undefined {
    const currentUser = this.user();
    return this.isInstructor(currentUser) ? currentUser : undefined;
  }

  getMember(): Member | undefined {
    const currentUser = this.user();
    return this.isMember(currentUser) ? currentUser : undefined;
  }

  getRole(): string {
    const role = (this.user() as any)?.role;
    switch(role) {
      case 'ADMIN': return 'Administrador';
      case 'INSTRUCTOR': return 'Instructor';
      case 'MEMBER': return 'Miembro';
      default: return '';
    }
  }

  goToDetail() {
    const selectedUser = this.user();
    const role = this.authService.getUserRole(); 

    if (!selectedUser || !role) {
      console.warn('Usuario no definido o no logueado');
      return;
    }

    if (role === 'ADMIN') {
      this.router.navigate(['/users', selectedUser.username]);
    } else if (role === 'INSTRUCTOR') {
      this.router.navigate(['/instructors/members', selectedUser.id]);
    }
  }
}
