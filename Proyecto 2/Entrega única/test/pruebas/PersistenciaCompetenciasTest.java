package pruebas;

import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import club.ClubDeportivoSeneca;
import deportes.*;
import persistencia.PersistenciaArchivos;
import usuarios.Jugador;

@DisplayName("Persistencia de competencias deportivas")
class PersistenciaCompetenciasTest {
    @TempDir Path carpeta;
    private ClubDeportivoSeneca original;
    private CompetenciaLiga liga;
    private PartidoCompetencia partido;
    private ArrayList<Jugador> jugadores;

    @BeforeEach
    void preparar() {
        // Given: un club con deporte, categoria, equipo y nomina validos.
        original = new ClubDeportivoSeneca("Seneca");
        Deporte futbol = new Deporte("Futbol", 7, 3, 1);
        Categoria mayores = new Categoria("Mayores", 18, 99);
        original.getAreaDeportiva().agregarDeporte(futbol);
        original.getAreaDeportiva().agregarCategoria(mayores);
        Equipo equipo = original.getAreaDeportiva().crearEquipo("Leones", futbol, mayores, 12);
        jugadores = new ArrayList<>();
        for (int i = 1; i <= 7; i++) {
            Jugador jugador = new Jugador("persist45_" + i, "1234", "Jugador " + i,
                    0, LocalDate.of(2000, 1, i));
            original.registrarUsuario(jugador);
            original.getAreaDeportiva().inscribirJugadorEnEquipo(jugador, equipo, "Jugador", i);
            jugadores.add(jugador);
        }
        liga = new CompetenciaLiga("Liga", "Seneca", "2026", 7, futbol, mayores);
        original.getAreaDeportiva().crearCompetencia(liga);
        assertTrue(liga.inscribirEquipo(equipo, jugadores));
        partido = new PartidoCompetencia(LocalDate.of(2026, 10, 1), futbol,
                equipo, "Rivales", "Cancha");
        original.getAreaDeportiva().registrarPartidoCompetencia(liga, partido);
    }

    private CompetenciaLiga guardarYRecuperar() throws IOException {
        // When: guardo y recupero el club desde una carpeta aislada.
        PersistenciaArchivos archivos = new PersistenciaArchivos(carpeta.toString());
        archivos.guardar(original);
        ClubDeportivoSeneca recuperado = new ClubDeportivoSeneca("Seneca");
        archivos.cargar(recuperado);
        assertEquals(1, recuperado.getAreaDeportiva().getCompetencias().size());
        return (CompetenciaLiga) recuperado.getAreaDeportiva().getCompetencias().get(0);
    }

    @Test void conservaCompetenciaYNomina() throws IOException {
        CompetenciaLiga recuperada = guardarYRecuperar();
        // Then: se conservan equipo y jugadores inscritos.
        assertEquals(1, recuperada.getNominas().size());
        assertEquals(7, recuperada.getNominas().get(0).getJugadores().size());
    }

    @Test void conservaPartidoPendienteSinPuntos() throws IOException {
        CompetenciaLiga recuperada = guardarYRecuperar();
        assertEquals(1, recuperada.getPartidos().size());
        assertFalse(recuperada.getPartidos().get(0).tieneResultadoRegistrado());
        assertTrue(recuperada.getTablaPosiciones().isEmpty());
    }

    @Test void conservaMarcadorYTablaDePosiciones() throws IOException {
        assertTrue(partido.registrarMarcador(3, 1));
        CompetenciaLiga recuperada = guardarYRecuperar();
        assertEquals(3, recuperada.getPartidos().get(0).getMarcadorLocal());
        assertEquals(1, recuperada.getPartidos().get(0).getMarcadorVisitante());
        assertEquals(3, recuperada.getTablaPosiciones().get(0).getPuntos());
        assertEquals(1, recuperada.getTablaPosiciones().get(0).getPartidosJugados());
    }

    @Test void conservaEstadisticasIndividuales() throws IOException {
        assertTrue(partido.registrarMarcador(3, 1));
        partido.registrarEstadistica(new EstadisticaFutbol(jugadores.get(0), 2, 1));
        CompetenciaLiga recuperada = guardarYRecuperar();
        assertEquals(1, recuperada.getPartidos().get(0).getEstadisticas().size());
        EstadisticaFutbol estadistica = (EstadisticaFutbol)
                recuperada.getPartidos().get(0).getEstadisticas().get(0);
        assertEquals(2, estadistica.getGoles());
        assertEquals(1, estadistica.getAsistencias());
    }
}
