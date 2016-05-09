package arhangel.dim.container;

import arhangel.dim.client.Client;
import arhangel.dim.core.net.StringProtocol;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;


public class ConfigTest {

    private static Container container;

    private static Client expectedClent;
    private static StringProtocol expectedStringProtocol;

    @BeforeClass
    public static void init() {
        try {
            container = new Container("client.xml");
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
        Assert.assertTrue(container != null);
        expectedStringProtocol = new StringProtocol();


        expectedClent = new Client();
        expectedClent.setHost("localhost");
        expectedClent.setPort(19000);
        expectedClent.setProtocol(expectedStringProtocol);

    }

    @Test
    public void testGetByName() throws Exception {
        Client client = (Client) container.getByName("client");
        Assert.assertTrue(client != null);
        System.out.println(client.getHost() + client.getPort() + client.getProtocol().toString());
        Assert.assertEquals(client, expectedClent);
    }

    @Test
    public void testGetByClass() throws Exception {
        Client client = (Client) container.getByClass("arhangel.dim.client.Client");
        Assert.assertTrue(client != null);
        Assert.assertEquals(expectedClent, client);

    }
}

