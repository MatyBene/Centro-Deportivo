import { Component } from '@angular/core';
import { Member } from '../../models/Member';
import { InstructorService } from '../../services/instructor-service';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-member-detail-page',
  imports: [],
  templateUrl: './member-detail-page.html',
  styleUrl: './member-detail-page.css'
})
export class MemberDetailPage {
member: Member | undefined;
  isLoading: boolean = true;
  error: string | null = null;

  constructor(
    private instructorService: InstructorService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.loadMember(params.get('id'));
    });
  }

  loadMember(memberId: string | null): void {
    if (!memberId) {
      this.error = 'No se proporcionó un ID de socio.';
      this.isLoading = false;
      return;
    }
    
    const id = parseInt(memberId, 10);
    
    if (isNaN(id)) {
      this.error = 'ID de socio no válido.';
      this.isLoading = false;
      return;
    }

    this.isLoading = true;
    this.error = null;

    this.instructorService.getMemberProfileDetails(id).subscribe({
      next: (data) => {
        this.member = data;
        this.isLoading = false;
      },
      error: (e) => {
        console.error('Error al cargar perfil del Socio:', e);
        this.error = 'No se pudo cargar el perfil del socio. Verifique que el ID exista.';
        this.isLoading = false;
      }
    });
  }
}
