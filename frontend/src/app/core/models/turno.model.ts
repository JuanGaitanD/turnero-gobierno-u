export type Servicio = 'tramite_vivienda' | 'atencion_tributaria' | 'pqrs';
export type EstadoTurno = 'PENDIENTE' | 'EN_ATENCION' | 'ATENDIDO' | 'CANCELADO';

export interface Turno {
  id: number;
  numeroTurno: string;
  usuarioNombre: string;
  usuarioDni: string;
  usuarioEdad: number;
  servicio: Servicio;
  estado: EstadoTurno;
  prioridad: number;
  fechaCreacion: string;
  fechaAtencion?: string | null;
}

export interface CrearTurnoRequest {
  usuarioId: number;
  servicio: Servicio;
}
