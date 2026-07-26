import { Component} from '@angular/core';
import { RouterLink } from "@angular/router";
import { AuthService } from '../../services/auth-service';


@Component({
 selector: 'app-home-page',
 imports: [RouterLink],
 templateUrl: './home-page.html',
 styleUrl: './home-page.css',
})
export class HomePage {
  baseUrl = 'http://localhost:8080/activity-list';

  constructor(public authService: AuthService) {}
}