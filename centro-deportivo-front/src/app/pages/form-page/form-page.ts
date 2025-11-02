import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MemberService } from '../../services/member-service';
import { Router } from '@angular/router';
import { FieldError } from '../../components/field-error/field-error';
import { CustomValidators } from '../../utils/custom-validators';
import { AdminService } from '../../services/admin-service';
import { Admin } from '../../models/Admin';

@Component({
  selector: 'app-form-page',
  imports: [ReactiveFormsModule, FieldError],
  templateUrl: './form-page.html',
  styleUrl: './form-page.css'
})
export class FormPage implements OnInit{
  userForm!: FormGroup;
  isEditMode: boolean = false;
  isAdminRegisterMode: boolean = false;
  currentAdmin: Admin | null = null;
  serverErrors: { [key: string]: string } = {};

  constructor(
    private memberService: MemberService,
    private adminService: AdminService,
    private fb: FormBuilder,
    private router: Router
  ){}

  ngOnInit(): void {
    this.isEditMode = this.router.url.includes('/profile/edit');
    this.isAdminRegisterMode = this.router.url.includes('/admin/register');

    if (this.isAdminRegisterMode) {
      this.adminService.getAdmin().subscribe({
        next: (admin) => {this.currentAdmin = admin},
        error: (e) => {console.log('Error al obtener admin:', e)}
      });
    }

    if (this.isEditMode) {
      this.userForm = this.fb.group({
        name: ['', [Validators.required, CustomValidators.noWhitespace]],
        lastname: ['', [Validators.required, CustomValidators.noWhitespace]],
        dni: ['', [Validators.required, Validators.minLength(8), CustomValidators.noWhitespace]],
        birthdate: ['', [Validators.required, Validators.pattern(/^\d{4}-\d{2}-\d{2}$/)]],
        phone: ['', [Validators.required, Validators.maxLength(15), CustomValidators.noWhitespace]],
        email: ['', [Validators.required, Validators.email]]
      });
      this.loadMemberData();
    } else {
      this.userForm = this.fb.group({
        name: ['', [Validators.required, CustomValidators.noWhitespace]],
        lastname: ['', [Validators.required, CustomValidators.noWhitespace]],
        dni: ['', [Validators.required, Validators.minLength(8), CustomValidators.noWhitespace]],
        birthdate: ['', [Validators.required, Validators.pattern(/^\d{4}-\d{2}-\d{2}$/)]],
        phone: ['', [Validators.required, Validators.maxLength(15), CustomValidators.noWhitespace]],
        email: ['', [Validators.required, Validators.email]],
        username: ['', [Validators.required, CustomValidators.noWhitespace]],
        password: ['', [Validators.required, Validators.minLength(8), Validators.pattern(/^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\S+$).{8,}$/)]],
        role: ['MEMBER', Validators.required],
        specialty: [''],
        permissionLevel: [null]
      });
    }

    this.userForm.get('role')?.valueChanges.subscribe(role => {
      const specialtyControl = this.userForm.get('specialty');
      const permissionLevelControl = this.userForm.get('permissionLevel');
      
      if (role === 'INSTRUCTOR') {
        specialtyControl?.setValidators([Validators.required, CustomValidators.noWhitespace]);
        permissionLevelControl?.clearValidators();
      } else if (role === 'ADMIN') {
        permissionLevelControl?.setValidators([Validators.required]);
        specialtyControl?.clearValidators();
      } else {
        specialtyControl?.clearValidators();
        permissionLevelControl?.clearValidators();
      }
      
      specialtyControl?.updateValueAndValidity();
      permissionLevelControl?.updateValueAndValidity();
    });

    this.userForm.valueChanges.subscribe(() => {
      Object.keys(this.userForm.controls).forEach(key => {
        const control = this.userForm.get(key);
        if (control?.dirty && this.serverErrors[key]) {
          delete this.serverErrors[key];
        }
      });
    });
  }

  loadMemberData(): void {
    this.memberService.getMember().subscribe({
      next: (member) => {
        this.userForm.patchValue({
          name: member.name,
          lastname: member.lastname,
          dni: member.dni,
          birthdate: member.birthdate,
          phone: member.phone,
          email: member.email
        });
      },
      error: (e) => {
        console.log('Error al cargar datos del member:', e);
      }
    });
  }

  get name() { return this.userForm.get('name') }
  get lastname() { return this.userForm.get('lastname') }
  get dni() { return this.userForm.get('dni') }
  get birthdate() { return this.userForm.get('birthdate') }
  get phone() { return this.userForm.get('phone') }
  get email() { return this.userForm.get('email') }
  get username() { return this.userForm.get('username') }
  get password() { return this.userForm.get('password') }
  get role() { return this.userForm.get('role') }
  get specialty() { return this.userForm.get('specialty') }
  get permissionLevel() { return this.userForm.get('permissionLevel') }

  onSubmit(): void {
    const formValue = { ...this.userForm.value };
    Object.keys(formValue).forEach(key => {
      if (typeof formValue[key] === 'string') {
        formValue[key] = formValue[key].trim();
      }
    });

    if (this.isEditMode) {
      this.memberService.putMember(formValue).subscribe({
        next: () => {
          this.router.navigate(['/profile']);
        },
        error: (e) => {
          console.log('Error al actualizar perfil:', e);
          this.handleServerError(e);
        }
      });
    } else if(this.isAdminRegisterMode) {
      this.registerUserByRole(formValue);
    } else {
      console.log('Datos enviados al backend:', formValue);
      this.memberService.register(formValue).subscribe({
        next: () => {
          this.router.navigate(['/public/login']);
        },
        error: (e) => {
          this.handleServerError(e);
        }
      });
    }
  }

  registerUserByRole(formValue: any) {
    const role = formValue.role;

    switch(role) {
      case 'MEMBER':
        console.log('FORM MEMBER: ', formValue);
        this.adminService.registerMember(formValue).subscribe({
          next: () => {this.router.navigate(['/'])}, // REDIRIGIR AL PERFIL DEL SOCIO CREADO
          error: (e) => {this.handleServerError(e)}
        });
        break;

      case 'INSTRUCTOR':
        this.adminService.registerInstructor(formValue).subscribe({
          next: () => {this.router.navigate(['/'])}, // REDIRIGIR AL PERFIL DEL INSTRUCTOR CREADO
          error: (e) => {
            console.log('ERROR: ', e)
            this.handleServerError(e)}
        })
        break;
      
      case 'ADMIN':
        this.adminService.registerAdmin(formValue).subscribe({
          next: () => {this.router.navigate(['/'])}, // REDIRIGIR AL PERFIL DEL ADMINISTRADOR CREADO
          error: (e) => {this.handleServerError(e)}
        })
        break;
    }
  }

  handleServerError(e: any) {
    let errorData = e.error;
    if (typeof e.error === 'string') {
      try {
        errorData = JSON.parse(e.error);
      } catch (parseError) {
        console.log('No se pudo parsear el error');
      }
    }
    
    if (errorData?.details?.field && errorData?.details?.message) {
      this.serverErrors[errorData.details.field] = errorData.details.message;
    }
  }
}
