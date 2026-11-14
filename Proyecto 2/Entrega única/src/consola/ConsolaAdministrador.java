package consola;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import reservas.Instalacion;
import administracion.SugerenciaSnack;
import administracion.ReporteDeportivo;
import administracion.ReporteVentas;
import deportes.Deporte;
import deportes.Categoria;
import deportes.Equipo;
import torneos.Encuentro;
import usuarios.Socio;
import usuarios.Entrenador;
import usuarios.Fisioterapeuta;
import torneos.Torneo;
import usuarios.Administrador;
import usuarios.Usuario;

public final class ConsolaAdministrador {
    public static void main(String[] args) {
        ConsolaComun c = new ConsolaComun();
        try {
            c.configuracionInicial();
            Usuario u=c.autenticar();
            if (!(u instanceof Administrador)) { System.out.println("Acceso exclusivo para administradores."); return; }
            Administrador admin=(Administrador)u;
            int opcion;
            do {
                System.out.println("\n1. Consultar torneos\n2. Crear torneo\n3. Liberar cupos preferenciales\n4. Iniciar torneo\n5. Usuarios registrados\n6. Registrar usuario\n7. Registrar deporte\n8. Registrar resultado\n9. Registrar categoria\n10. Consultar categorias\n11. Crear equipo\n12. Consultar equipos\n13. Registrar instalacion\n14. Configurar maximo simultaneo\n15. Revisar sugerencias de snacks\n16. Consultar reportes por fechas\n0. Salir");
                opcion=c.entero("Opcion",0,16);
                switch(opcion) {
                    case 1: c.historial(); break;
                    case 2: c.ejecutarSeguro(() -> {
                        List<Deporte> deportes=c.club.getAreaDeportiva().getDeportes();
                        if(deportes.isEmpty()) { System.out.println("Registre primero los deportes en el sistema."); return; }
                        for(int i=0;i<deportes.size();i++) System.out.println((i+1)+". "+deportes.get(i).getNombre());
                        Deporte d=deportes.get(c.entero("Deporte",1,deportes.size())-1);
                        String nombre=c.texto("Nombre torneo");
                        Torneo.Modalidad modalidad=Torneo.Modalidad.values()[c.entero("Modalidad: 1. Conjunto  2. Individual  3. Dobles",1,3)-1];
                        Categoria categoria=null;
                        String nivel=null;
                        if(modalidad==Torneo.Modalidad.CONJUNTO) {
                            List<Categoria> categorias=c.club.getAreaDeportiva().getCategorias();
                            if(categorias.isEmpty()) { System.out.println("Registre primero una categoria (opcion 9)."); return; }
                            for(int i=0;i<categorias.size();i++) System.out.println((i+1)+". "+categorias.get(i).getNombre());
                            categoria=categorias.get(c.entero("Categoria",1,categorias.size())-1);
                        } else nivel=c.texto("Nivel");
                        Torneo.Formato formato=Torneo.Formato.values()[c.entero("Formato: 1. Liga  2. Eliminacion",1,2)-1];
                        int cupos=c.entero("Cupos",2,1000); LocalDate inicio=c.fecha("Inicio"); LocalDate fin=c.fecha("Fin");
                        if(c.entero("1. Recreativo  2. Competitivo",1,2)==1)
                            c.club.getGestionTorneos().crearTorneoRecreativo(admin,nombre,d,modalidad,categoria,nivel,formato,cupos,inicio,fin,c.decimal("Porcentaje bono (0 a 100)",0,100));
                        else c.club.getGestionTorneos().crearTorneoCompetitivo(admin,nombre,d,modalidad,categoria,nivel,formato,cupos,inicio,fin,c.decimal("Tarifa",0,100000000),c.decimal("Porcentaje premio (0 a 100)",0,100));
                        System.out.println("Torneo creado.");
                    }); break;
                    case 3: c.ejecutarSeguro(() -> { Torneo t=c.seleccionarTorneo(); if(t!=null) { c.club.getGestionTorneos().liberarCuposPreferenciales(admin,t); System.out.println("Cupos liberados."); } }); break;
                    case 4: c.ejecutarSeguro(() -> { Torneo t=c.seleccionarTorneo(); if(t!=null) { c.club.getGestionTorneos().iniciarTorneo(admin,t); System.out.println("Torneo iniciado."); } }); break;
                    case 5: c.club.getUsuarios().forEach(x -> System.out.println(x.getLogin()+" - "+x.getNombre())); break;
                    case 6: c.ejecutarSeguro(() -> {
                        int tipo=c.entero("1. Socio  2. Entrenador  3. Fisioterapeuta",1,3);
                        String login=c.texto("Login"), clave=c.texto("Contrasena"), nombre=c.texto("Nombre");
                        Usuario nuevo=tipo==1?new Socio(login,clave,nombre,0):tipo==2?
                            new Entrenador(login,clave,nombre,c.texto("Especialidad")):
                            new Fisioterapeuta(login,clave,nombre);
                        System.out.println(admin.registrarUsuario(c.club,nuevo)?"Usuario creado.":"No se pudo registrar.");
                    }); break;
                    case 7: c.ejecutarSeguro(() -> {
                        String nombre=c.texto("Nombre deporte");
                        c.club.getAreaDeportiva().agregarDeporte(new Deporte(nombre,c.entero("Minimo jugadores",1,100),
                            c.entero("Puntos victoria",0,100),c.entero("Puntos empate",0,100)));
                        System.out.println("Deporte registrado.");
                    }); break;
                    case 8: c.ejecutarSeguro(() -> {
                        Torneo t=c.seleccionarTorneo(); if(t==null) return;
                        List<Encuentro> encuentros=t.getEncuentros();
                        if(encuentros.isEmpty()) { System.out.println("No hay encuentros generados. Inicie el torneo primero."); return; }
                        for(int i=0;i<encuentros.size();i++) {
                            Encuentro e=encuentros.get(i);
                            System.out.println((i+1)+". Ronda "+e.getRonda()+" - "+e.getCompetidorA().getNombre()+" vs "+
                                (e.getCompetidorB()==null?"LIBRE":e.getCompetidorB().getNombre())+
                                (e.estaJugado()?" [JUGADO]":" [PENDIENTE]"));
                        }
                        Encuentro e=encuentros.get(c.entero("Encuentro",1,encuentros.size())-1);
                        c.club.getGestionTorneos().registrarResultado(admin,t,e,c.entero("Marcador A",0,10000),c.entero("Marcador B",0,10000));
                        System.out.println("Resultado registrado.");
                    }); break;
                    case 9: c.ejecutarSeguro(() -> {
                        String nombre=c.texto("Nombre categoria");
                        for(Categoria existente:c.club.getAreaDeportiva().getCategorias()) {
                            if(existente.getNombre().equalsIgnoreCase(nombre)) {
                                System.out.println("Ya existe una categoria con ese nombre."); return;
                            }
                        }
                        int edadMinima=c.entero("Edad minima",0,120);
                        int edadMaxima=c.entero("Edad maxima",edadMinima,120);
                        c.club.getAreaDeportiva().agregarCategoria(new Categoria(nombre,edadMinima,edadMaxima));
                        System.out.println("Categoria registrada.");
                    }); break;
                    case 10: {
                        List<Categoria> categorias=c.club.getAreaDeportiva().getCategorias();
                        if(categorias.isEmpty()) System.out.println("No hay categorias registradas.");
                        else for(int i=0;i<categorias.size();i++) {
                            Categoria cat=categorias.get(i);
                            System.out.println((i+1)+". "+cat.getNombre()+" ("+cat.getEdadMinima()+" a "+cat.getEdadMaxima()+" anos)");
                        }
                        break;
                    }
                    case 11: c.ejecutarSeguro(() -> {
                        List<Deporte> deportes=c.club.getAreaDeportiva().getDeportes();
                        List<Categoria> categorias=c.club.getAreaDeportiva().getCategorias();
                        if(deportes.isEmpty() || categorias.isEmpty()) { System.out.println("Registre primero un deporte y una categoria."); return; }
                        String nombre=c.texto("Nombre equipo");
                        for(Equipo existente:c.club.getAreaDeportiva().getEquipos())
                            if(existente.getNombre().equalsIgnoreCase(nombre)) { System.out.println("Ya existe un equipo con ese nombre."); return; }
                        for(int i=0;i<deportes.size();i++) System.out.println((i+1)+". "+deportes.get(i).getNombre());
                        Deporte deporte=deportes.get(c.entero("Deporte",1,deportes.size())-1);
                        for(int i=0;i<categorias.size();i++) System.out.println((i+1)+". "+categorias.get(i).getNombre());
                        Categoria categoria=categorias.get(c.entero("Categoria",1,categorias.size())-1);
                        int cupo=c.entero("Maximo de integrantes",deporte.getMinJugadoresPartido(),100);
                        c.club.getAreaDeportiva().crearEquipo(nombre,deporte,categoria,cupo);
                        System.out.println("Equipo creado. Sus integrantes pueden unirse desde ConsolaSocio.");
                    }); break;
                    case 12: {
                        List<Equipo> equipos=c.club.getAreaDeportiva().getEquipos();
                        if(equipos.isEmpty()) System.out.println("No hay equipos registrados.");
                        for(Equipo e:equipos) System.out.println(e.getNombre()+" | "+e.getDeporte().getNombre()+" | "+e.getCategoria().getNombre()+" | "+e.getIntegrantes().size()+"/"+e.getCupoMaximo()+" integrantes");
                    } break;
                    case 13: c.ejecutarSeguro(() -> {
                        List<Deporte> deportes=c.club.getAreaDeportiva().getDeportes();
                        if(deportes.isEmpty()) { System.out.println("Registre primero un deporte."); return; }
                        String nombre=c.texto("Nombre instalacion");
                        for(Instalacion i:c.club.getGestionReservas().getInstalaciones())
                            if(i.getNombre().equalsIgnoreCase(nombre)) { System.out.println("Nombre ya registrado."); return; }
                        for(int i=0;i<deportes.size();i++) System.out.println((i+1)+". "+deportes.get(i).getNombre());
                        Deporte deporte=deportes.get(c.entero("Deporte",1,deportes.size())-1);
                        int capacidad=c.entero("Capacidad maxima",1,1000);
                        Set<String> modalidades=new HashSet<>();
                        int cantidad=c.entero("Cantidad de modalidades",1,10);
                        for(int i=0;i<cantidad;i++) modalidades.add(c.texto("Modalidad "+(i+1)));
                        Instalacion instalacion=new Instalacion(nombre,deporte,capacidad,modalidades);
                        instalacion.establecerTechada(c.entero("1. Techada  2. Descubierta",1,2)==1);
                        if(!c.club.getGestionReservas().agregarInstalacion(instalacion)) throw new IllegalStateException("No se pudo registrar");
                        System.out.println("Instalacion registrada.");
                    }); break;
                    case 14: c.ejecutarSeguro(() -> {
                        List<Deporte> deportes=c.club.getAreaDeportiva().getDeportes();
                        if(deportes.isEmpty()) { System.out.println("No hay deportes."); return; }
                        for(int i=0;i<deportes.size();i++) System.out.println((i+1)+". "+deportes.get(i).getNombre());
                        String deporte=deportes.get(c.entero("Deporte",1,deportes.size())-1).getNombre();
                        int maximo=c.entero("Maximo de instalaciones simultaneas",1,1000);
                        c.club.getGestionReservas().configurarMaximoSimultaneo(deporte,maximo);
                        System.out.println("Maximo configurado.");
                    }); break;
                    case 15: c.ejecutarSeguro(() -> {
                        List<SugerenciaSnack> sugerencias=c.club.getGestionSugerencias().getSugerencias();
                        if(sugerencias.isEmpty()) { System.out.println("No hay sugerencias."); return; }
                        for(int i=0;i<sugerencias.size();i++) {
                            SugerenciaSnack s=sugerencias.get(i);
                            System.out.println((i+1)+". "+s.getNombre()+" | "+s.getEstado()+" | Autor: "+s.getAutor().getLogin()+" | Alergenos: "+s.getAlergenos()+" | Motivo: "+s.getMotivo());
                        }
                        SugerenciaSnack seleccion=sugerencias.get(c.entero("Sugerencia",1,sugerencias.size())-1);
                        if(seleccion.getEstado()==SugerenciaSnack.Estado.APROBADA || seleccion.getEstado()==SugerenciaSnack.Estado.RECHAZADA) return;
                        int decision=c.entero("1. Aprobar  2. Rechazar  3. Volver",1,3);
                        if(decision==1) c.club.getGestionSugerencias().aprobar(admin,seleccion);
                        if(decision==2) c.club.getGestionSugerencias().rechazar(admin,seleccion,c.texto("Motivo del rechazo"));
                        if(decision==1 || decision==2) System.out.println("Sugerencia resuelta.");
                    }); break;
                    case 16: c.ejecutarSeguro(() -> {
                        LocalDate desde = c.fecha("Fecha inicial");
                        LocalDate hasta = c.fecha("Fecha final");
                        if (desde.isAfter(hasta)) {
                            throw new IllegalArgumentException("La fecha inicial no puede ser posterior a la final");
                        }
                        ReporteDeportivo deportivo = new ReporteDeportivo(c.club, desde, hasta);
                        ReporteVentas ventas = new ReporteVentas(c.club, desde, hasta);

                        System.out.println("\n=== REPORTE DEPORTIVO ===");
                        System.out.println("Torneos: " + deportivo.getTorneos());
                        System.out.println("Torneos finalizados: " + deportivo.getTorneosFinalizados());
                        System.out.println("Torneos abiertos o en curso: " + deportivo.getTorneosEnCursoOAbiertos());
                        System.out.println("Premios en efectivo: " + deportivo.getPremiosEnEfectivo());
                        System.out.println("Bonos otorgados: " + deportivo.getBonosOtorgados());

                        System.out.println("\n=== REPORTE DE VENTAS ===");
                        System.out.println("Ventas de tienda: " + ventas.getVentasTienda());
                        System.out.println("Ventas de cafeteria: " + ventas.getVentasCafeteria());
                        System.out.println("Ingresos totales: " + ventas.getIngresosTotales());
                        System.out.println("Ingresos por inscripciones a torneos: " + ventas.getIngresosTorneos());
                        System.out.println("Ventas con bono de torneo: " + ventas.getVentasConBono());
                    }); break;
                    default: break;
                }
            } while(opcion!=0);
        } finally { c.guardar(); }
    }
}
