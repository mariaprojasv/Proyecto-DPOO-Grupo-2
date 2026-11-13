package pruebas;

import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import club.ClubDeportivoSeneca;
import persistencia.PersistenciaArchivos;
import usuarios.Administrador;
import usuarios.Entrenador;
import usuarios.Fisioterapeuta;
import usuarios.GestionTurnos;
import usuarios.SolicitudCambioTurno;

@DisplayName("Persistencia de solicitudes de cambio de turno")
class PersistenciaTurnosTest {
    @TempDir Path carpeta;
    private ClubDeportivoSeneca club;
    private Administrador administrador;
    private GestionTurnos turnos;

    @BeforeEach void preparar() {
        club = new ClubDeportivoSeneca("Seneca");
        administrador = new Administrador("adm43", "p", "Admin");
        club.registrarUsuario(administrador);
        for (int i = 1; i <= 3; i++) {
            Entrenador empleado = new Entrenador("ent43_" + i, "p", "Entrenador " + i, "Tenis");
            club.registrarUsuario(empleado);
            assertTrue(club.getGestionTurnos().agregarTurno(empleado, LocalTime.of(8, 0), LocalTime.of(10, 0)));
        }
        Fisioterapeuta fisioterapeuta = new Fisioterapeuta("fis43", "p", "Fisioterapeuta");
        club.registrarUsuario(fisioterapeuta);
        assertTrue(club.getGestionTurnos().agregarTurno(fisioterapeuta, LocalTime.of(8, 0), LocalTime.of(10, 0)));
        turnos = club.getGestionTurnos();
    }

    private ClubDeportivoSeneca restaurar() throws IOException {
        PersistenciaArchivos archivos = new PersistenciaArchivos(carpeta.toString());
        archivos.guardar(club);
        ClubDeportivoSeneca nuevo = new ClubDeportivoSeneca("Seneca");
        archivos.cargar(nuevo);
        return nuevo;
    }

    @Test void guardaArchivoYConservaEstados() throws IOException {
        SolicitudCambioTurno aprobada = turnos.solicitarCambio(turnos.getTurnos().get(0), LocalTime.of(9, 0), LocalTime.of(10, 0));
        turnos.aprobarCambio(administrador, aprobada);
        SolicitudCambioTurno rechazada = turnos.solicitarCambio(turnos.getTurnos().get(1), LocalTime.of(9, 0), LocalTime.of(10, 0));
        turnos.rechazarCambio(administrador, rechazada);
        turnos.solicitarCambio(turnos.getTurnos().get(2), LocalTime.of(9, 0), LocalTime.of(10, 0));
        ClubDeportivoSeneca nuevo = restaurar();
        assertTrue(Files.exists(carpeta.resolve("solicitudes_turno.txt")));
        assertEquals(3, nuevo.getGestionTurnos().getSolicitudes().size());
        assertEquals(SolicitudCambioTurno.Estado.APROBADA, nuevo.getGestionTurnos().getSolicitudes().get(0).getEstado());
        assertEquals(SolicitudCambioTurno.Estado.RECHAZADA, nuevo.getGestionTurnos().getSolicitudes().get(1).getEstado());
        assertEquals(SolicitudCambioTurno.Estado.PENDIENTE, nuevo.getGestionTurnos().getSolicitudes().get(2).getEstado());
    }

    @Test void conservaHorariosYRelacionesConEmpleados() throws IOException {
        turnos.solicitarCambio(turnos.getTurnos().get(0), LocalTime.of(9, 0), LocalTime.of(10, 0));
        ClubDeportivoSeneca nuevo = restaurar();
        SolicitudCambioTurno solicitud = nuevo.getGestionTurnos().getSolicitudes().get(0);
        assertEquals(LocalTime.of(8, 0), solicitud.getInicioOriginal());
        assertEquals(LocalTime.of(9, 0), solicitud.getNuevoInicio());
        assertSame(nuevo.getGestionTurnos().getTurnos().get(0), solicitud.getTurnoOriginal());
        assertSame(nuevo.buscarUsuario("ent43_1"), solicitud.getTurnoOriginal().getEmpleado());
    }

    @Test void solicitudPendientePuedeRechazarseTrasCargar() throws IOException {
        turnos.solicitarCambio(turnos.getTurnos().get(0), LocalTime.of(9, 0), LocalTime.of(10, 0));
        ClubDeportivoSeneca nuevo = restaurar();
        SolicitudCambioTurno solicitud = nuevo.getGestionTurnos().getSolicitudes().get(0);
        nuevo.getGestionTurnos().rechazarCambio((Administrador) nuevo.buscarUsuario("adm43"), solicitud);
        assertEquals(SolicitudCambioTurno.Estado.RECHAZADA, solicitud.getEstado());
    }

    @Test void carpetaSinArchivosMantieneSolicitudesVacias() throws IOException {
        ClubDeportivoSeneca nuevo = new ClubDeportivoSeneca("Seneca");
        new PersistenciaArchivos(carpeta.toString()).cargar(nuevo);
        assertTrue(nuevo.getGestionTurnos().getSolicitudes().isEmpty());
    }
}
