package deportes;

import usuarios.Jugador;

public class EstadisticaTenisMesa extends EstadisticaIndividual {
    private int setsGanados;

    public EstadisticaTenisMesa(Jugador jugador, int ganados, int perdidos, int setsGanados) {
        super(jugador, ganados, perdidos);
        this.setsGanados = setsGanados;
    }

    public int getSetsGanados() {
	return setsGanados;
	}

    @Override
    public void acumular(Estadistica otra) {
        if (otra instanceof EstadisticaTenisMesa) {
            EstadisticaTenisMesa e = (EstadisticaTenisMesa) otra;
            acumularResultados(e);
            setsGanados += e.getSetsGanados();
        }
    }

    @Override
    public int getValorDestacado() {
	return getPartidosGanados();
	}

    @Override
    public Estadistica crearVacia() {
        return new EstadisticaTenisMesa(getJugador(), 0, 0, 0);
    }
}
