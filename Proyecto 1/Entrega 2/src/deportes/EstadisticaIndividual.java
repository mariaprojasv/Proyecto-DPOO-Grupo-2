package deportes;

import usuarios.Jugador;

public abstract class EstadisticaIndividual extends Estadistica {
    private int partidosGanados;
    private int partidosPerdidos;

    public EstadisticaIndividual(Jugador jugador, int partidosGanados, int partidosPerdidos) {
        super(jugador);
        this.partidosGanados = partidosGanados;
        this.partidosPerdidos = partidosPerdidos;
    }

    public int getPartidosGanados() {
	return partidosGanados;
	}

    public int getPartidosPerdidos() {
	return partidosPerdidos;
	}

    public void acumularResultados(EstadisticaIndividual otra) {
        partidosGanados += otra.getPartidosGanados();
        partidosPerdidos += otra.getPartidosPerdidos();
    }
}
