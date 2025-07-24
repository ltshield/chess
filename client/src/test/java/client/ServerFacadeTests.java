package client;

import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;
import service.RegisterRequest;
import service.UserService;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade("http://localhost:" + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    @Test
    public void clearTest() {

        try {
            serverFacade.clear();
            assertTrue(true);
        } catch (Exception e) {
            System.out.println(e);
            fail();
        }

    }

}
