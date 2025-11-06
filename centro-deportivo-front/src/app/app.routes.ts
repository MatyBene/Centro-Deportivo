import { Routes } from '@angular/router';
import { HomePage } from './pages/home-page/home-page';
import { FormPage } from './pages/form-page/form-page';
import { LoginPage } from './pages/login-page/login-page';
import { ProfilePage } from './pages/profile-page/profile-page';
import { guestGuard } from './guards/guest-guard';
import { authGuard } from './guards/auth-guard';
import { ActivityListPage } from './pages/activity-list-page/activity-list-page';
import { ActivityDetailPage } from './pages/activity-detail-page/activity-detail-page';
import { InstructorActivitiesPage } from './pages/instructor-activities-page/instructor-activities-page';
import { MemberActivitiesPageComponent } from './pages/member-activities-page/member-activities-page';
import { InstructorDetailPage } from './pages/instructor-detail-page/instructor-detail-page';
import { UserListPage } from './pages/user-list-page/user-list-page';
import { UserDetailPage } from './pages/user-detail-page/user-detail-page';
import { MemberListPage } from './pages/member-list-page/member-list-page';

export const routes: Routes = [
    {path: '', component: HomePage},
    {path: 'public/login', component: LoginPage, canActivate: [guestGuard]},
    {path: 'public/register', component: FormPage, canActivate: [guestGuard]},

    {path: 'activity-list', component: ActivityListPage},
    {path: 'activity-list/my-activities', component: InstructorActivitiesPage, canActivate: [authGuard]},
    {path: 'activity-list/:id', component: ActivityDetailPage},

    {path: 'my-activities', component: MemberActivitiesPageComponent, canActivate: [authGuard]},

    {path: 'instructors/:id', component: InstructorDetailPage},
    {path: 'instructor/members', component: MemberListPage},

    {path: 'admin/register', component: FormPage, canActivate: [authGuard]},

    {path: 'users', component: UserListPage, canActivate: [authGuard]},
    {path: 'users/:user', component: UserDetailPage, canActivate: [authGuard]},

    {path: 'profile', component: ProfilePage, canActivate: [authGuard]},
    {path: 'profile/edit', component: FormPage, canActivate: [authGuard]}
];
