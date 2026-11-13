package pruebas;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import deportes.Estadistica;
import deportes.EstadisticaBaloncesto;
import deportes.EstadisticaVoleibol;
import usuarios.Jugador;

@DisplayName("Estadisticas: baloncesto y voleibol")
class EstadisticasComplementariasTest {
    private Jugador jugador;

    @BeforeEach void preparar() {
        jugador = new Jugador("jug49", "clave", "Jugador", 0, LocalDate.of(2000, 1, 1));
    }

    @Test void baloncestoCreaEstadisticaVaciaDelMismoTipoYJugador() {
        Estadistica original = new EstadisticaBaloncesto(jugador, 10, 3);
        Estadistica vacia = original.crearVacia();
        assertInstanceOf(EstadisticaBaloncesto.class, vacia);
        assertSame(jugador, vacia.getJugador());
        assertEquals(0, vacia.getValorDestacado());
    }

    @Test void baloncestoAcumulaPuntosYRebotes() {
        EstadisticaBaloncesto total = (EstadisticaBaloncesto) new EstadisticaBaloncesto(jugador, 10, 3).crearVacia();
        total.acumular(new EstadisticaBaloncesto(jugador, 10, 3));
        total.acumular(new EstadisticaBaloncesto(jugador, 5, 2));
        assertEquals(15, total.getPuntos());
        assertEquals(5, total.getRebotes());
        assertEquals(15, total.getValorDestacado());
    }

    @Test void voleibolCreaEstadisticaVaciaDelMismoTipoYJugador() {
        Estadistica original = new EstadisticaVoleibol(jugador, 4, 2);
        Estadistica vacia = original.crearVacia();
        assertInstanceOf(EstadisticaVoleibol.class, vacia);
        assertSame(jugador, vacia.getJugador());
        assertEquals(0, vacia.getValorDestacado());
    }

    @Test void voleibolAcumulaAcesYBloqueos() {
        EstadisticaVoleibol total = (EstadisticaVoleibol) new EstadisticaVoleibol(jugador, 4, 2).crearVacia();
        total.acumular(new EstadisticaVoleibol(jugador, 4, 2));
        total.acumular(new EstadisticaVoleibol(jugador, 3, 1));
        assertEquals(7, total.getAces());
        assertEquals(3, total.getBloqueos());
        assertEquals(7, total.getValorDestacado());
    }
}
