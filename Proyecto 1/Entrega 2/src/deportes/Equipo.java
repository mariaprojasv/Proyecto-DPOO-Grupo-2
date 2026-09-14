package deportes;

import java.util.ArrayList;
import usuarios.Jugador;

public class Equipo {
	private String nombre;
	private int cupoMaximo;
	private Deporte deporte;
	private Categoria categoria;
	private ArrayList<InscripcionEquipo> inscripciones;
	public Equipo(String nombre, int cupoMaximo, Deporte deporte, Categoria categoria) {
		this.nombre = nombre;
		this.cupoMaximo = cupoMaximo;
		this.deporte = deporte;
		this.categoria = categoria;
		this.inscripciones = new ArrayList<>();
	}
	public String getNombre() {
		return nombre;
	}
	public int getCupoMaximo() {
		return cupoMaximo;
	}
	public Deporte getDeporte() {
		return deporte;
	}
	public Categoria getCategoria() {
		return categoria;
	}
	public ArrayList<InscripcionEquipo> getInscripciones() {
		return inscripciones;
	}
	
	public boolean numeroDisponible(int numero) {
		for (InscripcionEquipo ins : inscripciones) {
			if (ins.getNumeroCamiseta() == numero) {
				return false;
			}
		}
		return true;
		
	}
	public ArrayList<Jugador> getJugadores() {
	    ArrayList<Jugador> jugadores = new ArrayList<>();

	    for (InscripcionEquipo ins : inscripciones) {
	        jugadores.add(ins.getJugador());
	    }

	    return jugadores;
	}
	public boolean tieneMinimoJugadores() {
	    return inscripciones.size() >= deporte.getMinJugadoresPartido();
	}
	public void retirarJugador(Jugador j) {
	    for (InscripcionEquipo ins : inscripciones) {
	        if (ins.getJugador() == j) {
	            inscripciones.remove(ins);
	            return;
	        }
	    }
	}
	public boolean agregarJugador(Jugador j, String posicion, int numero) {

	    if (inscripciones.size() >= cupoMaximo) {
	        return false;
	    }

	    if (categoria.edadPermitida(j.getEdad()) == false) {
	        return false;
	    }

	    if (!numeroDisponible(numero)) {
	        return false;
	    }

	    inscripciones.add(new InscripcionEquipo(j, posicion, numero));

	    return true;
	}
	
	
	
}
