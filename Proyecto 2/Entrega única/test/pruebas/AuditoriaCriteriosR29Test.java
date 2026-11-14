package pruebas;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import club.ClubDeportivoSeneca;
import deportes.Categoria;
import deportes.Deporte;
import deportes.Equipo;
import torneos.*;
import usuarios.*;

/** Evidencia adicional HU-ADM-04 y HU-JUG-03. */
class AuditoriaCriteriosR29Test {
    private ClubDeportivoSeneca club;
    private Administrador admin;
    private GestionTorneos gestion;
    private Deporte futbol;
    private Categoria sub15;
    private LocalDate fecha;

    @BeforeEach void preparar() {
        club = new ClubDeportivoSeneca("Auditoria R29");
        admin = new Administrador("admin-r29", "clave", "Admin R29");
        club.registrarUsuario(admin);
        gestion = club.getGestionTorneos();
        futbol = new Deporte("Futbol R29", 2, 3, 1);
        sub15 = new Categoria("Sub-15 R29", 10, 15);
        club.getAreaDeportiva().agregarDeporte(futbol);
        club.getAreaDeportiva().agregarCategoria(sub15);
        fecha = LocalDate.of(2026, 12, 5);
    }

    @Test void huAdm04RechazaTorneoDeConjuntoSinCategoria() {
        int antes = gestion.getTorneos().size();
        assertThrows(IllegalArgumentException.class, () ->
            gestion.crearTorneoRecreativo(admin, "Sin categoria R29", futbol,
                Torneo.Modalidad.CONJUNTO, null, null, Torneo.Formato.LIGA,
                5, fecha, fecha, 10));
        assertEquals(antes, gestion.getTorneos().size());
    }

    @Test void huJug03RechazaEquipoDeOtroDeporteSinOcuparCupo() {
        TorneoRecreativo torneo = gestion.crearTorneoRecreativo(admin, "Torneo futbol R29", futbol,
            Torneo.Modalidad.CONJUNTO, sub15, null, Torneo.Formato.LIGA,
            5, fecha, fecha, 10);
        Deporte baloncesto = new Deporte("Baloncesto R29", 2, 3, 1);
        club.getAreaDeportiva().agregarDeporte(baloncesto);
        Equipo equipo = club.getAreaDeportiva().crearEquipo("Equipo baloncesto R29", baloncesto, sub15, 5);
        Jugador a = new Jugador("r29-a", "clave", "Jugador A", 0, LocalDate.of(2012, 1, 1));
        Jugador b = new Jugador("r29-b", "clave", "Jugador B", 0, LocalDate.of(2012, 1, 1));
        club.registrarUsuario(a);
        club.registrarUsuario(b);
        assertTrue(club.getAreaDeportiva().inscribirJugadorEnEquipo(a, equipo, "Base", 1));
        assertTrue(club.getAreaDeportiva().inscribirJugadorEnEquipo(b, equipo, "Alero", 2));
        assertEquals(2, equipo.getJugadores().size());
        int cuposAntes = torneo.getCuposDisponibles();
        assertThrows(IllegalStateException.class, () -> gestion.inscribirEquipo(torneo, equipo, a));
        assertEquals(0, torneo.getCuposOcupados());
        assertEquals(cuposAntes, torneo.getCuposDisponibles());
    }
}
