package pruebas;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import org.junit.jupiter.api.Test;
import administracion.SugerenciaSnack;
import club.ClubDeportivoSeneca;
import deportes.Deporte;
import reservas.GestionReservas;
import reservas.Instalacion;
import reservas.Reserva;
import usuarios.Administrador;
import usuarios.Entrenador;
import usuarios.Socio;

class HistoriasOficialesR12Test {
    @Test void huSoc01CupoSimultaneoYPadel() {
        GestionReservas g=new GestionReservas();
        Deporte tenis=new Deporte("Tenis",1,3,1);
        Instalacion a=new Instalacion("A",tenis,2,Set.of("individual"));
        Instalacion b=new Instalacion("B",tenis,2,Set.of("individual"));
        g.agregarInstalacion(a);g.agregarInstalacion(b);
        assertTrue(g.configurarMaximoSimultaneo("Tenis",1));
        Socio socio=new Socio("s1","clave","Socio",0);
        LocalDate f=LocalDate.of(2026,12,1);LocalTime i=LocalTime.of(9,0),fin=LocalTime.of(10,0);
        assertNotNull(g.realizarReservaValidada(socio,a,f,i,fin,1,"individual"));
        assertEquals(1,g.consultarReservas(socio).size());
        assertThrows(IllegalStateException.class,()->g.realizarReservaValidada(socio,b,f,i,fin,1,"individual"));
        Instalacion padel=new Instalacion("Padel",new Deporte("Padel",2,3,1),4,Set.of("dobles"));
        g.agregarInstalacion(padel);
        assertThrows(IllegalArgumentException.class,()->g.realizarReservaValidada(socio,padel,f,i,fin,2,"dobles"));
    }
    @Test void huJug01CapacidadYBebidasCalientesSinExcepcionPorTecho() {
        GestionReservas g=new GestionReservas();
        Socio socio=new Socio("s2","clave","Jugador",0);
        Instalacion mesa=new Instalacion("Mesa",new Deporte("Tenis de mesa",1,3,1),2,Set.of("individual"));
        mesa.establecerTechada(false);g.agregarInstalacion(mesa);
        LocalDate f=LocalDate.of(2026,12,1);LocalTime i=LocalTime.of(9,0),fin=LocalTime.of(10,0);
        assertThrows(IllegalArgumentException.class,()->g.realizarReservaValidada(socio,mesa,f,i,fin,3,"individual"));
        Reserva r=g.realizarReservaValidada(socio,mesa,f,i,fin,1,"individual");
        assertFalse(g.permitirUso(r,true));assertTrue(g.permitirUso(r,false));
    }
    @Test void huAdm03AprobacionRechazoYConsulta() {
        ClubDeportivoSeneca club=new ClubDeportivoSeneca("Seneca");
        Administrador admin=new Administrador("admin","clave","Admin");
        Entrenador empleado=new Entrenador("ent","clave","Entrenador","Tenis");
        assertTrue(club.registrarUsuario(admin));assertTrue(club.registrarUsuario(empleado));
        SugerenciaSnack incompleta=club.getGestionSugerencias().sugerirSnack(empleado,"Snack",1000,2,"");
        assertEquals(SugerenciaSnack.Estado.COMPLETAR,incompleta.getEstado());
        assertThrows(IllegalStateException.class,()->club.getGestionSugerencias().aprobar(admin,incompleta));
        assertNull(club.getCafeteria().buscarProducto("Snack"));
        assertEquals(SugerenciaSnack.Estado.COMPLETAR, incompleta.getEstado());
        club.getGestionSugerencias().completarAlergenos(empleado,incompleta,"Ninguno");
        assertEquals(SugerenciaSnack.Estado.PENDIENTE,incompleta.getEstado());
        SugerenciaSnack aprobado=incompleta;
        club.getGestionSugerencias().aprobar(admin,aprobado);
        assertNotNull(club.getCafeteria().buscarProducto("Snack"));
        SugerenciaSnack rechazado=club.getGestionSugerencias().sugerirSnack(empleado,"Otro",1000,2,"Gluten");
        club.getGestionSugerencias().rechazar(admin,rechazado,"No cumple");
        assertEquals(SugerenciaSnack.Estado.RECHAZADA,rechazado.getEstado());
        assertEquals("No cumple",rechazado.getMotivo());
        assertNull(club.getCafeteria().buscarProducto("Otro"));
        assertTrue(club.getGestionSugerencias().getSugerencias().contains(rechazado));
    }
    @Test
    void huJug01MaximoInstalacionesSimultaneas() {
        GestionReservas gestion = new GestionReservas();
        Socio socio = new Socio("s3", "clave", "Jugador", 0);
        Deporte tenis = new Deporte("Tenis", 2, 3, 1);
        Instalacion cancha1 = new Instalacion(
                "Cancha 1", tenis, 2, Set.of("Individual"));
        Instalacion cancha2 = new Instalacion(
                "Cancha 2", tenis, 2, Set.of("Individual"));
        gestion.agregarInstalacion(cancha1);
        gestion.agregarInstalacion(cancha2);
        assertTrue(gestion.configurarMaximoSimultaneo("Tenis", 1));
        LocalDate fecha = LocalDate.of(2026, 12, 1);
        Reserva primera = gestion.realizarReservaValidada(
                socio, cancha1, fecha,
                LocalTime.of(10, 0),
                LocalTime.of(11, 0),
                2, "Individual");
        assertNotNull(primera);
        assertThrows(IllegalStateException.class, () ->
                gestion.realizarReservaValidada(
                        socio, cancha2, fecha,
                        LocalTime.of(10, 30),
                        LocalTime.of(11, 30),
                        2, "Individual")
        );
        Reserva posterior = gestion.realizarReservaValidada(
                socio, cancha2, fecha,
                LocalTime.of(11, 30),
                LocalTime.of(12, 30),
                2, "Individual");
        assertNotNull(posterior);
    }

