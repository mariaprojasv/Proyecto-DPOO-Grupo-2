package pruebas;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import club.ClubDeportivoSeneca;
import deportes.Deporte;
import torneos.*;
import usuarios.*;

/** Pruebas de regresion adicionales para criterios parciales de la matriz de trazabilidad. */
class AuditoriaCriteriosR25Test {
    private ClubDeportivoSeneca club;
    private Administrador admin;
    private GestionTorneos gestion;
    private Deporte tenis;
    private LocalDate dia;

    @BeforeEach void preparar() {
        club = new ClubDeportivoSeneca("Club Auditoria");
        admin = new Administrador("admin-aud", "clave", "Admin Auditoria");
        club.registrarUsuario(admin);
        gestion = club.getGestionTorneos();
        tenis = new Deporte("Tenis", 1, 3, 0);
        club.getAreaDeportiva().agregarDeporte(tenis);
        dia = LocalDate.of(2026, 12, 5);
    }

    private TorneoRecreativo recreativo(String nombre, int cupos) {
        return gestion.crearTorneoRecreativo(admin, nombre, tenis, Torneo.Modalidad.INDIVIDUAL,
                null, "Intermedio", Torneo.Formato.ELIMINACION, cupos, dia, dia, 15);
    }

    private Jugador jugador(String login) {
        Jugador j = new Jugador(login, "clave", login, 0, LocalDate.of(2012, 1, 1));
        club.registrarUsuario(j);
        return j;
    }

    @Test void huAdm04CreacionRecreativaGratuitaYEnInscripcion() {
        TorneoRecreativo t = recreativo("Gratis", 10);
        assertEquals(Torneo.Estado.EN_INSCRIPCION, t.getEstado());
        assertEquals(2, t.getCuposPreferenciales());
        gestion.inscribirJugador(t, jugador("jug-a"));
        assertEquals(0.0, t.getTotalRecaudado(), 0.001);
    }

    @Test void huAdm04RechazaCuposInsuficientesYNivelAusente() {
        assertThrows(IllegalArgumentException.class, () -> recreativo("Uno", 1));
        assertThrows(IllegalArgumentException.class, () -> gestion.crearTorneoRecreativo(admin,
                "Sin nivel", tenis, Torneo.Modalidad.INDIVIDUAL, null, null,
                Torneo.Formato.ELIMINACION, 4, dia, dia, 15));
    }

    @Test void huAdm05CompetitivoCobraYRechazaParametrosInvalidos() {
        TorneoCompetitivo t = gestion.crearTorneoCompetitivo(admin, "Competitivo", tenis,
                Torneo.Modalidad.INDIVIDUAL, null, "Intermedio", Torneo.Formato.ELIMINACION,
                4, dia, dia, 10000, 0.7);
        assertEquals(Torneo.Estado.EN_INSCRIPCION, t.getEstado());
        gestion.inscribirJugador(t, jugador("jug-b"));
        assertEquals(10000.0, t.getTotalRecaudado(), 0.001);
        assertThrows(IllegalArgumentException.class, () -> gestion.crearTorneoCompetitivo(admin,
                "Tarifa cero", tenis, Torneo.Modalidad.INDIVIDUAL, null, "Intermedio",
                Torneo.Formato.ELIMINACION, 4, dia, dia, 0, 0.7));
        assertThrows(IllegalArgumentException.class, () -> gestion.crearTorneoCompetitivo(admin,
                "Porcentaje invalido", tenis, Torneo.Modalidad.INDIVIDUAL, null, "Intermedio",
                Torneo.Formato.ELIMINACION, 4, dia, dia, 10000, 1.1));
        assertEquals(1, gestion.getTorneosAbiertos().size());
    }

    @Test void huAdm06NoIniciaConUnSoloInscritoYConservaEstado() {
        TorneoRecreativo t = recreativo("Pocos", 4);
        gestion.inscribirJugador(t, jugador("jug-c"));
        assertThrows(IllegalStateException.class, () -> gestion.iniciarTorneo(admin, t));
        assertEquals(Torneo.Estado.EN_INSCRIPCION, t.getEstado());
        assertTrue(t.getEncuentros().isEmpty());
    }

    @Test void huAdm06InicioLiberaPreferencialesYGeneraEncuentros() {
        TorneoRecreativo t = recreativo("Inicio", 5);
        gestion.inscribirJugador(t, jugador("jug-d"));
        gestion.inscribirJugador(t, jugador("jug-e"));
        assertEquals(1, t.getCuposPreferencialesDisponibles());
        gestion.iniciarTorneo(admin, t);
        assertEquals(Torneo.Estado.EN_CURSO, t.getEstado());
        assertEquals(0, t.getCuposPreferencialesDisponibles());
        assertFalse(t.getEncuentros().isEmpty());
    }
}
