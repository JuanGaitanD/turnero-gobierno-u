import { Component, signal, inject } from '@angular/core';
import { CommonModule, DatePipe, NgClass } from '@angular/common';
import { TurnosService } from '../../core/services/turnos.service';
import { Turno } from '../../core/models/turno.model';

@Component({
  selector: 'app-historial-turnos',
  standalone: true,
  imports: [CommonModule, DatePipe, NgClass],
  templateUrl: './historial-turnos.html',
  styleUrl: './historial-turnos.css',
})
export class HistorialTurnos {
  private turnosSrv = inject(TurnosService);
  historial = signal<Turno[]>([]);
  loading = signal<boolean>(false);
  error = signal<string | null>(null);

  constructor() {
    this.cargarHistorial();
  }

  cargarHistorial() {
    this.loading.set(true);
    this.error.set(null);
    this.turnosSrv.historial().subscribe({
      next: data => {
        this.historial.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Error cargando historial');
        this.loading.set(false);
      }
    });
  }
}
