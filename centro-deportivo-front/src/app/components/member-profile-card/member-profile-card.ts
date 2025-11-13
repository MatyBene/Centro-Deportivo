import { Component, input, output } from '@angular/core';
import { Member } from '../../models/Member';
import { AdminService } from '../../services/admin-service';
import { InstructorService } from '../../services/instructor-service';
import { AuthService } from '../../services/auth-service';

@Component({
  selector: 'app-member-profile-card',
  imports: [],
  templateUrl: './member-profile-card.html',
  styleUrl: './member-profile-card.css'
})
export class MemberProfileCard {
    member = input.required<Member>();
  instructorActivities = input<number[]>([]);
  memberUpdated = output<void>();

  constructor(
    private adminService: AdminService,
    private instructorService: InstructorService,
    private authService: AuthService
  ) {}

  unerollment(activityId: string) {
    const username = this.member().username;
    const role = this.authService.getUserRole(); 

    console.log('Rol actual:', role);

    let request$;

    if (role === 'ADMIN') {
      request$ = this.adminService.unenrollMemberToActivity(activityId, username);
    } else if (role === 'INSTRUCTOR') {
      request$ = this.instructorService.unenrollMemberByUsername(Number(activityId), username);
    } else {
      console.warn('Rol sin permisos para dar de baja.');
      return;
    }

    request$.subscribe({
      next: () => this.memberUpdated.emit(),
      error: (e) => console.log('Error:', e)
    });
  }

  canUnenroll(activityId: number): boolean {
    const activities = this.instructorActivities() || [];
    return activities.includes(activityId);
  }
}

