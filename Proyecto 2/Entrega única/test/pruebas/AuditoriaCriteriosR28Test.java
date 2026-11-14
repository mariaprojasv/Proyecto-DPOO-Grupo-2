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

/** Evidencia adicional para HU-JUG-02 CA-4 y HU-JUG-06 CA-5. */
class AuditoriaCriteriosR28Test {
    private ClubDeportivoSeneca club;
    private Administrador admin;
    private GestionTorneos gestion;
    private Deporte tenis;
    private LocalDate fecha;

    @BeforeEach void preparar() {
        club = new ClubDeportivoSeneca("Auditoria R28");
        admin = new Administrador("admin-r28", "clave", "Administrador");
        club.registrarUsuario(admin);
        gestion = club.getGestionTorneos();
        tenis = new Deporte("Tenis", 1, 3, 0);
        club.getAreaDeportiva().agregarDeporte(tenis);
        fecha = LocalDate.of(2026, 12, 5);
    }

    private Jugador jugador(String login) {
        Jugador j = new Jugador(login, "clave", login, 0, LocalDate.of(2012, 1, 1));
        club.registrarUsuario(j);
        return j;
    }

    @Test void huJug02RechazaInscripcionCuandoSeAgotanCuposRegulares() {
        TorneoRecreativo t = gestion.crearTorneoRecreativo(admin, "Cupos R28", tenis,
                Torneo.Modalidad.INDIVIDUAL, null, "Intermedio", Torneo.Formato.LIGA,
                2, fecha, fecha, 10);
        assertEquals(1, t.getCuposRegularesDisponibles());
        gestion.inscribirJugador(t, jugador("r28-j1"));
        assertEquals(0, t.getCuposRegularesDisponibles());
        assertEquals(1, t.getCuposPreferencialesDisponibles());
        assertThrows(IllegalStateException.class,
                () -> gestion.inscribirJugador(t, jugador("r28-j2")));
        assertEquals(1, t.getCuposOcupados());
    }

    @Test void huJug06RechazaPersonaExternaCuandoEquipoEstaLleno() {
        Deporte futbol = new Deporte("Futbol R28", 2, 3, 1);
        Categoria categoria = new Categoria("Sub-15 R28", 10, 15);
        club.getAreaDeportiva().agregarDeporte(futbol);
        club.getAreaDeportiva().agregarCategoria(categoria);
        Equipo equipo = club.getAreaDeportiva().crearEquipo("Equipo lleno R28", futbol, categoria, 2);
        Jugador a = jugador("r28-equipo-a");
        Jugador b = jugador("r28-equipo-b");
        assertTrue(club.getAreaDeportiva().inscribirJugadorEnEquipo(a, equipo, "Delantero", 9));
        assertTrue(club.getAreaDeportiva().inscribirJugadorEnEquipo(b, equipo, "Portero", 1));
        assertEquals(2, equipo.getIntegrantes().size());
        PersonaExterna externo = new PersonaExterna("r28-externo", "clave", "Externo");
        club.registrarUsuario(externo);
        assertThrows(IllegalStateException.class,
                () -> club.getAreaDeportiva().inscribirIntegranteExterno(externo, equipo));
        assertEquals(2, equipo.getIntegrantes().size());
        assertFalse(equipo.getIntegrantes().contains(externo));
    }
}
