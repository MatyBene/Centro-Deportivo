import { Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth-service';

@Component({
  selector: 'app-header',
  imports: [RouterLink],
  templateUrl: './header.html',
  styleUrl: './header.css'
})
export class Header {
  menuOpen: boolean = false;
  
  constructor(public authService: AuthService, private router: Router){}

  toggleMenu() {
    this.menuOpen = !this.menuOpen;
  }

  closeMenu() {
    this.menuOpen = false;
  }

  logout(){
    this.authService.logout();
    this.router.navigate(['/']);
    this.closeMenu();
  }
}
