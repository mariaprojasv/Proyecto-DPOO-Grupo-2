package deportes;

import java.util.ArrayList;

public class CompetenciaLiga extends Competencia {
	private ArrayList<PosicionTabla> tabla;

	public CompetenciaLiga(String nombre, String organizador, String temporada, int nominaMinima, Deporte deporte, Categoria categoria) {
		super(nombre, organizador, temporada, nominaMinima, deporte, categoria);
		this.tabla = new ArrayList<>();
	}

    /** Devuelve una instantanea independiente de las posiciones actuales. */
    public ArrayList<PosicionTabla> getTablaPosiciones() {
        reconstruirTabla();
        ArrayList<PosicionTabla> copia = new ArrayList<>();
        for (PosicionTabla posicion : tabla) {
            copia.add(new PosicionTabla(posicion));
        }
        return copia;
    }

    @Override
    protected void despuesDeRegistrarPartido(PartidoCompetencia p) {
        reconstruirTabla();
    }

    /** Reconstruye las posiciones a partir de los marcadores confirmados actuales. */
    private void reconstruirTabla() {
        tabla.clear();
        for (PartidoCompetencia partido : getPartidos()) {
            agregarResultado(partido);
        }
    }

    private void agregarResultado(PartidoCompetencia p) {
        if (p == null || !p.tieneResultadoRegistrado()) {
            return;
        }
        PosicionTabla fila = null;
        for (PosicionTabla posicion : tabla) {
            if (posicion.getEquipo() == p.getEquipoClub()) {
                fila = posicion;
                break;
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
