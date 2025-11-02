import { Component, output, signal, computed } from '@angular/core';

export interface UserFilterValues {
  role: string;
  status: string;
  permission: string;
}

@Component({
  selector: 'app-user-filters',
  imports: [],
  templateUrl: './user-filters.html',
  styleUrl: './user-filters.css'
})
export class UserFilters {

  filterChange = output<UserFilterValues>();
  clearFilters = output<void>();

  selectedRole = signal('');
  selectedStatus = signal('');
  selectedPermission = signal('');

  showStatusFilter = computed(() => this.selectedRole() === 'MEMBER');
  showPermissionFilter = computed(() => this.selectedRole() === 'ADMIN');

  onRoleChange(event: Event) {
    const value = (event.target as HTMLSelectElement).value;
    this.selectedRole.set(value);
    this.selectedStatus.set('');
    this.selectedPermission.set('');
    this.emitFilterChange();
  }

  onStatusChange(event: Event) {
    const value = (event.target as HTMLSelectElement).value;
    this.selectedStatus.set(value);
    this.emitFilterChange();
  }

  onPermissionChange(event: Event) {
    const value = (event.target as HTMLSelectElement).value;
    this.selectedPermission.set(value);
    this.emitFilterChange();
  }

  onClearFilters() {
    this.selectedRole.set('');
    this.selectedStatus.set('');
    this.selectedPermission.set('');
    this.clearFilters.emit();
  }

  private emitFilterChange() {
    this.filterChange.emit({
      role: this.selectedRole(),
      status: this.selectedStatus(),
      permission: this.selectedPermission()
    });
  }
}
