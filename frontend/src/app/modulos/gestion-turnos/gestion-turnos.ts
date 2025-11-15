import { Component, signal, inject } from '@angular/core';
import { CommonModule, DatePipe, NgClass } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TurnosService } from '../../core/services/turnos.service';
import { UsuariosService } from '../../core/services/usuarios.service';
import { Turno, CrearTurnoRequest } from '../../core/models/turno.model';
import { Usuario } from '../../core/models/usuario.model';

@Component({
  selector: 'app-gestion-turnos',
  imports: [CommonModule, FormsModule, NgClass, DatePipe],
  templateUrl: './gestion-turnos.html',
  styleUrl: './gestion-turnos.css',
})
export class GestionTurnos {
  // Inyección adecuada de servicios para permitir HttpClient y tree-shaking
  private turnosSrv = inject(TurnosService);
  private usuariosSrv = inject(UsuariosService);

  pendientes = signal<Turno[]>([]);
  usuarios = signal<Usuario[]>([]);
  siguiente = signal<Turno | null>(null);
  loadingPendientes = signal<boolean>(false);
  loadingAccion = signal<boolean>(false);
  error = signal<string | null>(null);
  success = signal<string | null>(null);

  nuevoTurno: CrearTurnoRequest = { usuarioId: 0, servicio: 'tramite_vivienda' };

  constructor() {
    this.cargarPendientes();
    this.usuariosSrv.listar().subscribe(data => this.usuarios.set(data));
  }

  cargarPendientes() {
    this.loadingPendientes.set(true);
    this.error.set(null);
    this.turnosSrv.pendientes().subscribe({
      next: data => {
        this.pendientes.set(data);
        this.loadingPendientes.set(false);
      },
      error: () => {
        this.error.set('Error cargando pendientes');
        this.loadingPendientes.set(false);
      }
    });
  }

  crearTurno() {
    if (!this.nuevoTurno.usuarioId) return;
    console.log('Creando turno con datos:', this.nuevoTurno);
    this.loadingAccion.set(true);
    this.error.set(null);
    this.success.set(null);
    this.turnosSrv.crear(this.nuevoTurno).subscribe({
      next: () => {
        this.success.set('Turno creado');
        this.nuevoTurno = { usuarioId: 0, servicio: 'tramite_vivienda' };
        this.loadingAccion.set(false);
        this.cargarPendientes();
      },
      error: (err) => {
        console.error('Error al crear turno:', err);
        this.error.set('No se pudo crear turno');
        this.loadingAccion.set(false);
      }
    });
  }

  obtenerSiguiente() {
    console.log('Obteniendo siguiente turno...');
    this.loadingAccion.set(true);
    this.error.set(null);
    this.success.set(null);
    this.turnosSrv.siguiente().subscribe({
      next: t => {
        console.log('Siguiente turno obtenido:', t);
        this.siguiente.set(t);
        this.success.set('Turno en atención');
        this.loadingAccion.set(false);
        this.cargarPendientes();
      },
      error: (err) => {
        console.error('Error al obtener siguiente turno:', err);
        this.error.set(err.status === 204 ? 'No hay turnos pendientes' : 'Error al obtener siguiente turno');
        this.siguiente.set(null);
        this.loadingAccion.set(false);
      }
    });
  }

  finalizarTurno() {
    const turno = this.siguiente();
    if (!turno) return;
    this.loadingAccion.set(true);
    this.error.set(null);
    this.success.set(null);
    this.turnosSrv.finalizar(turno.id).subscribe({
      next: () => {
        this.success.set('Turno finalizado');
        this.siguiente.set(null);
        this.loadingAccion.set(false);
        this.cargarPendientes();
      },
      error: () => {
        this.error.set('No se pudo finalizar');
        this.loadingAccion.set(false);
      }
    });
  }

  cancelarTurno() {
    const turno = this.siguiente();
    if (!turno) return;
    this.loadingAccion.set(true);
    this.error.set(null);
    this.success.set(null);
    this.turnosSrv.cancelar(turno.id).subscribe({
      next: () => {
        this.success.set('Turno cancelado');
        this.siguiente.set(null);
        this.loadingAccion.set(false);
        this.cargarPendientes();
      },
      error: () => {
        this.error.set('No se pudo cancelar');
        this.loadingAccion.set(false);
      }
    });
  }
}
