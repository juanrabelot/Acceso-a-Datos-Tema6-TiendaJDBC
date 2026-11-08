
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

}