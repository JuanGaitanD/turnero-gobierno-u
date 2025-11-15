import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GestionTurnos } from './gestion-turnos';

describe('GestionTurnos', () => {
  let component: GestionTurnos;
  let fixture: ComponentFixture<GestionTurnos>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GestionTurnos]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GestionTurnos);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
