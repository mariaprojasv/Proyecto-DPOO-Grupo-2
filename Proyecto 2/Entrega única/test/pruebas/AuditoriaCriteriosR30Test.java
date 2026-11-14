package pruebas;

import static org.junit.jupiter.api.Assertions.*;
import java.time.*;
import java.util.EnumSet;
import org.junit.jupiter.api.Test;
import club.ClubDeportivoSeneca;
import deportes.Deporte;
import torneos.*;
import usuarios.*;

/** Evidencias HU-ENT-03 y HU-SOC-04: mensaje del turno y bolsa exacta. */
class AuditoriaCriteriosR30Test {
    @Test void huEnt03MensajeDeRechazoIdentificaFechaDelTurno() {
        ClubDeportivoSeneca club = new ClubDeportivoSeneca("Auditoria R30 turnos");
        Administrador admin = new Administrador("admin-r30-a", "clave", "Admin");
        club.registrarUsuario(admin);
        Deporte tenis = new Deporte("Tenis R30", 1, 3, 0);
        club.getAreaDeportiva().agregarDeporte(tenis);
        LocalDate sabado = LocalDate.of(2026, 12, 5);
        LocalDate domingo = sabado.plusDays(1);
        TorneoRecreativo torneo = club.getGestionTorneos().crearTorneoRecreativo(admin,
            "Dos dias R30", tenis, Torneo.Modalidad.INDIVIDUAL, null, "Intermedio",
            Torneo.Formato.ELIMINACION, 5, sabado, domingo, 15);
        Entrenador empleado = new Entrenador("ent-r30", "clave", "Entrenador R30", "Tenis");
        club.registrarUsuario(empleado);
        assertTrue(club.getGestionTurnos().agregarTurno(empleado, LocalTime.of(9, 0),
            LocalTime.of(11, 0), EnumSet.of(DayOfWeek.SUNDAY)));
        IllegalStateException error = assertThrows(IllegalStateException.class,
            () -> club.getGestionTorneos().inscribirJugador(torneo, empleado));
        assertTrue(error.getMessage().contains(domingo.toString()),
            "El mensaje debe identificar el dia exacto del turno: " + error.getMessage());
        assertEquals(0, torneo.getCuposOcupados());
    }

    @Test void huSoc04BolsaPremioExactamenteCatorceMilDeVeinteMil() {
        ClubDeportivoSeneca club = new ClubDeportivoSeneca("Auditoria R30 premios");
        Administrador admin = new Administrador("admin-r30-b", "clave", "Admin");
        club.registrarUsuario(admin);
        Deporte tenis = new Deporte("Tenis R30", 1, 3, 0);
        club.getAreaDeportiva().agregarDeporte(tenis);
        LocalDate fecha = LocalDate.of(2026, 12, 5);
        TorneoCompetitivo torneo = club.getGestionTorneos().crearTorneoCompetitivo(admin,
            "Premio 14000 R30", tenis, Torneo.Modalidad.INDIVIDUAL, null, "Intermedio",
            Torneo.Formato.ELIMINACION, 4, fecha, fecha, 10000, 0.70);
        Jugador a = new Jugador("jug-r30-a", "clave", "Jugador A", 0, LocalDate.of(2012, 1, 1));
        Jugador b = new Jugador("jug-r30-b", "clave", "Jugador B", 0, LocalDate.of(2012, 1, 1));
        club.registrarUsuario(a);
        club.registrarUsuario(b);
        club.getGestionTorneos().inscribirJugador(torneo, a);
        club.getGestionTorneos().inscribirJugador(torneo, b);
        assertEquals(20000.0, torneo.getTotalRecaudado(), 0.001);
        assertEquals(14000.0, torneo.getPremioEnEfectivo(), 0.001);
    }
}
