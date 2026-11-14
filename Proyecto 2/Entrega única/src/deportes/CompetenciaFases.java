package deportes;

import java.util.ArrayList;

public class CompetenciaFases extends Competencia {
	private ArrayList<Fase> fases;
	private int faseActual;

	public CompetenciaFases(String nombre, String organizador, String temporada, int nominaMinima, Deporte deporte, Categoria categoria) {
		super(nombre, organizador, temporada, nominaMinima, deporte, categoria);
		this.fases = new ArrayList<>();
		this.faseActual = 0;
	}

	public ArrayList<Fase> getFases() {
	return fases;
	}

	public int getFaseActual() {
	return faseActual;
	}

	public void agregarFase(Fase f) {

	    if (f != null && !fases.contains(f)) {
            fases.add(f);
        }

	}

	public void avanzarFase() {

	    if (faseActual + 1 < fases.size()) {
            faseActual += 1;
        }

	}

}