    @Test void huAdm03SoloAutorPuedeCompletarYEstadoSeActualiza() {
        ClubDeportivoSeneca club=new ClubDeportivoSeneca("Seneca");
        Entrenador autor=new Entrenador("autor","clave","Autor","Tenis");
        Entrenador ajeno=new Entrenador("ajeno","clave","Ajeno","Tenis");
        Administrador admin=new Administrador("admin","clave","Admin");
        club.registrarUsuario(autor);club.registrarUsuario(ajeno);club.registrarUsuario(admin);
        SugerenciaSnack s=club.getGestionSugerencias().sugerirSnack(autor,"SnackNuevo",2500,5,"");
        assertEquals(SugerenciaSnack.Estado.COMPLETAR,s.getEstado());
        assertThrows(SecurityException.class,()->club.getGestionSugerencias().completarAlergenos(ajeno,s,"Ninguno"));
        assertEquals(SugerenciaSnack.Estado.COMPLETAR,s.getEstado());
        assertThrows(IllegalStateException.class,()->club.getGestionSugerencias().aprobar(admin,s));
        club.getGestionSugerencias().completarAlergenos(autor,s,"Ninguno");
        assertEquals(SugerenciaSnack.Estado.PENDIENTE,s.getEstado());
        club.getGestionSugerencias().aprobar(admin,s);
        assertEquals(SugerenciaSnack.Estado.APROBADA,s.getEstado());
        assertThrows(IllegalStateException.class,()->club.getGestionSugerencias().completarAlergenos(autor,s,"Gluten"));
    }
    @Test void huAdm03SnackAprobadoSePuedeVenderYDescuentaExistencias() {
        ClubDeportivoSeneca club=new ClubDeportivoSeneca("Seneca");
        Entrenador autor=new Entrenador("autor","clave","Autor","Tenis");
        Administrador admin=new Administrador("admin","clave","Admin");
        Socio comprador=new Socio("comprador","clave","Comprador",0);
        club.registrarUsuario(autor);club.registrarUsuario(admin);club.registrarUsuario(comprador);
        SugerenciaSnack s=club.getGestionSugerencias().sugerirSnack(autor,"SnackVenta",2500,5,"Ninguno");
        assertNull(club.getCafeteria().buscarProducto("SnackVenta"));
        club.getGestionSugerencias().aprobar(admin,s);
        assertNotNull(club.getCafeteria().buscarProducto("SnackVenta"));
        assertEquals(5,club.getCafeteria().buscarProducto("SnackVenta").getCantidad());
        assertNotNull(club.getCafeteria().registrarVentaValidada(comprador,"SnackVenta",2,0));
        assertEquals(3,club.getCafeteria().buscarProducto("SnackVenta").getCantidad());
        assertEquals(1,club.getCafeteria().getVentas().size());
    }
}
