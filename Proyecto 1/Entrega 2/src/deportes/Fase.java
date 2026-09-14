package deportes;

import java.util.ArrayList;

public class Fase {
	private String nombre;
	private boolean esEliminacion;
	private ArrayList<Grupo> grupos;

	public Fase(String nombre, boolean esEliminacion) {
		this.nombre = nombre;
		this.esEliminacion = esEliminacion;
		this.grupos = new ArrayList<>();
	}

	public String getNombre() {
		return nombre;
	}

	public boolean isEsEliminacion() {
		return esEliminacion;
	}

	public ArrayList<Grupo> getGrupos() {
		return grupos;
	}

	public void agregarGrupo(Grupo g) {
		grupos.add(g);
	}
}