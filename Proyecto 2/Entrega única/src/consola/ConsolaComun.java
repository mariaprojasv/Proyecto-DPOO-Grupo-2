package consola;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Scanner;
import club.ClubDeportivoSeneca;
import persistencia.PersistenciaArchivos;
import torneos.Torneo;
import usuarios.Usuario;
import usuarios.Administrador;

public final class ConsolaComun {
    private final Scanner entrada = new Scanner(System.in);
    private final PersistenciaArchivos archivos = new PersistenciaArchivos("datos_proyecto2");
    public final ClubDeportivoSeneca club = new ClubDeportivoSeneca("Club Deportivo Seneca");
    public ConsolaComun() {
        try { archivos.cargar(club); System.out.println("Datos cargados."); }
        catch (IOException e) { System.out.println("No se pudieron cargar los datos: " + e.getMessage()); }
    }
    public void guardar() {
        try { archivos.guardar(club); System.out.println("Cambios guardados."); }
        catch (IOException e) { System.out.println("ERROR al guardar: " + e.getMessage()); }
    }
    public String texto(String etiqueta) {
        while (true) {
            System.out.print(etiqueta + ": ");
            String valor = entrada.nextLine().trim();
            if (!valor.isEmpty() && !valor.contains("|")) return valor;
            System.out.println("Ingrese un valor no vacio y sin el caracter |.");
        }
    }
    /** Permite dejar el campo pendiente; mantiene la restriccion del delimitador de persistencia. */
    public String textoOpcional(String etiqueta) {
        while (true) {
            System.out.print(etiqueta + ": ");
            String valor = entrada.nextLine().trim();
            if (!valor.contains("|")) return valor;
            System.out.println("Ingrese un valor sin el caracter | (puede dejarlo vacio).");
        }
    }
    public int entero(String etiqueta, int minimo, int maximo) {
        while (true) {
            try { int n = Integer.parseInt(texto(etiqueta)); if (n >= minimo && n <= maximo) return n; }
            catch (NumberFormatException ignored) { }
            System.out.println("Ingrese un entero entre " + minimo + " y " + maximo + ".");
        }
    }
    public double decimal(String etiqueta, double minimo, double maximo) {
        while (true) {
            try { double n = Double.parseDouble(texto(etiqueta)); if (Double.isFinite(n) && n >= minimo && n <= maximo) return n; }
            catch (NumberFormatException ignored) { }
            System.out.println("Ingrese un numero entre " + minimo + " y " + maximo + ".");
        }
    }
    public static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd-MM-uuuu").withResolverStyle(ResolverStyle.STRICT);
    public LocalDate fecha(String etiqueta) {
        while (true) { try { return LocalDate.parse(texto(etiqueta + " (DD-MM-AAAA)"), FORMATO_FECHA); }
            catch (RuntimeException e) { System.out.println("Fecha invalida."); } }
    }
    public LocalTime hora(String etiqueta) {
        while (true) { try { return LocalTime.parse(texto(etiqueta + " (HH:MM)")); }
            catch (RuntimeException e) { System.out.println("Hora invalida."); } }
    }
    public void configuracionInicial() {
        if (!club.getUsuarios().isEmpty()) return;
        System.out.println("Configuracion inicial: no hay usuarios. Cree el primer administrador.");
        String login=texto("Login del administrador");
        String clave=texto("Contrasena del administrador");
        String nombre=texto("Nombre del administrador");
        if (!club.registrarUsuario(new Administrador(login,clave,nombre)))
            throw new IllegalStateException("No se pudo crear el administrador inicial");
        guardar();
    }
    public Usuario autenticar() {
        for (int intento=0; intento<3; intento++) {
            Usuario usuario = club.iniciarSesion(texto("Usuario"), texto("Contrasena"));
            if (usuario != null) return usuario;
            System.out.println("Credenciales incorrectas.");
        }
        return null;
    }
    public Torneo seleccionarTorneo() {
        List<Torneo> torneos = club.getGestionTorneos().getTorneos();
        if (torneos.isEmpty()) { System.out.println("No hay torneos registrados."); return null; }
        for (int i=0; i<torneos.size(); i++) System.out.println((i+1) + ". " + torneos.get(i).getNombre() + " [" + torneos.get(i).getEstado() + "]");
        return torneos.get(entero("Numero de torneo",1,torneos.size())-1);
    }
    public void historial() {
        Torneo torneo = seleccionarTorneo();
        if (torneo != null) System.out.println(club.getGestionTorneos().consultarHistorial(torneo));
    }
    public void ejecutarSeguro(Runnable accion) {
        try { accion.run(); } catch (RuntimeException e) { System.out.println("Operacion rechazada: " + e.getMessage()); }
    }
}
