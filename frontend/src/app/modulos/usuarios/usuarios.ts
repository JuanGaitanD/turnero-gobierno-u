import { Component, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UsuariosService } from '../../core/services/usuarios.service';
import { Usuario, UsuarioRequest } from '../../core/models/usuario.model';

@Component({
  selector: 'app-usuarios',
  imports: [CommonModule, FormsModule],
  templateUrl: './usuarios.html',
  styleUrl: './usuarios.css',
})
export class Usuarios {
  // Inyección adecuada del servicio (evita instanciar manualmente y permite HttpClient DI)
  private usuariosSrv = inject(UsuariosService);

  usuarios = signal<Usuario[]>([]);
  loading = signal<boolean>(false);
  error = signal<string | null>(null);
  success = signal<string | null>(null);
  editId: number | null = null;
  form: UsuarioRequest = {
    dni: '',
    nombreCompleto: '',
    genero: 'H',
    fechaNacimiento: ''
  };

  constructor() {
    this.cargar();
  }

  cargar() {
    this.loading.set(true);
    this.error.set(null);
    this.usuariosSrv.listar().subscribe({
      next: data => {
        this.usuarios.set(data);
        this.loading.set(false);
      },
      error: err => {
        this.error.set('Error cargando usuarios');
        this.loading.set(false);
      }
    });
  }

  guardar() {
    console.log('Guardando usuario con datos:', this.form);
    this.loading.set(true);
    this.error.set(null);
    this.success.set(null);
    const obs = this.editId
      ? this.usuariosSrv.actualizar(this.editId, this.form)
      : this.usuariosSrv.crear(this.form);
    obs.subscribe({
      next: () => {
        this.success.set(this.editId ? 'Usuario actualizado' : 'Usuario creado');
        this.reset();
        this.cargar();
      },
      error: (err) => {
        console.error('Error al guardar usuario:', err);
        this.error.set('No se pudo guardar');
        this.loading.set(false);
      }
    });
  }

  editar(u: Usuario) {
    this.editId = u.id;
    this.form = {
      dni: u.dni,
      nombreCompleto: u.nombreCompleto,
      genero: u.genero,
      fechaNacimiento: u.fechaNacimiento
    };
  }

  eliminar(u: Usuario) {
    if (confirm('Eliminar usuario?')) {
      this.loading.set(true);
      this.error.set(null);
      this.success.set(null);
      this.usuariosSrv.eliminar(u.id).subscribe({
        next: () => {
          this.success.set('Usuario eliminado');
          this.cargar();
        },
        error: () => {
          this.error.set('No se pudo eliminar');
          this.loading.set(false);
        }
      });
    }
  }

  reset() {
    this.editId = null;
    this.form = { dni: '', nombreCompleto: '', genero: 'H', fechaNacimiento: '' };
  }
}
