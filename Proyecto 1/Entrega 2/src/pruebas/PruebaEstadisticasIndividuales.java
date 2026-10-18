package pruebas;

import java.time.LocalDate;
import deportes.Estadistica;
import deportes.EstadisticaTenis;
import deportes.EstadisticaPadel;
import deportes.EstadisticaTenisMesa;
import usuarios.Jugador;

public class PruebaEstadisticasIndividuales {
    public static void main(String[] args) {
        Jugador jugador = new Jugador("individual", "1234", "Jugador Individual", 0, LocalDate.of(2000, 1, 1));
        EstadisticaTenis tenis = new EstadisticaTenis(jugador, 2, 1, 5);
        Estadistica acumuladaTenis = tenis.crearVacia();
        acumuladaTenis.acumular(tenis);
        acumuladaTenis.acumular(new EstadisticaTenis(jugador, 1, 0, 3));
        comprobar(((EstadisticaTenis) acumuladaTenis).getPartidosGanados() == 3, "Victorias tenis");
        comprobar(((EstadisticaTenis) acumuladaTenis).getAces() == 8, "Aces tenis");

        EstadisticaPadel padel = new EstadisticaPadel(jugador, 1, 2, 20);
        Estadistica acumuladaPadel = padel.crearVacia();
        acumuladaPadel.acumular(padel);
        comprobar(((EstadisticaPadel) acumuladaPadel).getPuntosGanados() == 20, "Puntos padel");

        EstadisticaTenisMesa mesa = new EstadisticaTenisMesa(jugador, 3, 1, 9);
        Estadistica acumuladaMesa = mesa.crearVacia();
        acumuladaMesa.acumular(mesa);
        comprobar(((EstadisticaTenisMesa) acumuladaMesa).getSetsGanados() == 9, "Sets tenis de mesa");
        comprobar(acumuladaMesa.getValorDestacado() == 3, "Valor destacado");
        System.out.println("Pruebas de estadisticas individuales superadas: 5/5");
    }

    private static void comprobar(boolean condicion, String mensaje) {
        if (!condicion) {
            throw new RuntimeException("Fallo: " + mensaje);
        }
    }
}
