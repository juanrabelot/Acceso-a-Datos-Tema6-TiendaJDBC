import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
public class TiendaJDBC {
        public static void main(String[] args) {
            try (Connection con =
                         DriverManager.getConnection(
                                 "jdbc:h2:mem:tienda",
                                 "sa",
                                 "")) {
                System.out.println("Conexión realizada");
                crearTablas(con);
                insertarProductos(con);
                consultas(con);
                modificaciones(con);
                transacciones(con);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    private static void crearTablas(Connection con) throws SQLException {
        Statement st = con.createStatement();
        //elimino las tablas si existen
        st.executeUpdate("DROP TABLE IF EXISTS VENTA");

        st.executeUpdate("DROP TABLE IF EXISTS PRODUCTO");
        //creo el producto
        st.executeUpdate(
                "CREATE TABLE PRODUCTO (" +
                        "ID INT PRIMARY KEY, " +
                        "NOMBRE VARCHAR(100) NOT NULL, " +
                        "CATEGORIA VARCHAR(50) NOT NULL, " +
                        "PRECIO DECIMAL(10,2) NOT NULL CHECK(PRECIO > 0), " +
                        "STOCK INT NOT NULL CHECK(STOCK >= 0)" +
                        ")"
        );
        //creo la venta
        st.executeUpdate(
                "CREATE TABLE VENTA (" +
                        "IDVENTA INT PRIMARY KEY, " +
                        "IDPRODUCTO INT NOT NULL, " +
                        "CANTIDAD INT NOT NULL CHECK(CANTIDAD > 0), " +
                        "FOREIGN KEY (IDPRODUCTO) REFERENCES PRODUCTO(ID)" +
                        ")"
        );
        System.out.println("Tablas creadas correctamente");

    }
    private static void insertarProductos(Connection con) throws SQLException {
        Statement st = con.createStatement();

        int total = 0;

        total += st.executeUpdate(
                "INSERT INTO PRODUCTO VALUES (1,'Portátil Lenovo','Portátiles',799.99,10)"
        );
        total += st.executeUpdate(
                "INSERT INTO PRODUCTO VALUES (2,'Monitor 27 pulgadas','Monitores',249.99,0)"
        );

        total += st.executeUpdate(
                "INSERT INTO PRODUCTO VALUES (3,'Teclado mecánico','Periféricos',59.95,25)"
        );

        total += st.executeUpdate(
                "INSERT INTO PRODUCTO VALUES (4,'Ratón inalámbrico','Periféricos',19.99,30)"
        );
        total += st.executeUpdate(
                "INSERT INTO PRODUCTO VALUES (5,'Webcam HD','Periféricos',34.50,4)"
        );
        total += st.executeUpdate(
                "INSERT INTO PRODUCTO VALUES (6,'SSD 1TB','Componentes',89.90,12)"
        );
        total += st.executeUpdate(
                "INSERT INTO PRODUCTO VALUES (7,'Memoria USB 64 GB','Periféricos',9.99,0)"
        );
        total += st.executeUpdate(
                "INSERT INTO PRODUCTO VALUES (8,'Portátil HP','Portátiles',699.00,5)"
        );
        System.out.println("Productos insertados: " + total);
    }
    private static void mostrar(Connection con,
                                String titulo,
                                String sql) throws SQLException {
        System.out.println(titulo);
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {

            System.out.println(
                    rs.getString("NOMBRE")
                            + " | "
                            + rs.getDouble("PRECIO")
                            + " €"
            );
        }
    }
    private static void consultas(Connection con) throws SQLException {
        mostrar(
                con,
                "Productos de mayor a menor precio",
                """
                SELECT NOMBRE, PRECIO
                FROM PRODUCTO
                ORDER BY PRECIO DESC
                """);
        mostrar(con,
                "Periféricos por debajo de 50 euros",
                """
                SELECT NOMBRE, PRECIO
                FROM PRODUCTO
                WHERE CATEGORIA = 'Periféricos'
                AND PRECIO < 50
                ORDER BY NOMBRE
                """);
        mostrar(con,
                "Productos que empiezan por Monitor",
                """
                SELECT NOMBRE, PRECIO
                FROM PRODUCTO
                WHERE NOMBRE LIKE 'Monitor%'
                ORDER BY NOMBRE
                """);
        mostrar(con,
                "Productos entre 50 y 200 euros",
                """
                SELECT NOMBRE, PRECIO
                FROM PRODUCTO
                WHERE PRECIO BETWEEN 50 AND 200
                ORDER BY PRECIO
                """);
        mostrar(con,
                "Monitores o portátiles con stock",
                """
                SELECT NOMBRE, PRECIO
                FROM PRODUCTO
                WHERE CATEGORIA IN ('Monitores', 'Portátiles')
                AND STOCK > 0
                ORDER BY NOMBRE
                """
        );
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("""
        SELECT COUNT(*) AS TOTAL
        FROM PRODUCTO
        WHERE STOCK = 0
        """);
        if (rs.next()) {
            System.out.println("\nProductos agotados: ");
            System.out.println("Total: " + rs.getInt("TOTAL"));
        }
    }
    private static void modificaciones(Connection con) throws SQLException {
        Statement st = con.createStatement();
        int actualizados = st.executeUpdate("""
        UPDATE PRODUCTO
        SET PRECIO = ROUND(PRECIO * 1.10, 2)
        WHERE CATEGORIA = 'Periféricos'
        """);
        System.out.println("Productos actualizados: " + actualizados);
        int eliminados = st.executeUpdate("""
        DELETE FROM PRODUCTO
        WHERE STOCK = 0
        """);
        System.out.println("Productos eliminados: " + eliminados);
    }
    private static void transacciones(Connection con) throws SQLException {
        System.out.println("TRANSACCIONES:");
        registrarVenta(con, 1, 3, 2);
        registrarVenta(con, 2, 5, 10);
    }
    private static void registrarVenta(Connection con,
                                       int idVenta,
                                       int idProducto,
                                       int cantidad) {
        try {
            Statement st = con.createStatement();
            st.executeUpdate(
                    "INSERT INTO VENTA VALUES (" +
                            idVenta + "," +
                            idProducto + "," +
                            cantidad + ")"
            );
            st.executeUpdate(
                    "UPDATE PRODUCTO " +
                            "SET STOCK = STOCK - " + cantidad +
                            " WHERE ID = " + idProducto
            );
            con.commit();
            System.out.println(
                    "Venta " + idVenta +
                            " realizada correctamente"
            );
            con.setAutoCommit(false);
        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.out.println(
                    "Venta " + idVenta +
                            " cancelada"
            );
        }
        finally {
            try {
                con.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}