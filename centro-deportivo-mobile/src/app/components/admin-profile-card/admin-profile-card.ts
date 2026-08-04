import { Component, input } from '@angular/core';
import { Admin } from '../../models/Admin';
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
  selector: 'app-admin-profile-card',
  templateUrl: './admin-profile-card.html',
  styleUrls: ['./admin-profile-card.scss'],
  imports: [IonCard, IonCardHeader, IonCardTitle, IonCardContent, IonList, IonItem, IonLabel, IonBadge],
})
export class AdminProfileCard {
  admin = input.required<Admin>();
}
