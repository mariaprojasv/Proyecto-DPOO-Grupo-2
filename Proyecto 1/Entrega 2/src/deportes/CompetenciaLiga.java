package deportes;

import java.util.ArrayList;

public class CompetenciaLiga extends Competencia {
	private ArrayList<PosicionTabla> tabla;

	public CompetenciaLiga(String nombre, String organizador, String temporada, int nominaMinima, Deporte deporte, Categoria categoria) {
		super(nombre, organizador, temporada, nominaMinima, deporte, categoria);
		this.tabla = new ArrayList<>();
	}

	public ArrayList<PosicionTabla> getTablaPosiciones() {
		return tabla;
	}

	@Override
	public void registrarPartido(PartidoCompetencia p) {
		getPartidos().add(p);
		actualizarTabla(p);
	}

	public void actualizarTabla(PartidoCompetencia p) {
		PosicionTabla fila = null;
		for (PosicionTabla pt : tabla) {
			if (pt.getEquipo() == p.getEquipoClub()) {
				fila = pt;
			}
		}
		if (fila == null) {
			fila = new PosicionTabla(p.getEquipoClub());
			tabla.add(fila);
		}
		String resultado = p.getResultado();
		fila.registrarResultado(resultado, getDeporte().getPuntosPorResultado(resultado));
	}
}

		
