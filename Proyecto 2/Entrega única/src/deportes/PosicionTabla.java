package deportes;

public class PosicionTabla {
	private Equipo equipo;
	private int puntos;
	private int partidosJugados;
	private int ganados;
	private int empatados;
	private int perdidos;
	public PosicionTabla(Equipo equipo) {
		this.equipo = equipo;
		this.puntos = 0;
		this.partidosJugados = 0;
		this.ganados = 0;
		this.empatados = 0;
		this.perdidos = 0;
	}
    /** Copia los contadores sin compartir el estado mutable de la fila. */
    public PosicionTabla(PosicionTabla original) {
        this.equipo = original.equipo;
        this.puntos = original.puntos;
        this.partidosJugados = original.partidosJugados;
        this.ganados = original.ganados;
        this.empatados = original.empatados;
        this.perdidos = original.perdidos;
    }
	public Equipo getEquipo() {
	return equipo;
	}
	public int getPuntos() {
	return puntos;
	}
	public int getPartidosJugados() {
	return partidosJugados;
	}
	public int getGanados() {
	return ganados;
	}
	public int getEmpatados() {
	return empatados;
	}
	public int getPerdidos() {
	return perdidos;
	}
	public void registrarResultado(String resultado, int puntos) {
		this.partidosJugados += 1;
		this.puntos += puntos;
		if (resultado.equals("VICTORIA")) {
			ganados += 1;
		} else if (resultado.equals("EMPATE")) {
			empatados += 1;
		} else {
			perdidos += 1;
		}
	}
	
}
