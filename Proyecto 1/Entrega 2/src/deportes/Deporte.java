package deportes;

public class Deporte {
	private String nombre;
	private int minJugadoresPartido;
	private int puntosGanar;
	private int puntosEmpate;
	public Deporte(String nombre, int minJugadoresPartido, int puntosGanar, int puntosEmpate) {
		this.nombre = nombre;
		this.minJugadoresPartido = minJugadoresPartido;
		this.puntosGanar = puntosGanar;
		this.puntosEmpate = puntosEmpate;
	}
	public String getNombre() {
		return nombre;
	}
	public int getMinJugadoresPartido() {
		return minJugadoresPartido;
	}
	public int getPuntosGanar() {
		return puntosGanar;
	}
	public int getPuntosEmpate() {
		return puntosEmpate;
	}
	public int getPuntosPorResultado(String resultado) {
		if (resultado.equals("VICTORIA")) {
			return puntosGanar;
		} else if (resultado.equals("EMPATE")) {
			return puntosEmpate;
		}
		return 0;
	}
	}

