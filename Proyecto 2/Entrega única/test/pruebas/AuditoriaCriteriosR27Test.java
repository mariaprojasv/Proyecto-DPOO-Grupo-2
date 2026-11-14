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

/** Pruebas adicionales de trazabilidad HU-JUG-03: cupos y validaciones de equipo. */
class AuditoriaCriteriosR27Test {
    private ClubDeportivoSeneca club;
    private Administrador admin;
    private GestionTorneos gestion;
    private Deporte futbol;
    private Categoria sub15;
    private LocalDate fecha;

    @BeforeEach void preparar() {
        club = new ClubDeportivoSeneca("Club Auditoria R27");
        admin = new Administrador("admin-r27", "clave", "Admin");
        club.registrarUsuario(admin);
        gestion = club.getGestionTorneos();
        futbol = new Deporte("Futbol", 2, 3, 1);
        sub15 = new Categoria("Sub-15", 10, 15);
        club.getAreaDeportiva().agregarDeporte(futbol);
        club.getAreaDeportiva().agregarCategoria(sub15);
        fecha = LocalDate.of(2026, 12, 5);
    }

    private TorneoRecreativo torneo(String nombre) {
        return gestion.crearTorneoRecreativo(admin, nombre, futbol, Torneo.Modalidad.CONJUNTO,
                sub15, null, Torneo.Formato.LIGA, 5, fecha, fecha, 10);
    }

    private Jugador jugador(String id, LocalDate nacimiento) {
        Jugador j = new Jugador(id, "clave", id, 0, nacimiento);
        club.registrarUsuario(j);
        return j;
    }

    private Equipo equipoConDosJugadores(String nombre, Categoria categoria) {
        Equipo e = club.getAreaDeportiva().crearEquipo(nombre, futbol, categoria, 5);
        LocalDate nacimiento = categoria == sub15 ? LocalDate.of(2012, 1, 1) : LocalDate.of(2009, 1, 1);
        Jugador a = jugador(nombre + "-a", nacimiento);
        Jugador b = jugador(nombre + "-b", nacimiento);
        assertTrue(club.getAreaDeportiva().inscribirJugadorEnEquipo(a, e, "Delantero", 9));
        assertTrue(club.getAreaDeportiva().inscribirJugadorEnEquipo(b, e, "Portero", 1));
        return e;
    }

    @Test void huJug03EquipoInscritoOcupaExactamenteUnCupo() {
        TorneoRecreativo t = torneo("Cupos equipos");
        Equipo e = equipoConDosJugadores("Equipo A", sub15);
        assertEquals(5, t.getCuposDisponibles());
        assertNotNull(gestion.inscribirEquipo(t, e, e.getJugadores().get(0)));
        assertEquals(1, t.getCuposOcupados());
        assertEquals(4, t.getCuposDisponibles());
    }

    @Test void huJug03RechazaEquipoDeCategoriaDiferenteSinOcuparCupo() {
        TorneoRecreativo t = torneo("Categoria equipos");
        Categoria otra = new Categoria("Sub-17", 16, 17);
        club.getAreaDeportiva().agregarCategoria(otra);
        Equipo e = equipoConDosJugadores("Equipo B", otra);
        assertEquals(2, e.getJugadores().size());
        Jugador representante = e.getJugadores().get(0);
        assertThrows(IllegalStateException.class,
                () -> gestion.inscribirEquipo(t, e, representante));
        assertEquals(0, t.getCuposOcupados());
    }

    @Test void huJug03RechazaEquipoIncompletoSinOcuparCupo() {
        TorneoRecreativo t = torneo("Equipo incompleto");
        Equipo e = club.getAreaDeportiva().crearEquipo("Equipo C", futbol, sub15, 5);
        Jugador a = jugador("equipo-c-a", LocalDate.of(2012, 1, 1));
        club.getAreaDeportiva().inscribirJugadorEnEquipo(a, e, "Delantero", 9);
        assertThrows(IllegalStateException.class, () -> gestion.inscribirEquipo(t, e, a));
        assertEquals(0, t.getCuposOcupados());
    }
}
