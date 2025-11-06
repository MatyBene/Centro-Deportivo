import { Component, OnInit } from '@angular/core';
import { Member } from '../../models/Member';
import { InstructorService } from '../../services/instructor-service';
import { PageableResponse } from '../../models/Pageable';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-member-list-page',
  imports: [RouterLink],
  templateUrl: './member-list-page.html',
  styleUrl: './member-list-page.css'
})
export class MemberListPage implements OnInit{
  currentPage: number = 0;
  pageSize: number = 5;
  totalPages: number = 0;
  totalElements: number = 0;
  members: Member[] = [];
  isLoading: boolean = true;
  error: string | null = null;

  constructor(private instructorService: InstructorService){}

  ngOnInit(): void {
    this.loadMembers();
  }

  loadMembers(): void{
    this.isLoading = true;
    this.error = null;
    this.instructorService.getAllMembers(this.currentPage, this.pageSize).subscribe({
      next: (response: PageableResponse<Member>) => {
        this.members = response.content;
        this.totalPages = response.totalPages;
        this.totalElements = response.totalElements;
        this.currentPage = response.number;
        this.isLoading = false;
      } ,
      error: (e) => {
        console.error('Error al cargar socios', e);
        this.error = 'No se pudo cargar la lista de socios. Verifica tus permisos.';
        this.isLoading = false;
      }
    })
  }
  goToPage(page: number): void {
    if (page >= 0 && page < this.totalPages) {
      this.currentPage = page;
      this.loadMembers();
    }
  }

  nextPage(): void {
    this.goToPage(this.currentPage + 1);
  }

  prevPage(): void {
    this.goToPage(this.currentPage - 1);
  }
}
