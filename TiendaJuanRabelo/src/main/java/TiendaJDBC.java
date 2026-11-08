
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

}