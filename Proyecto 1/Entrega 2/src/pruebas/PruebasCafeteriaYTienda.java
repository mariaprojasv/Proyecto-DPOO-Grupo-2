package pruebas;

import java.util.Scanner;

import cafeteria.bebida;
import cafeteria.Cafeteria;
import cafeteria.Producto;
import cafeteria.snack;
import Tienda.Articulo;
import Tienda.tienda;
import Tienda.Venta;
import usuarios.Socio;

public class PruebasCafeteriaYTienda {

	public static void main(String[] args) {

		Scanner scanner = new Scanner(System.in);

		tienda tienda = new tienda();
		Cafeteria cafeteria = new Cafeteria();

		tienda.agregarArticulo(
				new Articulo("Camiseta CDS", "Ropa", 50000, 20));

		tienda.agregarArticulo(
				new Articulo("Balon de futbol", "Deportes", 80000, 10));

		tienda.agregarArticulo(
				new Articulo("Botella deportiva", "Accesorios", 30000, 15));

		cafeteria.agregarProducto(
				new bebida("Cafe", 5000, 20, true));

		cafeteria.agregarProducto(
				new bebida("Gaseosa", 4000, 20, false));

		cafeteria.agregarProducto(
				new snack("Sandwich", 10000, 10, "Gluten, lacteos"));

		cafeteria.agregarProducto(
				new snack("Fruta", 6000, 15, "Ninguno"));

		Socio socio = new Socio(
				"sebastian",
				"1234",
				"Sebastian",
				0);

		int opcion = -1;

		while (opcion != 0) {

			System.out.println();
			System.out.println("================================");
			System.out.println(" CLUB DEPORTIVO SENECA");
			System.out.println("================================");
			System.out.println("1. Entrar a la tienda");
			System.out.println("2. Entrar a la cafeteria");
			System.out.println("0. Salir");
			System.out.print("Seleccione una opcion: ");

			opcion = scanner.nextInt();
			scanner.nextLine();

			switch (opcion) {

			case 1:
				menuTienda(scanner, tienda, socio);
				break;

			case 2:
				menuCafeteria(scanner, cafeteria);
				break;

			case 0:
				System.out.println("Hasta luego.");
				break;

			default:
				System.out.println("Opcion invalida.");
			}
		}

		scanner.close();
	}

	private static void menuTienda(
			Scanner scanner,
			tienda tienda,
			Socio socio) {

		int opcion = -1;

		while (opcion != 0) {

			System.out.println();
			System.out.println("========== TIENDA ==========");
			System.out.println("1. Ver inventario");
			System.out.println("2. Comprar articulo");
			System.out.println("3. Reabastecer articulo");
			System.out.println("0. Volver");
			System.out.print("Seleccione una opcion: ");

			opcion = scanner.nextInt();
			scanner.nextLine();

			switch (opcion) {

			case 1:

				tienda.mostrarInventario();

				break;

			case 2:

				System.out.print("Nombre del articulo: ");
				String nombre = scanner.nextLine();

				System.out.print("Cantidad: ");
				int cantidad = scanner.nextInt();
				scanner.nextLine();

				Articulo articulo = tienda.buscarArticulo(nombre);

				if (articulo == null) {
					System.out.println("El articulo no existe.");
				}
				else if (cantidad <= 0 || cantidad > articulo.getCantidad()) {
					System.out.println("Cantidad no disponible.");
				}
				else {

					Venta venta = tienda.crearVenta(socio);

					venta.agregarDetalle(articulo, cantidad);
					venta.calcularTotal();

					System.out.println();
					System.out.println(venta);
				}

				break;

			case 3:

				System.out.print("Articulo: ");
				String articuloNombre = scanner.nextLine();

				System.out.print("Cantidad a agregar: ");
				int cantidadAgregar = scanner.nextInt();
				scanner.nextLine();

				tienda.reabastecer(articuloNombre, cantidadAgregar);

				System.out.println("Inventario actualizado.");

				break;

			case 0:
				break;

			default:
				System.out.println("Opcion invalida.");
			}
		}
	}

	private static void menuCafeteria(
			Scanner scanner,
			Cafeteria cafeteria) {

		int opcion = -1;

		while (opcion != 0) {

			System.out.println();
			System.out.println("======== CAFETERIA ========");
			System.out.println("1. Ver productos");
			System.out.println("2. Comprar producto");
			System.out.println("3. Reabastecer producto");
			System.out.println("0. Volver");
			System.out.print("Seleccione una opcion: ");

			opcion = scanner.nextInt();
			scanner.nextLine();

			switch (opcion) {

			case 1:

				cafeteria.mostrarMenu();

				break;

			case 2:

				System.out.print("Nombre del producto: ");
				String nombre = scanner.nextLine();

				System.out.print("Cantidad: ");
				int cantidad = scanner.nextInt();
				scanner.nextLine();

				Producto producto =
						cafeteria.buscarProducto(nombre);

				if (producto == null) {
					System.out.println("El producto no existe.");
				}
				else if (cantidad <= 0 ||
						 cantidad > producto.getCantidad()) {

					System.out.println("Cantidad no disponible.");
				}
				else {

					boolean vendido =
							cafeteria.venderProducto(nombre, cantidad);

					if (vendido) {
						System.out.println("Compra realizada.");
						System.out.println(
								"Producto: " + producto.getNombre());
						System.out.println(
								"Cantidad: " + cantidad);
						System.out.println(
								"Total: $" +
								(producto.getPrecio() * cantidad));
					}
				}

				break;

			case 3:

				System.out.print("Producto: ");
				String productoNombre = scanner.nextLine();

				System.out.print("Cantidad a agregar: ");
				int cantidadAgregar = scanner.nextInt();
				scanner.nextLine();

				cafeteria.reabastecer(
						productoNombre,
						cantidadAgregar);

				System.out.println("Inventario actualizado.");

				break;

			case 0:
				break;

			default:
				System.out.println("Opcion invalida.");
			}
		}
	}
}