import { Component, OnInit } from '@angular/core';
import { Member } from '../../models/Member';
import Instructor from '../../models/Instructor';
import { Admin } from '../../models/Admin';
import { AdminService } from '../../services/admin-service';
import { Pagination } from '../../components/pagination/pagination';
import { UserSummaryItem } from '../../components/user-summary-item/user-summary-item';
import { UserFilters, UserFilterValues } from '../../components/user-filters/user-filters';

@Component({
  selector: 'app-user-list-page',
  imports: [UserSummaryItem, Pagination, UserFilters],
  templateUrl: './user-list-page.html',
  styleUrl: './user-list-page.css'
})
export class UserListPage implements OnInit{
  users: (Member | Instructor | Admin)[] = [];
  currentPage: number = 0;
  pageSize: number = 5;
  totalPages!: number;
  isLoading: boolean = false;
  showFilters: boolean = false;

  // Filtros
  selectedRole: string = '';
  selectedStatus: string = '';
  selectedPermission: string = '';

  constructor(private adminService: AdminService) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers() {
    this.isLoading = true;
    const startTime = Date.now();
    const minLoadingTime = 300;

    this.adminService.getUsers(
      this.currentPage, 
      this.pageSize,
      this.selectedRole || undefined,
      this.selectedStatus || undefined,
      this.selectedPermission || undefined
    ).subscribe({
      next: (data) => {
        const elapsedTime = Date.now() - startTime;
        const remainingTime = Math.max(0, minLoadingTime - elapsedTime);

        setTimeout(() => {
          this.users = data.content;
          this.totalPages = data.totalPages;
          this.isLoading = false;
        }, remainingTime)
      },
      error: (e) => {
        console.log('Error: ', e);
        const elapsedTime = Date.now() - startTime;
        const remainingTime = Math.max(0, minLoadingTime - elapsedTime);
        
        setTimeout(() => {
          this.isLoading = false;
        }, remainingTime);
      }
    })
  }

  onPageChange(page: number) {
    this.currentPage = page;
    this.loadUsers();
  }

  nextPage() {
    if(this.currentPage < this.totalPages - 1){
      this.currentPage++;
      this.loadUsers();
    }
  }

  previousPage() {
    if(this.currentPage > 0){
      this.currentPage--;
      this.loadUsers();
    }
  }

  get hasNextPage(): boolean {
    return this.currentPage < this.totalPages - 1;
  }

  get hasPreviousPage(): boolean {
    return this.currentPage > 0;
  }

  toggleFilters() {
    this.showFilters = !this.showFilters;
  }

  onFilterChange(filters: UserFilterValues) {
    this.selectedRole = filters.role;
    this.selectedStatus = filters.status;
    this.selectedPermission = filters.permission;
    this.currentPage = 0;
    this.loadUsers();
  }

  onClearFilters() {
    this.selectedRole = '';
    this.selectedStatus = '';
    this.selectedPermission = '';
    this.currentPage = 0;
    this.loadUsers();
  }
}
