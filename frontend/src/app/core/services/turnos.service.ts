import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Turno, CrearTurnoRequest } from '../models/turno.model';
import { Observable, tap } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class TurnosService {
  private http = inject(HttpClient);
  private base = environment.apiBaseUrl + '/turnos';

  listar(): Observable<Turno[]> {
    return this.http.get<Turno[]>(this.base);
  }

  pendientes(): Observable<Turno[]> {
    return this.http.get<Turno[]>(`${this.base}/pendientes`);
  }

  crear(req: CrearTurnoRequest): Observable<Turno> {
    return this.http.post<Turno>(this.base, req);
  }

  siguiente(): Observable<Turno> {
    console.log('📞 Llamando a GET /api/turnos/siguiente');
    return this.http.get<Turno>(`${this.base}/siguiente`).pipe(
      tap({
        next: (turno) => console.log('✅ Respuesta exitosa:', turno),
        error: (err) => console.error('❌ Error en la petición:', err)
      })
    );
  }

  finalizar(id: number): Observable<Turno> {
    return this.http.put<Turno>(`${this.base}/${id}/finalizar`, {});
  }

  cancelar(id: number): Observable<Turno> {
    return this.http.put<Turno>(`${this.base}/${id}/cancelar`, {});
  }
}
