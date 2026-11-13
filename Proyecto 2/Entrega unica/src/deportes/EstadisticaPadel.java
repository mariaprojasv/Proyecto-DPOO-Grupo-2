package deportes;

import usuarios.Jugador;

public class EstadisticaPadel extends EstadisticaIndividual {
    private int puntosGanados;

    public EstadisticaPadel(Jugador jugador, int ganados, int perdidos, int puntosGanados) {
        super(jugador, ganados, perdidos);
        this.puntosGanados = puntosGanados;
    }

    public int getPuntosGanados() {
	return puntosGanados;
	}

    @Override
    public void acumular(Estadistica otra) {
        if (otra instanceof EstadisticaPadel) {
            EstadisticaPadel e = (EstadisticaPadel) otra;
            acumularResultados(e);
            puntosGanados += e.getPuntosGanados();
        }
    }

    @Override
    public int getValorDestacado() {
	return getPartidosGanados();
	}

    @Override
    public Estadistica crearVacia() {
        return new EstadisticaPadel(getJugador(), 0, 0, 0);
    }
}
