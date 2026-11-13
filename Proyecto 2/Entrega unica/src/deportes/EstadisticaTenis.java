package deportes;

import usuarios.Jugador;

public class EstadisticaTenis extends EstadisticaIndividual {
    private int aces;

    public EstadisticaTenis(Jugador jugador, int ganados, int perdidos, int aces) {
        super(jugador, ganados, perdidos);
        this.aces = aces;
    }

    public int getAces() {
	return aces;
	}

    @Override
    public void acumular(Estadistica otra) {
        if (otra instanceof EstadisticaTenis) {
            EstadisticaTenis e = (EstadisticaTenis) otra;
            acumularResultados(e);
            aces += e.getAces();
        }
    }

    @Override
    public int getValorDestacado() {
	return getPartidosGanados();
	}

    @Override
    public Estadistica crearVacia() {
        return new EstadisticaTenis(getJugador(), 0, 0, 0);
    }
}
