package pruebas;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import administracion.ReporteVentas;
import club.ClubDeportivoSeneca;
import deportes.Deporte;
import torneos.*;
import usuarios.*;

/** Evidencias finales de HU-ADM-07 y HU-JUG-05. */
class AuditoriaCriteriosR31Test {
    @Test void huAdm07RecaudoTorneosNoSeSumaAIngresosGenerales() {
        ClubDeportivoSeneca club = new ClubDeportivoSeneca("Auditoria R31 ingresos");
        Administrador admin = new Administrador("admin-r31-a", "clave", "Admin");
        club.registrarUsuario(admin);
        Deporte tenis = new Deporte("Tenis R31", 1, 3, 0);
        club.getAreaDeportiva().agregarDeporte(tenis);
        LocalDate dia = LocalDate.of(2026, 12, 5);
        TorneoCompetitivo torneo = club.getGestionTorneos().crearTorneoCompetitivo(admin,
            "Recaudo R31", tenis, Torneo.Modalidad.INDIVIDUAL, null, "Intermedio",
            Torneo.Formato.ELIMINACION, 4, dia, dia, 10000, 0.7);
        Jugador a = new Jugador("jug-r31-a", "clave", "A", 0, LocalDate.of(2012, 1, 1));
        Jugador b = new Jugador("jug-r31-b", "clave", "B", 0, LocalDate.of(2012, 1, 1));
        club.registrarUsuario(a);
        club.registrarUsuario(b);
        club.getGestionTorneos().inscribirJugador(torneo, a);
        club.getGestionTorneos().inscribirJugador(torneo, b);
        ReporteVentas reporte = new ReporteVentas(club, dia, dia);
        assertEquals(20000.0, reporte.getIngresosTorneos(), 0.001);
        assertEquals(0.0, reporte.getIngresosTotales(), 0.001,
            "El recaudo de torneos no debe sumarse a los ingresos generales");
    }

    @Test void huJug05CuposPreferencialesSinUsoSeConviertenEnRegulares() {
        ClubDeportivoSeneca club = new ClubDeportivoSeneca("Auditoria R31 cupos");
        Administrador admin = new Administrador("admin-r31-b", "clave", "Admin");
        club.registrarUsuario(admin);
        Deporte tenis = new Deporte("Tenis R31", 1, 3, 0);
        club.getAreaDeportiva().agregarDeporte(tenis);
        LocalDate dia = LocalDate.of(2026, 12, 5);
        TorneoRecreativo torneo = club.getGestionTorneos().crearTorneoRecreativo(admin,
            "Cupos R31", tenis, Torneo.Modalidad.INDIVIDUAL, null, "Intermedio",
            Torneo.Formato.LIGA, 5, dia, dia, 10);
        assertEquals(1, torneo.getCuposPreferencialesDisponibles());
        assertEquals(4, torneo.getCuposRegularesDisponibles());
        club.getGestionTorneos().liberarCuposPreferenciales(admin, torneo);
        assertTrue(torneo.sonPreferencialesLiberados());
        assertEquals(0, torneo.getCuposPreferencialesDisponibles());
        assertEquals(5, torneo.getCuposRegularesDisponibles());
        for (int i = 0; i < 5; i++) {
            Jugador j = new Jugador("jug-r31-" + i, "clave", "Jugador " + i,
                0, LocalDate.of(2012, 1, 1));
            club.registrarUsuario(j);
            club.getGestionTorneos().inscribirJugador(torneo, j);
        }
        assertEquals(5, torneo.getCuposOcupados());
        assertEquals(0, torneo.getCuposRegularesDisponibles());
    }
}
