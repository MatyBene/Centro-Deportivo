import { Component, input } from '@angular/core';
import { Admin } from '../../models/Admin';

@Component({
  selector: 'app-admin-profile-card',
  imports: [],
  templateUrl: './admin-profile-card.html',
  styleUrl: './admin-profile-card.css'
})
export class AdminProfileCard {
  admin = input.required<Admin>();
}
