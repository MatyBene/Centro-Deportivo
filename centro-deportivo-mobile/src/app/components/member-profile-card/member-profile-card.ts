import { Component, input } from '@angular/core';
import { Member } from '../../models/Member';
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
  selector: 'app-member-profile-card',
  templateUrl: './member-profile-card.html',
  styleUrls: ['./member-profile-card.scss'],
  imports: [IonCard, IonCardHeader, IonCardTitle, IonCardContent, IonList, IonItem, IonLabel, IonBadge],
})
export class MemberProfileCard {
  member = input.required<Member>();
}
