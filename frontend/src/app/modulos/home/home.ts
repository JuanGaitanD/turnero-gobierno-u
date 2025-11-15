import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UsuariosService } from '../../core/services/usuarios.service';
import { TurnosService } from '../../core/services/turnos.service';

@Component({
  selector: 'app-home',
  imports: [CommonModule],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home {
  private usuariosSrv = inject(UsuariosService);
  private turnosSrv = inject(TurnosService);

  usuariosCount = signal<number>(0);
  pendientesCount = signal<number>(0);
  loading = signal<boolean>(false);
  error = signal<string | null>(null);

  constructor() {
    this.cargarResumen();
  }

  cargarResumen() {
    this.loading.set(true);
    this.error.set(null);
    // Ejecutamos dos requests en paralelo
    this.usuariosSrv.listar().subscribe({
      next: data => this.usuariosCount.set(data.length),
      error: () => this.error.set('Error usuarios')
    });
    this.turnosSrv.pendientes().subscribe({
      next: data => this.pendientesCount.set(data.length),
      error: () => this.error.set('Error turnos'),
      complete: () => this.loading.set(false)
    });
  }
}
