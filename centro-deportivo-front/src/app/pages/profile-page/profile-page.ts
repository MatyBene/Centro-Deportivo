import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth-service';
import { Member } from '../../models/Member';
import { MemberService } from '../../services/member-service';
import { Router, RouterLink } from '@angular/router';
import { Admin } from '../../models/Admin';
import { AdminService } from '../../services/admin-service';
import { MemberProfileCard } from '../../components/member-profile-card/member-profile-card';
import { AdminProfileCard } from '../../components/admin-profile-card/admin-profile-card';
import { InstructorProfileCard } from '../../components/instructor-profile-card/instructor-profile-card';
import { InstructorService } from '../../services/instructor-service';
import Instructor from '../../models/Instructor';

@Component({
  selector: 'app-profile-page',
  imports: [RouterLink, MemberProfileCard, AdminProfileCard, InstructorProfileCard],
  templateUrl: './profile-page.html',
  styleUrl: './profile-page.css'
})
export class ProfilePage implements OnInit{
  member!: Member;
  admin!: Admin;
  instructor!: Instructor;

  constructor(
    public authService: AuthService, 
    public memberService: MemberService, 
    public adminService: AdminService,
    public instructorService: InstructorService,
    private router: Router){}

  ngOnInit(): void {
    this.showUser();
  }

  showUser() {
    if(this.authService.getUserRole() === 'MEMBER') {
      this.memberService.getMember().subscribe({
        next: (data) => {this.member = data},
        error: (e) => {console.log('ERROR: ', e)}
      })
    }

    if(this.authService.getUserRole() === 'ADMIN') {
      this.adminService.getAdmin().subscribe({
        next: (data) => {this.admin = data},
        error: (e) => {console.log('ERROR: ', e)}
      })
    }

    if(this.authService.getUserRole() === 'INSTRUCTOR') {
      this.instructorService.getProfile().subscribe({
        next: (data) => {this.instructor = data},
        error: (e) => {console.log('ERROR: ', e)}
      })
    }
  }

  removeMember() {
    if(this.authService.getUserRole() === 'MEMBER') {
      this.memberService.deleteMember().subscribe({
        next: (data) => {
          this.authService.logout();
          this.router.navigate(['/']);
        },
        error: (e) => {console.log('ERROR: ', e)}
      })
    }
  }
}
