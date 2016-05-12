package arhangel.dim.core.store;

import arhangel.dim.container.beans.Car;
import arhangel.dim.core.User;
import arhangel.dim.lections.objects.LoaderDemo;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import arhangel.dim.container.Container;

/**
 *
 */
public class UserStoreTest {

    static UserOperations userOperations;

    @BeforeClass
    public static void init() {
        try {
            Container container = new Container("server.xml");
            userOperations = new UserOperations();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void addUser() throws Exception {
        User user = userOperations.addUser("lolita", "12321");
        Assert.assertTrue(user != null);

    }

    @Test
    public void getUser() throws Exception {
        User user = userOperations.getUser("lolita", "12321");
        Assert.assertTrue(user != null);
        user = userOperations.addUser("lolita", "12");
        Assert.assertTrue(user == null);
    }

    @Test
    public void getUserById() throws Exception {
        User user = userOperations.getUserById(Long.valueOf(2));
        Assert.assertTrue(user != null);
        user = userOperations.getUserById(Long.valueOf(120));
        Assert.assertTrue(user == null);
    }

    @Test
    public void removeUser() throws Exception {

    }
}