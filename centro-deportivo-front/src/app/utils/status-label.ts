export function getStatusLabel(status: string | null | undefined): string {
  switch (status) {
    case 'ACTIVE':
      return 'Activo';
    case 'INACTIVE':
      return 'Inactivo';
    default:
      return status ?? '';
  }
}
