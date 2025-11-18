import { Component, OnInit } from '@angular/core';
import { Member } from '../../models/Member';
import Instructor from '../../models/Instructor';
import { Admin } from '../../models/Admin';
import { AdminService } from '../../services/admin-service';
import { ActivatedRoute, Router } from '@angular/router';
import { MemberProfileCard } from '../../components/member-profile-card/member-profile-card';
import { AdminProfileCard } from '../../components/admin-profile-card/admin-profile-card';
import { InstructorProfileCard } from "../../components/instructor-profile-card/instructor-profile-card";

@Component({
  selector: 'app-user-detail-page',
  imports: [MemberProfileCard, AdminProfileCard,InstructorProfileCard],
  templateUrl: './user-detail-page.html',
  styleUrl: './user-detail-page.css'
})
export class UserDetailPage implements OnInit{
  member!: Member;
  instructor!: Instructor;
  admin!: Admin;
  showDeleteModal = false;
  isDeleting = false;

  constructor(
    public adminService: AdminService, 
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadUser();
  }

  loadUser() {
    const username = this.route.snapshot.params['user'];

    this.adminService.getUserDetail(username).subscribe({
      next: (data) => {
        if(data.role === 'MEMBER') {
          this.member = data as Member;
        } else if(data.role === 'INSTRUCTOR') {
          this.instructor = data as Instructor;
        } else if(data.role === 'ADMIN') {
          this.admin = data as Admin;
        }
      }
    })
  }

  removeUser() {
    this.showDeleteModal = true;
  }

  confirmDelete() {
    const username = this.route.snapshot.params['user'];
    this.isDeleting = true;

    this.adminService.deleteUser(username).subscribe({
      next: (data) => {
        this.isDeleting = false;
        this.showDeleteModal = false;
        this.router.navigate(['/users']);
      },
      error: (e) => {
        console.log(e);
        this.isDeleting = false;
        alert('Error al eliminar el usuario');
      }
    })
  }

  cancelDelete() {
    this.showDeleteModal = false;
  }
}
