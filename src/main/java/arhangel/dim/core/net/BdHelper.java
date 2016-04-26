package arhangel.dim.core.net;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import arhangel.dim.core.User;

public class BdHelper {
    public static void main(String[] argv) throws SQLException, ClassNotFoundException {

        Class.forName("org.postgresql.Driver");
        Connection connection = DriverManager.getConnection("jdbc:postgresql://178.62.140.149:5432/Katerina198b",
                "trackuser", "trackuser");

        Statement stmt = connection.createStatement();
        String sql = "INSERT INTO \"user\" (ID,login,password) VALUES (1, 'Paul', 'Calia');";
        stmt.executeUpdate(sql);
        stmt.close();
        stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM \"user\";");
        while (rs.next()) {
            int id = rs.getInt("id");
            String name = rs.getString("login");
            System.out.println("ID = " + id);
            System.out.println("NAME = " + name);
            System.out.println();

        }
    }
}
