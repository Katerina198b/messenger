package arhangel.dim.core.store;

import arhangel.dim.container.beans.Car;
import arhangel.dim.core.User;
import arhangel.dim.lections.objects.LoaderDemo;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import arhangel.dim.container.Container;

import java.sql.Connection;
import java.sql.DriverManager;


public class UserStoreTest {

    static UserOperations userOperations;
    static Connection connection;



    @BeforeClass
    public static void init() {
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection("jdbc:postgresql://178.62.140.149:5432/Katerina198b",
                    "trackuser", "trackuser");
            connection.setAutoCommit(false);
            userOperations = new UserOperations(connection);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void addUser() throws Exception {

        User user = userOperations.addUser("lltr", "12321");
        Assert.assertTrue(user != null);

    }

    @Test
    public void getUser() throws Exception {
        User user = userOperations.getUser("lolita", "12321");
        Assert.assertTrue(user != null);
        user = userOperations.getUser("lol", "12");
        Assert.assertTrue(user == null);
    }

    @Test
    public void getUserById() throws Exception {
        User user = userOperations.getUserById(Long.valueOf(2));
        Assert.assertTrue(user != null);
        user = userOperations.getUserById(Long.valueOf(120));
        Assert.assertTrue(user == null);
    }

    @AfterClass
    public static void close() throws Exception {
        connection.close();
    }

}
