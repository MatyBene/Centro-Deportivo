import { Component, input } from '@angular/core';
import Instructor from '../../models/Instructor';

@Component({
  selector: 'app-instructor-profile-card',
  imports: [],
  templateUrl: './instructor-profile-card.html',
  styleUrl: './instructor-profile-card.css'
})
export class InstructorProfileCard {
  instructor = input.required<Instructor>();
}
