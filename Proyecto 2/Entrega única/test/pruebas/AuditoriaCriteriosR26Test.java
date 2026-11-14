package pruebas;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import club.ClubDeportivoSeneca;
import deportes.Deporte;
import torneos.*;
import usuarios.*;

/** Evidencias especificas para HU-JUG-02 y HU-ADM-06. */
class AuditoriaCriteriosR26Test {
    private ClubDeportivoSeneca club;
    private Administrador admin;
    private GestionTorneos gestion;
    private Deporte tenis;
    private LocalDate fecha;

    @BeforeEach void preparar() {
        club = new ClubDeportivoSeneca("Club Auditoria R26");
        admin = new Administrador("admin-r26", "clave", "Admin");
        club.registrarUsuario(admin);
        gestion = club.getGestionTorneos();
        tenis = new Deporte("Tenis", 1, 3, 0);
        club.getAreaDeportiva().agregarDeporte(tenis);
        fecha = LocalDate.of(2026, 12, 5);
    }

    private TorneoRecreativo torneo(String nombre) {
        return gestion.crearTorneoRecreativo(admin, nombre, tenis,
                Torneo.Modalidad.INDIVIDUAL, null, "Intermedio",
                Torneo.Formato.ELIMINACION, 4, fecha, fecha, 15);
    }

    private Jugador jugador(String id) {
        Jugador j = new Jugador(id, "clave", id, 0, LocalDate.of(2012, 1, 1));
        club.registrarUsuario(j);
        return j;
    }

    @Test void huJug02NoPermiteInscripcionIndividualDuplicada() {
        TorneoRecreativo t = torneo("Duplicados");
        Jugador j = jugador("jug-r26-a");
        gestion.inscribirJugador(t, j);
        assertEquals(1, t.getCuposOcupados());
        assertThrows(IllegalStateException.class, () -> gestion.inscribirJugador(t, j));
        assertEquals(1, t.getCuposOcupados());
    }

    @Test void huAdm06SoloAdministradorRegistradoPuedeIniciar() {
        TorneoRecreativo t = torneo("Permiso inicio");
        gestion.inscribirJugador(t, jugador("jug-r26-b"));
        gestion.inscribirJugador(t, jugador("jug-r26-c"));
        Administrador intruso = new Administrador("intruso-r26", "clave", "Intruso");
        assertThrows(SecurityException.class, () -> gestion.iniciarTorneo(intruso, t));
        assertEquals(Torneo.Estado.EN_INSCRIPCION, t.getEstado());
        gestion.iniciarTorneo(admin, t);
        assertEquals(Torneo.Estado.EN_CURSO, t.getEstado());
    }

    @Test void huAdm06SoloAdministradorRegistradoPuedeRegistrarResultado() {
        TorneoRecreativo t = torneo("Permiso resultado");
        gestion.inscribirJugador(t, jugador("jug-r26-d"));
        gestion.inscribirJugador(t, jugador("jug-r26-e"));
        gestion.iniciarTorneo(admin, t);
        Encuentro encuentro = t.getEncuentros().get(0);
        Administrador intruso = new Administrador("intruso-r26", "clave", "Intruso");
        assertThrows(SecurityException.class, () -> gestion.registrarResultado(intruso, t, encuentro, 6, 0));
        assertEquals(Torneo.Estado.EN_CURSO, t.getEstado());
    }
}
