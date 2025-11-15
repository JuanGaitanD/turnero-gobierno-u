import { Routes } from '@angular/router';
import { Home } from './modulos/home/home';
import { Usuarios } from './modulos/usuarios/usuarios';
import { GestionTurnos } from './modulos/gestion-turnos/gestion-turnos';

export const routes: Routes = [
	{ path: '', component: Home },
	{ path: 'usuarios', component: Usuarios },
	{ path: 'turnos', component: GestionTurnos },
	{ path: '**', redirectTo: '' }
];
