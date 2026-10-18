package pruebas;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import club.ClubDeportivoSeneca;
import persistencia.PersistenciaArchivos;
import usuarios.Socio;
import usuarios.Entrenador;
import usuarios.Fisioterapeuta;
import cafeteria.snack;
import cafeteria.VentaCafeteria;

public class PruebaPersistenciaTurnosCafeteria {
    private static int verificaciones = 0;

    private static void verificar(boolean condicion) {
        if (!condicion) {
            throw new RuntimeException("Fallo en verificacion " + (verificaciones + 1));
        }
        verificaciones++;
    }

    public static void main(String[] args) throws Exception {
        ClubDeportivoSeneca original = new ClubDeportivoSeneca("Seneca");
        Socio socio = new Socio("socio13", "123", "Socio", 0);
        original.registrarUsuario(socio);
        original.getCafeteria().agregarProducto(new snack("Galleta", 2000, 12, "Ninguno"));
        VentaCafeteria venta = original.getCafeteria().registrarVenta(socio, "Galleta", 3, 700);
        verificar(venta != null);
        venta.establecerFecha(LocalDate.of(2026, 10, 1));
        original.getGestionTurnos().agregarTurno(new Entrenador("ent13a", "123", "Entrenador A", "Futbol"), LocalTime.of(8, 0), LocalTime.of(12, 0));
        original.getGestionTurnos().agregarTurno(new Entrenador("ent13b", "123", "Entrenador B", "Voleibol"), LocalTime.of(8, 0), LocalTime.of(12, 0));
        original.getGestionTurnos().agregarTurno(new Fisioterapeuta("fis13", "123", "Fisio"), LocalTime.of(8, 0), LocalTime.of(12, 0));
        File carpeta = new File("data/prueba_turnos_cafeteria");
        PersistenciaArchivos persistencia = new PersistenciaArchivos(carpeta.getPath());
        persistencia.guardar(original);
        verificar(new File(carpeta, "turnos.txt").exists());
        verificar(new File(carpeta, "ventas_cafeteria.txt").exists());
        ClubDeportivoSeneca recuperado = new ClubDeportivoSeneca("Seneca");
        persistencia.cargar(recuperado);
        verificar(recuperado.getGestionTurnos().getTurnos().size() == 3);
        verificar(recuperado.getGestionTurnos().coberturaCompleta(LocalTime.of(8, 0), LocalTime.of(12, 0)));
        verificar(recuperado.buscarUsuario("fis13") instanceof Fisioterapeuta);
        verificar(recuperado.getCafeteria().getVentas().size() == 1);
        VentaCafeteria recuperada = recuperado.getCafeteria().getVentas().get(0);
        verificar(recuperada.getFecha().equals(LocalDate.of(2026, 10, 1)));
        verificar(recuperada.getCantidad() == 3);
        verificar(recuperada.getTotal() == 7180);
        verificar(recuperado.getCafeteria().buscarProducto("Galleta").getCantidad() == 9);
        persistencia.guardar(recuperado);
        ClubDeportivoSeneca segunda = new ClubDeportivoSeneca("Seneca");
        persistencia.cargar(segunda);
        verificar(segunda.getCafeteria().buscarProducto("Galleta").getCantidad() == 9);
        verificar(segunda.getGestionTurnos().getTurnos().size() == 3);
        System.out.println("Revision 13: " + verificaciones + " verificaciones correctas");
    }
}
