package pruebas;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import deportes.*;
import usuarios.Jugador;

@DisplayName("Reglas deportivas integradas y encapsulacion")
class ReglasDeportivasIntegradasTest {
    private Deporte futbol;
    private Categoria adultos;
    private Equipo equipo;
    private Jugador ana;
    private CompetenciaLiga liga;

    @BeforeEach
    void preparar() {
        futbol = new Deporte("Futbol", 1, 3, 1);
        adultos = new Categoria("Adultos", 18, 99);
        equipo = new Equipo("Leones", 3, futbol, adultos);
        ana = new Jugador("ana52", "clave", "Ana", 0, LocalDate.of(2000, 1, 1));
        equipo.agregarJugadorValidado(ana, "Defensa", 1);
        liga = new CompetenciaLiga("Liga", "Club", "2026", 1, futbol, adultos);
    }

    @Test
    void nominasYPartidosNoSePuedenBorrarModificandoListasDevueltas() {
        ArrayList<Jugador> jugadores = new ArrayList<>();
        jugadores.add(ana);
        assertTrue(liga.inscribirEquipo(equipo, jugadores));
        liga.getNominas().clear();
        assertEquals(1, liga.getNominas().size());
        assertTrue(liga.getEquipos().contains(equipo));

        PartidoCompetencia partido = new PartidoCompetencia(LocalDate.of(2026, 11, 20), futbol, equipo, "Rival", "Cancha");
        liga.registrarPartidoValidado(partido);
        assertEquals(1, liga.getPartidos().size());
        liga.getPartidos().clear();
        assertEquals(1, liga.getPartidos().size());
        assertTrue(partido.registrarMarcador(2, 0));
        assertEquals(3, liga.getTablaPosiciones().get(0).getPuntos());
    }

    @Test
    void dosAmarillasImpidenAlineacionHastaCumplirSuspension() {
        ControlSanciones control = new ControlSanciones();
        PartidoCompetencia partido = new PartidoCompetencia(LocalDate.of(2026, 11, 20), futbol, equipo, "Rival", "Cancha");
        partido.establecerControlSanciones(control);
        assertTrue(partido.puedeAlinearse(ana));
        assertTrue(control.registrarTarjeta(new Tarjeta(ana, "AMARILLA")));
        assertTrue(control.registrarTarjeta(new Tarjeta(ana, "AMARILLA")));
        assertTrue(control.estaSuspendido(ana));
        assertFalse(partido.puedeAlinearse(ana));
        control.cumplirSuspension(ana);
        assertFalse(control.estaSuspendido(ana));
        assertTrue(partido.puedeAlinearse(ana));
    }

    @Test
    void fasesNoAvanzanMasAllaDeLasRegistradas() {
        CompetenciaFases copa = new CompetenciaFases("Copa", "Club", "2026", 1, futbol, adultos);
        copa.avanzarFase();
        assertEquals(0, copa.getFaseActual());
        copa.agregarFase(new Fase("Primera", true));
        copa.avanzarFase();
        assertEquals(0, copa.getFaseActual());
        copa.agregarFase(new Fase("Segunda", true));
        copa.avanzarFase();
        assertEquals(1, copa.getFaseActual());
        copa.avanzarFase();
        assertEquals(1, copa.getFaseActual());
    }

    @Test
    void nominaRechazaJugadoresAjenosYDuplicados() {
        Jugador ajeno = new Jugador("ajeno52", "clave", "Ajeno", 0, LocalDate.of(2000, 1, 2));
        ArrayList<Jugador> ajenos = new ArrayList<>();
        ajenos.add(ajeno);
        assertFalse(liga.inscribirEquipo(equipo, ajenos));
        ArrayList<Jugador> repetidos = new ArrayList<>();
        repetidos.add(ana);
        repetidos.add(ana);
        assertFalse(liga.inscribirEquipo(equipo, repetidos));
        ArrayList<Jugador> validos = new ArrayList<>();
        validos.add(ana);
        assertTrue(liga.inscribirEquipo(equipo, validos));
        assertFalse(liga.inscribirEquipo(equipo, validos));
    }
}
