package pruebas;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import deportes.*;
import usuarios.Jugador;

@DisplayName("Estadisticas individuales: acumulacion por deporte")
class EstadisticasIndividualesTest {
    private Jugador jugador;
    @BeforeEach void preparar() {
        jugador = new Jugador("estad48", "1234", "Jugador", 0, LocalDate.of(2000, 1, 1));
    }
    @Test void tenisAcumulaVictoriasYAces() {
        EstadisticaTenis acumulada = (EstadisticaTenis) new EstadisticaTenis(jugador, 2, 1, 5).crearVacia();
        acumulada.acumular(new EstadisticaTenis(jugador, 2, 1, 5));
        acumulada.acumular(new EstadisticaTenis(jugador, 1, 0, 3));
        assertAll(() -> assertEquals(3, acumulada.getPartidosGanados()),
                  () -> assertEquals(8, acumulada.getAces()));
    }
    @Test void padelAcumulaPuntos() {
        EstadisticaPadel acumulada = (EstadisticaPadel) new EstadisticaPadel(jugador, 1, 2, 20).crearVacia();
        acumulada.acumular(new EstadisticaPadel(jugador, 1, 2, 20));
        assertEquals(20, acumulada.getPuntosGanados());
    }
    @Test void tenisMesaAcumulaSetsYConservaValorDestacado() {
        EstadisticaTenisMesa acumulada = (EstadisticaTenisMesa) new EstadisticaTenisMesa(jugador, 3, 1, 9).crearVacia();
        acumulada.acumular(new EstadisticaTenisMesa(jugador, 3, 1, 9));
        assertAll(() -> assertEquals(9, acumulada.getSetsGanados()),
                  () -> assertEquals(3, acumulada.getValorDestacado()));
    }
    @Test void noMezclaEstadisticasDeDeportesDiferentes() {
        EstadisticaTenis tenis = new EstadisticaTenis(jugador, 2, 1, 5);
        tenis.acumular(new EstadisticaPadel(jugador, 4, 0, 50));
        assertEquals(2, tenis.getPartidosGanados());
        assertEquals(5, tenis.getAces());
    }
}
