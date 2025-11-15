export interface Usuario {
  id: number;
  dni: string;
  nombreCompleto: string;
  genero: 'H' | 'M';
  fechaNacimiento: string; // ISO
  edad: number;
}

export interface UsuarioRequest {
  dni: string;
  nombreCompleto: string;
  genero: 'H' | 'M';
  fechaNacimiento: string;
}
