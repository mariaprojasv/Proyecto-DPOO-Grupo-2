package pruebas;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import administracion.InterfazAdministracion;
import administracion.SugerenciaSnack;
import club.ClubDeportivoSeneca;
import usuarios.Administrador;
import usuarios.Entrenador;
import usuarios.GestionTurnos;
import usuarios.SolicitudCambioTurno;

@DisplayName("Flujos integrados: sesion, sugerencias y turnos")
class FlujosAutorizacionTest {
    private ClubDeportivoSeneca club;
    private InterfazAdministracion interfaz;
    private Administrador administrador;

    @BeforeEach
    void setUp() {
        // GIVEN: cada prueba comienza con un club independiente.
        club = new ClubDeportivoSeneca("Seneca");
        administrador = new Administrador("admin47", "clave", "Administradora");
        assertTrue(club.registrarUsuario(administrador));
        interfaz = new InterfazAdministracion(club);
    }

    private SugerenciaSnack sugerenciaPendiente() {
        return club.getGestionSugerencias().sugerirSnack(
                administrador, "Snack R47", 2500, 4, "Ninguno");
    }

    @Nested
    @DisplayName("Sugerencias desde la interfaz")
    class Sugerencias {
        @Test
        void noApruebaSinIniciarSesionNiModificaElProducto() {
            SugerenciaSnack sugerencia = sugerenciaPendiente();
            // WHEN / THEN
            assertThrows(SecurityException.class, () -> interfaz.aprobarSugerencia(sugerencia));
            assertEquals(SugerenciaSnack.Estado.PENDIENTE, sugerencia.getEstado());
            assertNull(club.getCafeteria().buscarProducto("Snack R47"));
        }

        @Test
        void aprobarConSesionAgregaElProducto() {
            SugerenciaSnack sugerencia = sugerenciaPendiente();
            interfaz.iniciarSesion("admin47", "clave");
            interfaz.aprobarSugerencia(sugerencia);
            assertEquals(SugerenciaSnack.Estado.APROBADA, sugerencia.getEstado());
            assertNotNull(club.getCafeteria().buscarProducto("Snack R47"));
        }

        @Test
        void cerrarSesionImpideRechazar() {
            SugerenciaSnack sugerencia = sugerenciaPendiente();
            interfaz.iniciarSesion("admin47", "clave");
            interfaz.cerrarSesion();
            assertThrows(SecurityException.class,
                    () -> interfaz.rechazarSugerencia(sugerencia, "No cumple"));
            assertEquals(SugerenciaSnack.Estado.PENDIENTE, sugerencia.getEstado());
        }

        @Test
        void rechazoAutorizadoNoCreaProducto() {
            SugerenciaSnack sugerencia = sugerenciaPendiente();
            interfaz.iniciarSesion("admin47", "clave");
            interfaz.rechazarSugerencia(sugerencia, "No cumple");
            assertEquals(SugerenciaSnack.Estado.RECHAZADA, sugerencia.getEstado());
            assertEquals("No cumple", sugerencia.getMotivo());
            assertNull(club.getCafeteria().buscarProducto("Snack R47"));
        }
    }

    @Nested
    @DisplayName("Solicitudes de turnos desde la interfaz")
    class Turnos {
        private SolicitudCambioTurno crearSolicitud() {
            GestionTurnos gestion = club.getGestionTurnos();
            Entrenador entrenador = new Entrenador("ent47", "clave", "Entrenador", "Tenis");
            assertTrue(gestion.agregarTurno(entrenador, LocalTime.of(8, 0), LocalTime.of(10, 0)));
            return gestion.solicitarCambio(gestion.getTurnos().get(0),
                    LocalTime.of(8, 0), LocalTime.of(9, 0));
        }

        @Test
        void noPermiteRechazarSinSesion() {
            SolicitudCambioTurno solicitud = crearSolicitud();
            assertThrows(SecurityException.class, () -> interfaz.rechazarCambioTurno(solicitud));
            assertEquals(SolicitudCambioTurno.Estado.PENDIENTE, solicitud.getEstado());
        }

        @Test
        void rechazoConSesionConservaElTurnoOriginal() {
            SolicitudCambioTurno solicitud = crearSolicitud();
            interfaz.iniciarSesion("admin47", "clave");
            interfaz.rechazarCambioTurno(solicitud);
            assertEquals(SolicitudCambioTurno.Estado.RECHAZADA, solicitud.getEstado());
            assertEquals(LocalTime.of(10, 0), club.getGestionTurnos().getTurnos().get(0).getFin());
        }

        @Test
        void aprobacionSinCoberturaRevierteCambioYDejaSolicitudPendiente() {
            SolicitudCambioTurno solicitud = crearSolicitud();
            interfaz.iniciarSesion("admin47", "clave");
            assertThrows(IllegalStateException.class, () -> interfaz.aprobarCambioTurno(solicitud));
            assertEquals(SolicitudCambioTurno.Estado.PENDIENTE, solicitud.getEstado());
            assertEquals(LocalTime.of(10, 0), club.getGestionTurnos().getTurnos().get(0).getFin());
        }
    }
}
