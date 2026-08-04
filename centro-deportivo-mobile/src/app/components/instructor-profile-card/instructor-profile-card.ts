import { Component, input } from '@angular/core';
import Instructor from '../../models/Instructor';
import {
  IonCard,
  IonCardHeader,
  IonCardTitle,
  IonCardContent,
  IonList,
  IonItem,
  IonLabel,
  IonBadge,
} from '@ionic/angular/standalone';

@Component({
  selector: 'app-instructor-profile-card',
  templateUrl: './instructor-profile-card.html',
  styleUrls: ['./instructor-profile-card.scss'],
  imports: [IonCard, IonCardHeader, IonCardTitle, IonCardContent, IonList, IonItem, IonLabel, IonBadge],
})
export class InstructorProfileCard {
  instructor = input.required<Instructor>();
}
