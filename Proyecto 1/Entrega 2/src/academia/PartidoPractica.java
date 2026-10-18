package academia;

import java.time.LocalDate;
import deportes.Deporte;

public class PartidoPractica {
    private LocalDate fecha;
    private Deporte deporte;
    private String observacion;
    public PartidoPractica(LocalDate fecha, Deporte deporte, String observacion) {
        this.fecha = fecha;
        this.deporte = deporte;
        this.observacion = observacion;
    }
    public LocalDate getFecha() {
	return fecha;
	}
    public Deporte getDeporte() {
	return deporte;
	}
    public String getObservacion() {
	return observacion;
	}
}
