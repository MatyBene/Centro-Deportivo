import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MemberProfileCard } from './member-profile-card';

describe('MemberProfileCard', () => {
  let component: MemberProfileCard;
  let fixture: ComponentFixture<MemberProfileCard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MemberProfileCard]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MemberProfileCard);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
