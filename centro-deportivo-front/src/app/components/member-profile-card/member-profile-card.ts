import { Component, input, output } from '@angular/core';
import { Member } from '../../models/Member';
import { AdminService } from '../../services/admin-service';

@Component({
  selector: 'app-member-profile-card',
  imports: [],
  templateUrl: './member-profile-card.html',
  styleUrl: './member-profile-card.css'
})
export class MemberProfileCard {
  member = input.required<Member>();
  memberUpdated = output<void>();

  constructor(private adminService: AdminService) {}

  unerollment(activityId: string) {
    this.adminService.unenrollMemberToActivity(activityId, this.member().username).subscribe({
      next: () => {
        this.memberUpdated.emit();
      },
      error: (e) => {console.log(e)}
    })
  }
}
