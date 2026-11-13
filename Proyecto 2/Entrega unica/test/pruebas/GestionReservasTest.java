package pruebas;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import deportes.Deporte;
import reservas.GestionReservas;
import reservas.Instalacion;
import reservas.Reserva;
import usuarios.Administrador;
import usuarios.Socio;

@DisplayName("Reservas: validaciones, fronteras y conflictos")
class GestionReservasTest {
    private GestionReservas gestion;
    private Instalacion cancha;
    private Socio socio;
    private LocalDate fecha;

    @BeforeEach
    void setUp() {
        // GIVEN: instalacion registrada, sin reservas previas.
        gestion = new GestionReservas();
        cancha = new Instalacion("Cancha 1", new Deporte("Futbol", 1, 3, 1), 10, Set.of("libre"));
        socio = new Socio("socio37", "1234", "Socio", 0);
        fecha = LocalDate.of(2026, 11, 20);
        assertTrue(gestion.agregarInstalacion(cancha));
    }

    private Reserva reservar(int jugadores, LocalTime inicio, LocalTime fin) {
        return gestion.realizarReservaValidada(socio, cancha, fecha, inicio, fin, jugadores, "libre");
    }

    @Test
    void reservaValidaQuedaRegistrada() {
        // WHEN
        Reserva reserva = reservar(5, LocalTime.of(9, 0), LocalTime.of(10, 0));
        // THEN
        assertNotNull(reserva);
        assertEquals(1, gestion.getReservas().size());
        assertSame(socio, reserva.getUsuario());
    }

    @ParameterizedTest(name = "jugadores {0}: valido={1}")
    @CsvSource({"0, false", "1, true", "5, true", "10, true", "11, false"})
    void particionesYBordesDeCapacidad(int jugadores, boolean valido) {
        if (valido) {
            assertNotNull(reservar(jugadores, LocalTime.of(9, 0), LocalTime.of(10, 0)));
            assertEquals(1, gestion.getReservas().size());
        } else {
            assertThrows(IllegalArgumentException.class,
                () -> reservar(jugadores, LocalTime.of(9, 0), LocalTime.of(10, 0)));
            assertTrue(gestion.getReservas().isEmpty());
        }
    }

    @Test
    void rechazaHoraFinalIgualALaInicial() {
        assertThrows(IllegalArgumentException.class,
            () -> reservar(5, LocalTime.of(9, 0), LocalTime.of(9, 0)));
    }

    @Test
    void rechazaModalidadNoAdmitida() {
        assertThrows(IllegalArgumentException.class,
            () -> gestion.realizarReservaValidada(socio, cancha, fecha,
                LocalTime.of(9, 0), LocalTime.of(10, 0), 5, "dobles"));
    }

    @Test
    void administradorNoPuedeReservar() {
        Administrador admin = new Administrador("admin37", "1234", "Admin");
        assertThrows(SecurityException.class,
            () -> gestion.realizarReservaValidada(admin, cancha, fecha,
                LocalTime.of(9, 0), LocalTime.of(10, 0), 5, "libre"));
    }

    @Test
    void padelExigeCuatroJugadoresEnDobles() {
        Instalacion padel = new Instalacion("Padel 1", new Deporte("Padel", 1, 3, 1), 4, Set.of("dobles", "libre"));
        assertTrue(gestion.agregarInstalacion(padel));
        assertThrows(IllegalArgumentException.class, () -> gestion.realizarReservaValidada(
            socio, padel, fecha, LocalTime.of(11, 0), LocalTime.of(12, 0), 2, "dobles"));
        assertThrows(IllegalArgumentException.class, () -> gestion.realizarReservaValidada(
            socio, padel, fecha, LocalTime.of(11, 0), LocalTime.of(12, 0), 4, "libre"));
        assertNotNull(gestion.realizarReservaValidada(socio, padel, fecha,
            LocalTime.of(11, 0), LocalTime.of(12, 0), 4, "dobles"));
        assertEquals(1, gestion.getReservas().size());
    }

    @Test
    void maximoSimultaneoAplicaEntreDistintasInstalaciones() {
        Instalacion segunda = new Instalacion("Cancha 2", new Deporte("Futbol", 1, 3, 1), 10, Set.of("libre"));
        assertTrue(gestion.agregarInstalacion(segunda));
        assertTrue(gestion.configurarMaximoSimultaneo("Futbol", 1));
        reservar(5, LocalTime.of(9, 0), LocalTime.of(10, 0));
        assertThrows(IllegalStateException.class, () -> gestion.realizarReservaValidada(
            socio, segunda, fecha, LocalTime.of(9, 30), LocalTime.of(10, 30), 5, "libre"));
        assertEquals(1, gestion.getReservas().size());
        assertNotNull(gestion.realizarReservaValidada(socio, segunda, fecha,
            LocalTime.of(10, 0), LocalTime.of(11, 0), 5, "libre"));
    }

    @Test
    void bebidasCalientesRestringidasEnPadelYTenisDeMesa() {
        for (String deporte : new String[]{"Padel", "Tenis de mesa"}) {
            GestionReservas g = new GestionReservas();
            boolean padel = deporte.equals("Padel");
            Instalacion i = new Instalacion("Instalacion " + deporte,
                new Deporte(deporte, 1, 3, 1), 4, Set.of("dobles"));
            // En tenis de mesa la restriccion aplica solo a instalaciones techadas.
            if (!padel) i.establecerTechada(true);
            assertTrue(g.agregarInstalacion(i));
            Reserva r = g.realizarReservaValidada(socio, i, fecha,
                LocalTime.of(9, 0), LocalTime.of(10, 0), 4, "dobles");
            assertFalse(g.permitirUso(r, true));
            assertTrue(g.permitirUso(r, false));
            if (!padel) {
                i.establecerTechada(false);
                assertTrue(g.permitirUso(r, true),
                    "En tenis de mesa descubierto se permiten bebidas calientes");
            }
        }
    }

    @Test
    void bebidaCalientePermitidaEnFutbolYNoParaReservaInexistente() {
        Reserva r = reservar(5, LocalTime.of(9, 0), LocalTime.of(10, 0));
        assertTrue(gestion.permitirUso(r, true));
        assertFalse(new GestionReservas().permitirUso(r, false));
    }

    @Nested
    @DisplayName("Conflictos entre reservas")
    class Conflictos {
        @BeforeEach
        void primeraReserva() {
            reservar(5, LocalTime.of(9, 0), LocalTime.of(10, 0));
        }

        @Test
        void solapamientoSeRechazaSinCrearReserva() {
            assertThrows(IllegalStateException.class,
                () -> reservar(5, LocalTime.of(9, 30), LocalTime.of(10, 30)));
            assertEquals(1, gestion.getReservas().size());
        }

        @Test
        void reservaContiguaEsValida() {
            assertNotNull(reservar(5, LocalTime.of(10, 0), LocalTime.of(11, 0)));
            assertEquals(2, gestion.getReservas().size());
        }
    }
}
