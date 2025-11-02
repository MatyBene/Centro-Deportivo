import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth-service';
import { Member } from '../../models/Member';
import { MemberService } from '../../services/member-service';
import { Router, RouterLink } from '@angular/router';
import { Admin } from '../../models/Admin';
import { AdminService } from '../../services/admin-service';
import { MemberProfileCard } from '../../components/member-profile-card/member-profile-card';
import { AdminProfileCard } from '../../components/admin-profile-card/admin-profile-card';

@Component({
  selector: 'app-profile-page',
  imports: [RouterLink, MemberProfileCard, AdminProfileCard],
  templateUrl: './profile-page.html',
  styleUrl: './profile-page.css'
})
export class ProfilePage implements OnInit{
  member!: Member;
  admin!: Admin;

  constructor(
    public authService: AuthService, 
    public memberService: MemberService, 
    public adminService: AdminService,
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
